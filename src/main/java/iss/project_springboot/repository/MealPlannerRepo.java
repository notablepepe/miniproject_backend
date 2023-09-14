package iss.project_springboot.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class MealPlannerRepo {
    
    @Autowired
    JdbcTemplate template;

    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    
    public boolean existsByUsernameAndRecipeId(String username, Integer recipeId) {
        String sql = "SELECT COUNT(*) FROM favorites WHERE username = ? AND recipe_id = ?";
        int count = template.queryForObject(sql, Integer.class, username, recipeId);
        return count > 0;
    }

    public void addRecipeToFavorites(String username, Integer recipeId) {
        if (existsByUsernameAndRecipeId(username,recipeId)) {
            // update the existing record by appending the new recipeId if not in array
            String sql = "INSERT INTO favorites (username, recipe_id) VALUES (?, ?)";
            template.update(sql, username, recipeId);
        } else {
            // insert a new record with the username and an array containing the recipeId
            String insertSql = "INSERT INTO favorites (username, recipe_id) VALUES (?, ?)";
            template.update(insertSql, username, recipeId);
        }
    }

    
    public List<Integer> getFavoritesFromUser(String username) {
    
        String sql = "SELECT recipe_id FROM favorites WHERE username = ?";
        List<Map<String, Object>> rows = template.queryForList(sql, username);

        List<Integer> favoriteRecipeIds = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Integer recipeId = (Integer) row.get("recipe_id");
            favoriteRecipeIds.add(recipeId);
        }

        return favoriteRecipeIds;
    
    }

    
    public void deleteFavoriteFromUser(String username, Integer recipeId) {
        String sql = "DELETE FROM favorites WHERE username = ? AND recipe_id = ?";
        if (existsByUsernameAndRecipeId(username,recipeId)) {
            template.update(sql, username, recipeId);
        }
    }

    public Boolean registerUser(String username, String password, String email) {
        String usernameQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        int usernameCount = template.queryForObject(usernameQuery, Integer.class, username);

        // Check if the email already exists
        String emailQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
        int emailCount = template.queryForObject(emailQuery, Integer.class, email);

        if (emailCount > 0) {
            System.out.println("Email is already registered.");
            return false;
        }

        if (usernameCount > 0 || emailCount > 0) {
            System.out.println("Username is already taken.");
            return false;
        }

        String insertQuery = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = passwordEncoder.encode(password);

        template.update(insertQuery, username, hashedPassword, email);

        System.out.println("User registered successfully.");
        return true;
    }


    public Boolean authenticateUser(String username, String password) {
        String countQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        int userCount = template.queryForObject(countQuery, Integer.class, username);

        if (userCount == 0) {
            return false;
        }

        String selectQuery = "SELECT password FROM users WHERE username = ?";
        String hashedPassword = template.queryForObject(selectQuery, String.class, username);

        if(passwordEncoder.matches(password, hashedPassword)) {
            System.out.println("password correct");
            return true;
        }

        else {
            System.out.println("password wrong");
            return false;
        }    
    }


}
