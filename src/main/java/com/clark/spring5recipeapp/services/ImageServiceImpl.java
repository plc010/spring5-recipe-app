package com.clark.spring5recipeapp.services;

import com.clark.spring5recipeapp.domain.Recipe;
import com.clark.spring5recipeapp.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private RecipeRepository recipeRepository;

    public ImageServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void saveImageFile(Long recipeId, MultipartFile file) {
        try {
            log.debug("Received image file");


            Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

            if (recipeOptional.isPresent()) {
                log.debug("Found recipe by ID: " + recipeId);
                Recipe recipe = recipeOptional.get();

                Byte[] data = new Byte[file.getBytes().length];

                int i = 0;
                for (byte b : file.getBytes()) {
                    data[i++] = b;
                }

                recipe.setImage(data);
                recipeRepository.save(recipe);
            } else {
                log.error("Did not find recipe for ID: " + recipeId);
            }
        } catch (Exception e) {
            log.error("Error occurred", e);
            e.printStackTrace();
        }
    }
}
