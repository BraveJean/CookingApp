package com.cookingapp;

public class RecipesInfo {
    private String recipeName;
    private String recipeId;
    private String userId;
    private String userName;
    private String image;
    private String video;
    private String steps;
    private String ingredient;

    public RecipesInfo() {
    }

    public RecipesInfo(String recipeName, String recipeId, String userId, String userName, String image, String video, String steps, String ingredient) {
        this.recipeName = recipeName;
        this.recipeId = recipeId;
        this.userId = userId;
        this.userName = userName;
        this.image = image;
        this.video = video;
        this.steps = steps;
        this.ingredient = ingredient;
    }

    public RecipesInfo(String image) {
        this.image = image;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
