package com.brannan.yelp.reviews.restaurantinfo.rest.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.brannan.yelp.reviews.restaurantinfo.pojos.Restaurant;
import com.brannan.yelp.reviews.restaurantinfo.pojos.Review;
import com.brannan.yelp.reviews.restaurantinfo.pojos.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;

@SuppressWarnings("unchecked")
@Component("yelpRestClient")
public class YelpRestClient {

	private static WebClient client = null;
	private static final String yelpApiKey = "YELP_API_KEY";
	private static final String yelpApiStartUrl = "https://api.yelp.com/v3/businesses/";

	private final ObjectMapper objectMapper = new ObjectMapper();

	static {
		// Basic way of making sure the credentials are there
		String key = "";

		if (System.getenv(yelpApiKey) == null) {
			try (InputStream yelpIs = YelpRestClient.class.getResourceAsStream("/yelp-api.txt");) {
				key = new String(yelpIs.readAllBytes(), StandardCharsets.UTF_8);
			} catch (IOException e) {
				// Do nothing and let the client be null to throw an error later
			}
		} else {
			key = System.getenv(yelpApiKey);
		}
		client = WebClient.builder().baseUrl(yelpApiStartUrl).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).defaultHeader("Authorization", "Bearer " + key).build();
	}

	public Restaurant getRestaurant(String name, String location) {
		return getRestaurant(name, location, false);
	}

	public Restaurant getRestaurant(String name, String location, boolean pullReviews) {

		// Simple search by name and location and just use the first result
		Restaurant restaurant = searchForRestaurantByName(name, location);
		if (restaurant == null) {
			// Shortcut back out and let Provider show error
			return null;
		}

		if (pullReviews) {
			restaurant.setReviews(getReviewsByRestaurantId(restaurant.getId()));
		}

		return restaurant;
	}

	public List<Review> getReviewsByRestaurantId(String restaurantId) {
		String reviewsString = client.get().uri(restaurantId + "/reviews").retrieve().bodyToMono(String.class).block();
		List<Review> reviews = new ArrayList<>();
		try {
			// Pull review API call into a map for easier access
			Map<String, Object> m = mapifyString(reviewsString); // objectMapper.readValue(reviewsString, HashMap.class);

			reviews = objectMapper.convertValue(m.get("reviews"), new TypeReference<List<Review>>() {
			});

			for (Review review : reviews) {

				if (review.getUser().getImage_url() != null) {
					detectFaces(review.getUser());
				}

			}
			return reviews;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	// Code sample from google cloud examples
	private void detectFaces(User user) throws IOException {
		List<AnnotateImageRequest> requests = new ArrayList<>();

		ImageSource imgSource = ImageSource.newBuilder().setImageUri(user.getImage_url()).build();
		Image img = Image.newBuilder().setSource(imgSource).build();
		Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();

		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);
		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					System.out.format("Error: %s%n", res.getError().getMessage());
					return;
				}

				for (FaceAnnotation anno : res.getFaceAnnotationsList()) {
					user.setAngerLikelihood(anno.getAngerLikelihood().toString());
					user.setJoyLikelihood(anno.getJoyLikelihood().toString());
					user.setSorrowLikelihood(anno.getSorrowLikelihood().toString());
					user.setSurpriseLikelihood(anno.getSurpriseLikelihood().toString());
					user.setHeadwearLikelihood(anno.getHeadwearLikelihood().toString());
					user.setBlurredLikelihood(anno.getBlurredLikelihood().toString());
					user.setUnderExposedLikelihood(anno.getUnderExposedLikelihood().toString());
				}
			}
		}
	}

	private Restaurant searchForRestaurantByName(String name, String location) {
		String reviewsString = client.get().uri(uriBuilder -> uriBuilder.path("search").queryParam("term", name).queryParam("location", location).build()).retrieve().bodyToMono(String.class).block();
		Map<String, Object> map = mapifyString(reviewsString);
		if (map == null) {
			return null;
		}

		return objectMapper.convertValue(map.get("businesses"), new TypeReference<List<Restaurant>>() {
		}).get(0);
	}

	private Map<String, Object> mapifyString(String json) {
		try {
			return objectMapper.readValue(json, HashMap.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
