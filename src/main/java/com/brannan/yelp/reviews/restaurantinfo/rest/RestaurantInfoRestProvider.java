package com.brannan.yelp.reviews.restaurantinfo.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brannan.yelp.reviews.restaurantinfo.pojos.Restaurant;

@RestController
class RestaurantInfoRestProvider {

	@GetMapping(value = "/get-reviews", produces = MediaType.APPLICATION_JSON_VALUE)
	public Restaurant getRestaurantById() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId("AGbDnpKNHPcPBbbc9GycSw");
		return restaurant;
	}

}