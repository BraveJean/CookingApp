package com.cookingapp;

public class Rating {

    private String rating;

    /**
     * the person who give a rating of this recipe
     */
    private String ratingUser;

    /**
     * The id of recipe
     */
    private String recipeId;

    /**
     * the author id of this recipe
     */
    private String userId;


    public Rating() {
    }

    public Rating(String rating, String ratingUser, String recipeId, String userId) {
        this.rating = rating;
        this.ratingUser = ratingUser;
        this.recipeId = recipeId;
        this.userId = userId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRatingUser() {
        return ratingUser;
    }

    public void setRatingUser(String ratingUser) {
        this.ratingUser = ratingUser;
    }
}
