package iss.project_springboot.model;

public class FavoriteRecipe {
    
    private String username;
    private Integer recipeId;
    
    public FavoriteRecipe() {
    }

    public FavoriteRecipe(String username, Integer recipeId) {
        this.username = username;
        this.recipeId = recipeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRecipeId() {
        return recipeId;
    }
    
    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    
}
