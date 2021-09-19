package com.brannan.yelp.reviews.restaurantinfo.rest.provider;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.brannan.yelp.reviews.restaurantinfo.pojos.Restaurant;
import com.brannan.yelp.reviews.restaurantinfo.pojos.Review;
import com.brannan.yelp.reviews.restaurantinfo.rest.client.YelpRestClient;

@RestController
class RestaurantInfoRestProvider {

	@Autowired
	YelpRestClient yelpRestClient;

	@GetMapping(value = "/get-restaurant/{restaurantId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Restaurant getRestaurantById(@PathVariable final String restaurantId) {

		Restaurant restaurant = yelpRestClient.getRestaurant(restaurantId, true);

		return restaurant;

	}

	@GetMapping(value = "/get-reviews/{restaurantId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Review> getReviewsByRestaurantId(@PathVariable final String restaurantId) {

		List<Review> reviews = yelpRestClient.getReviewsByRestaurantId(restaurantId);

		return reviews;

	}

}