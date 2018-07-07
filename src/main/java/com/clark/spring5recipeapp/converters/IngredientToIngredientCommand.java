package com.clark.spring5recipeapp.converters;

import com.clark.spring5recipeapp.commands.IngredientCommand;
import com.clark.spring5recipeapp.domain.Ingredient;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class IngredientToIngredientCommand implements Converter<Ingredient, IngredientCommand> {

    private UnitOfMeasureToUnitOfMeasureCommand converter;

    public IngredientToIngredientCommand(UnitOfMeasureToUnitOfMeasureCommand converter) {
        this.converter = converter;
    }

    @Synchronized
    @Nullable
    @Override
    public IngredientCommand convert(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }

        final IngredientCommand command = new IngredientCommand();
        command.setId(ingredient.getId());
        command.setAmount(ingredient.getAmount());
        command.setDescription(ingredient.getDescription());
        command.setUom(converter.convert(ingredient.getUom()));
        return command;
    }
}
