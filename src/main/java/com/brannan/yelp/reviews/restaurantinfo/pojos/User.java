package com.brannan.yelp.reviews.restaurantinfo.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	private String id;
	private String profile_url;
	private String image_url;
	private String name;

	private String joyLikelihood;
	private String sorrowLikelihood;
	private String angerLikelihood;
	private String surpriseLikelihood;
	private String underExposedLikelihood;
	private String blurredLikelihood;
	private String headwearLikelihood;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProfile_url() {
		return profile_url;
	}

	public void setProfile_url(String profile_url) {
		this.profile_url = profile_url;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJoyLikelihood() {
		return joyLikelihood;
	}

	public void setJoyLikelihood(String joyLikelihood) {
		this.joyLikelihood = joyLikelihood;
	}

	public String getSorrowLikelihood() {
		return sorrowLikelihood;
	}

	public void setSorrowLikelihood(String sorrowLikelihood) {
		this.sorrowLikelihood = sorrowLikelihood;
	}

	public String getAngerLikelihood() {
		return angerLikelihood;
	}

	public void setAngerLikelihood(String angerLikelihood) {
		this.angerLikelihood = angerLikelihood;
	}

	public String getSurpriseLikelihood() {
		return surpriseLikelihood;
	}

	public void setSurpriseLikelihood(String surpriseLikelihood) {
		this.surpriseLikelihood = surpriseLikelihood;
	}

	public String getUnderExposedLikelihood() {
		return underExposedLikelihood;
	}

	public void setUnderExposedLikelihood(String underExposedLikelihood) {
		this.underExposedLikelihood = underExposedLikelihood;
	}

	public String getBlurredLikelihood() {
		return blurredLikelihood;
	}

	public void setBlurredLikelihood(String blurredLikelihood) {
		this.blurredLikelihood = blurredLikelihood;
	}

	public String getHeadwearLikelihood() {
		return headwearLikelihood;
	}

	public void setHeadwearLikelihood(String headwearLikelihood) {
		this.headwearLikelihood = headwearLikelihood;
	}

}