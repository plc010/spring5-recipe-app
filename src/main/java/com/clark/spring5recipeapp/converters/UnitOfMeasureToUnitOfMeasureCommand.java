package com.clark.spring5recipeapp.converters;

import com.clark.spring5recipeapp.commands.UnitOfMeasureCommand;
import com.clark.spring5recipeapp.domain.UnitOfMeasure;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class UnitOfMeasureToUnitOfMeasureCommand implements Converter<UnitOfMeasure, UnitOfMeasureCommand> {


    @Synchronized
    @Nullable
    @Override
    public UnitOfMeasureCommand convert(UnitOfMeasure unitOfMeasure) {
        if (unitOfMeasure != null) {
            final UnitOfMeasureCommand command = new UnitOfMeasureCommand();
            command.setId(unitOfMeasure.getId());
            command.setDescription(unitOfMeasure.getDescription());
            return command;
        }
        return null;
    }
}
