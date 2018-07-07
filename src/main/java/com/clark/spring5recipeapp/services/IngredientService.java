package com.clark.spring5recipeapp.services;

import com.clark.spring5recipeapp.commands.IngredientCommand;

public interface IngredientService {
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId ,Long ingredientId);
}
