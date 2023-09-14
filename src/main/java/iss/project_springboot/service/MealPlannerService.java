package iss.project_springboot.service;

import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import iss.project_springboot.model.EmailDetails;
import iss.project_springboot.model.LoginDetails;
import iss.project_springboot.model.User;
import iss.project_springboot.repository.MealPlannerRepo;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class MealPlannerService {
    
    @Autowired
    private MealPlannerRepo repo;

    @Autowired
    private EmailInterface emailInterface;

    private RestTemplate template = new RestTemplate();

    //To hide later
    @Value("${spoonkey}")
    String apiKey;

    

    public JsonArray getRecipes(Integer caloriesMin,Integer caloriesMax, Integer protein, Integer carbs, Integer fat) {
        
        //To hide later
        String url = "https://api.spoonacular.com/recipes/complexSearch";

        String uri = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("apiKey", apiKey)
                    .queryParam("number", 6) // To be edited later, might also need to add in query
                    .queryParam("minCalories", caloriesMin)
                    .queryParam("maxCalories", caloriesMax)
                    .queryParam("minProtein", protein)
                    .queryParam("minCarbs", carbs)
                    .queryParam("minFat", fat)
                    .toUriString();

        ResponseEntity<String> response =  template.getForEntity(uri, String.class);
        JsonReader reader = Json.createReader((new StringReader(response.getBody())));

        JsonObject result = reader.readObject();
        JsonArray recipeArray = result.getJsonArray("results");
        //testing 
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> before rebuilding");
        System.out.println(recipeArray);

        //Rebuilding the json object in the recipe array so it is more friendly
        JsonArrayBuilder finalResults = Json.createArrayBuilder();

        for(JsonValue recipe: recipeArray) {
            JsonObject recipeObj = recipe.asJsonObject();
            Integer id = recipeObj.getInt("id");
            String name = recipeObj.getString("title");
            String imgUrl = recipeObj.getString("image");
            JsonObject nutrition = recipeObj.getJsonObject("nutrition");
            JsonArray nutritionArray = nutrition.getJsonArray("nutrients");

            JsonObject finalObj = Json.createObjectBuilder()
                                 .add("id", id)
                                 .add("name", name)
                                 .add("imgUrl", imgUrl)
                                 .add("nutrition", nutritionArray)
                                 .build();

            finalResults.add(finalObj);

        }
        System.out.println(">>>>>>>>>>>>>>>>>>>> after rebuilding");
        System.out.println(finalResults);
        return finalResults.build();
    }

    public JsonObject getRecipeDetails(Integer id) {

        String url = "https://api.spoonacular.com/recipes/" + id + "/information";
        System.out.println(">>>>>>>>>>Url is " + url);
        String uri = UriComponentsBuilder
                        .fromUriString(url)
                        .queryParam("apiKey", apiKey)
                        .queryParam("includeNutrition", true)
                        .toUriString();

        ResponseEntity<String> response = template.getForEntity(uri, String.class);
        JsonReader reader = Json.createReader((new StringReader(response.getBody())));
        JsonObject result = reader.readObject();

        Integer recipeId = result.getInt("id");
        String name = result.getString("title");
        Integer readyInMinutes = result.getInt("readyInMinutes");
        Integer servings = result.getInt("servings");
        String imgUrl = result.getString("image");
        String description = result.getString("summary");
        String instructions = result.getString("instructions");
        
        JsonArray ingredientsArrayUnfiltered = result.getJsonArray("extendedIngredients");
        JsonArrayBuilder ingredientsArrayFiltered = Json.createArrayBuilder();
        for(JsonValue ingredient : ingredientsArrayUnfiltered) {
            JsonObject ingredientObj = ingredient.asJsonObject();
            String ingredientMeasurement = ingredientObj.getString("original");
            JsonObject finalIngredientObj = Json.createObjectBuilder()
                                            .add("ingredientDetails", ingredientMeasurement)
                                            .build();
            ingredientsArrayFiltered.add(finalIngredientObj);
        }

        JsonArray nutrientsArrayUnfiltered = result.getJsonObject("nutrition").getJsonArray("nutrients");
        JsonArrayBuilder nutrientsArrayFiltered = Json.createArrayBuilder();
        for(JsonValue nutrient : nutrientsArrayUnfiltered) {
            JsonObject nutrientObj = nutrient.asJsonObject();
            String macro = nutrientObj.getString("name");
            if(macro.equals("Calories") || macro.equals("Fat") || macro.equals("Carbohydrates") || macro.equals("Protein")) {
                nutrientsArrayFiltered.add(nutrientObj);
            }
        }

        JsonObject finalObj = Json.createObjectBuilder()
                             .add("id", recipeId)
                             .add("name", name)
                             .add("duration", readyInMinutes)
                             .add("servings", servings)
                             .add("image", imgUrl)
                             .add("summary", description)
                             .add("instructions", instructions)
                             .add("ingredients", ingredientsArrayFiltered)
                             .add("nutrients", nutrientsArrayFiltered)
                             .add("extendedIngredients", ingredientsArrayUnfiltered)
                             .build();


        
        //Testing recipe details output
        System.out.println(">>>>>>>>>>>>>>Getting recipe details for id number: " + id);
        System.out.println(finalObj);

        return finalObj;
    }

    public void addToFavorites(String username, Integer recipeId) {
        this.repo.addRecipeToFavorites(username,recipeId);
    }

    public List<Integer> getFavoritesFromUser(String username) {
        return this.repo.getFavoritesFromUser(username);
    }

    public void deleteFavoriteFromUser(String username, Integer recipeId) {
        this.repo.deleteFavoriteFromUser(username,recipeId);
    }

    public Boolean registerUser(User payload) {
        String username = payload.getUsername();
        String password = payload.getPassword();
        String email = payload.getEmail();
        Boolean successfullyRegistered = this.repo.registerUser(username,password,email);
        if(successfullyRegistered) {
            String receipient = email;
            String subject = "Meal Planner Account Registration";
            String message = "Hi " + username + ", \n Your Account has be successfully registered on MealPlanner";
            emailInterface.sendSimpleMail(new EmailDetails(receipient, message , subject));
            return true;
        }
        return false;
    }

    public Boolean authenticateUser(LoginDetails payload) {
        String username = payload.getUsername();
        String password = payload.getPassword();
        return this.repo.authenticateUser(username, password);
    }
}
