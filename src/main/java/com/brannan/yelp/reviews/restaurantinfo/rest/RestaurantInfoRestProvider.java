package com.brannan.yelp.reviews.restaurantinfo.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
class RestaurantInfoRestProvider {

	@GetMapping( value = "/get-reviews", produces = MediaType.APPLICATION_JSON_VALUE )
	public String one() {
		return null;
	}

}