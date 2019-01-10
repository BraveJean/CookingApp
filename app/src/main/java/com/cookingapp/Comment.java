package com.cookingapp;

public class Comment {

    private String comment;
    private String commentUser;
    private String recipeId;
    private String userId;


    public Comment() {
    }

    public Comment(String comment, String commentUser, String recipeId, String userId) {
        this.comment = comment;
        this.commentUser = commentUser;
        this.recipeId = recipeId;
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
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
}
