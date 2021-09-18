package com.brannan.yelp.reviews.restaurantinfo.rest.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.brannan.yelp.reviews.restaurantinfo.pojos.Restaurant;
import com.brannan.yelp.reviews.restaurantinfo.pojos.Review;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unchecked")
@Component("yelpRestClient")
public class YelpRestClient {

	private final WebClient client = WebClient.builder().baseUrl("https://api.yelp.com/v3/businesses/").defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader("Authorization", "Bearer 9D9YHrsZwcY6lfmzgB-a4lwkb6SCPzQE6qgh3zorgcDuKoasVnBDB0Jeputhh635_C7det8C-ao3K4g4QNeKWjgdqcsOk4GCUjkS0w3zdX6S-r6Duvve-TgcFQxFYXYx") // Don't steal me!
			.build();

	private final ObjectMapper objectMapper = new ObjectMapper();

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

		try {
			// Pull review API call into a map for easier access
			HashMap<String, Object> m = objectMapper.readValue(reviewsString, HashMap.class);

			return objectMapper.convertValue(m.get("reviews"), new TypeReference<List<Review>>() {
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

}
