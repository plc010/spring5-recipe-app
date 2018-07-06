package com.clark.spring5recipeapp.services;

import com.clark.spring5recipeapp.domain.Recipe;

import java.util.Set;

public interface RecipeService {

    Set<Recipe> getRecipes();
}
