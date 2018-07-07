package com.clark.spring5recipeapp.services;

import com.clark.spring5recipeapp.commands.IngredientCommand;
import com.clark.spring5recipeapp.converters.IngredientCommandToIngredient;
import com.clark.spring5recipeapp.converters.IngredientToIngredientCommand;
import com.clark.spring5recipeapp.domain.Ingredient;
import com.clark.spring5recipeapp.domain.Recipe;
import com.clark.spring5recipeapp.repositories.RecipeRepository;
import com.clark.spring5recipeapp.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;


    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand, IngredientCommandToIngredient ingredientCommandToIngredient, RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);

        if (!optionalRecipe.isPresent()) {
            // TODO - add error handling
            log.error("Recipe ID not found. ID: " + recipeId);
        }

        Recipe recipe = optionalRecipe.get();
        Optional<IngredientCommand> optionalIngredientCommand = recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map(ingredientToIngredientCommand::convert).findFirst();

        if (!optionalIngredientCommand.isPresent()) {
            // TODO - add error handling
            log.error("Ingredient ID not found. ID: " + ingredientId);
        }

        return optionalIngredientCommand.get();
    }

    @Override
    @Transactional
    public IngredientCommand saveIngredientCommand(IngredientCommand command) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(command.getRecipeId());
        if (!optionalRecipe.isPresent()) {
            // TODO - throw error if not found
            log.error("Recipe not found for ID: " + command.getDescription());
            return new IngredientCommand();
        }

        Recipe recipe = optionalRecipe.get();
        Optional<Ingredient> optionalIngredient = recipe
                .getIngredients()
                .stream()
                .filter(ingredient -> ingredient.getId().equals(command.getId()))
                .findFirst();

        if (optionalIngredient.isPresent()) {
            Ingredient ingredientFound = optionalIngredient.get();
            ingredientFound.setDescription(command.getDescription());
            ingredientFound.setAmount(command.getAmount());
            ingredientFound.setUom(unitOfMeasureRepository
                .findById(command.getUom().getId())
                .orElseThrow(() -> new RuntimeException("UOM Not found"))); // TODO - address this
        } else {
            // add new ingredient
            recipe.addIngredient(ingredientCommandToIngredient.convert(command));
        }

        Recipe savedRecipe = recipeRepository.save(recipe);

        Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                .findFirst();

        if (!savedIngredientOptional.isPresent()) {
            savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
                    .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
                    .filter(recipeIngredient -> recipeIngredient.getUom().getId().equals(command.getUom().getId()))
                    .findFirst();
        }

        // todo check for fail
        return ingredientToIngredientCommand.convert(savedIngredientOptional.get());
    }

    @Override
    public void deleteById(Long recipeId, Long id) {
        log.debug("Deleting ingredient: " + recipeId + ":" + id);

        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);

        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            log.debug("found recipe");

            Optional<Ingredient> optionalIngredient = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(id))
                    .findFirst();

            if (optionalIngredient.isPresent()) {
                log.debug("found ingredient");
                Ingredient ingredientToDelete = optionalIngredient.get();
                ingredientToDelete.setRecipe(null);
                recipe.getIngredients().remove(optionalIngredient.get());
                recipeRepository.save(recipe);
            } else {
                log.debug("Recipe ID not found. ID: " + recipeId);
            }
        }
    }
}
