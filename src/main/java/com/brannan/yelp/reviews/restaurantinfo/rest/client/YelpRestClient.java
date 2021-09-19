package com.brannan.yelp.reviews.restaurantinfo.rest.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	public Restaurant getRestaurant(String id) {
		return getRestaurant(id, false);
	}

	public Restaurant getRestaurant(String restaurantId, boolean pullReviews) {
		Restaurant restaurant = new Restaurant();
		restaurant.setId(restaurantId);

		restaurant = client.get().uri(restaurantId).retrieve().bodyToMono(Restaurant.class).block();

		if (pullReviews) {
			restaurant.setReviews(getReviewsByRestaurantId(restaurantId));
		}

		return restaurant;
	}

	public List<Review> getReviewsByRestaurantId(String restaurantId) {
		String reviewsString = client.get().uri(restaurantId + "/reviews").retrieve().bodyToMono(String.class).block();
		List<Review> reviews = new ArrayList<>();
		try {
			// Pull review API call into a map for easier access
			HashMap<String, Object> m = objectMapper.readValue(reviewsString, HashMap.class);

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

}
