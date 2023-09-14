package iss.project_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import iss.project_springboot.model.FavoriteRecipe;
import iss.project_springboot.model.LoginDetails;
import iss.project_springboot.model.User;
import iss.project_springboot.service.MealPlannerService;


@Controller
@RequestMapping(path = "/api")
@CrossOrigin()
public class MealPlannerController {
    
    @Autowired
    private MealPlannerService service;
    
    @GetMapping(path = "/recipes")
    public ResponseEntity<String> getRecipes(@RequestParam Integer caloriesMin, @RequestParam Integer caloriesMax, @RequestParam Integer protein,
            @RequestParam Integer carbs, @RequestParam Integer fat) {
                System.out.println(">>>>>>>>>> querying for recipes");
                return ResponseEntity.ok(service.getRecipes(caloriesMin, caloriesMax, protein, carbs, fat).toString());
    }

    @GetMapping(path = "/recipes/{id}")
    public ResponseEntity<String> getRecipeDetails(@PathVariable Integer id) {
        System.out.println(">>>>>>>>>>>>> getting recipe card");
        return ResponseEntity.ok(service.getRecipeDetails(id).toString());
    }

    @PostMapping(path = "/recipes/user/favorite")
    public void addToFavorites(@RequestBody FavoriteRecipe payload) {
        System.out.println("hello im in");
        String username = payload.getUsername();
        Integer recipeId = payload.getRecipeId();
        System.out.println("Favoriting recipe: " + recipeId + " from user: " + username);
        service.addToFavorites(username, recipeId);
    }

    @GetMapping(path = "/recipes/favorites/{username}")
    public ResponseEntity<String> getFavoritesFromUser(@PathVariable String username) {
        return ResponseEntity.ok(service.getFavoritesFromUser(username).toString());
    }

    @DeleteMapping(path = "/recipes/{username}/favorites/{recipeId}")
    public ResponseEntity<String> deleteFavoriteFromUser(@PathVariable String username, @PathVariable Integer recipeId) {
        System.out.println("Im deleting");
        this.service.deleteFavoriteFromUser(username,recipeId);
        return ResponseEntity.ok("Successfully removed recipe id " + recipeId + " from " + username);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> registerUser(@RequestBody User payload) {
        System.out.println("Im registering");
        if (service.registerUser(payload)) {
            return ResponseEntity.ok("Register successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Register unsuccessful");
        }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDetails payload) {
        if (service.authenticateUser(payload)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        
    }
}
