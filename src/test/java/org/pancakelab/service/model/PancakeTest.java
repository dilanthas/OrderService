package org.pancakelab.service.model;

import org.junit.jupiter.api.Test;
import org.pancakelab.model.Ingredient;
import org.pancakelab.model.Pancake;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PancakeTest {
    @Test
    void createStandardPancake_shouldHaveCorrectBaseIngredients() {
        // Arrange & Act
        Pancake pancake = Pancake.Builder.standard().build();

        // Assert
        List<Ingredient> baseIngredients = pancake.baseIngredients();
        assertTrue(baseIngredients.contains(Ingredient.FLOUR));
        assertTrue(baseIngredients.contains(Ingredient.EGG));
        assertTrue(baseIngredients.contains(Ingredient.MILK));
        assertEquals(3, baseIngredients.size());
        assertTrue(pancake.customIngredients().isEmpty());
        assertEquals(2.25, pancake.getPrice());
    }

    @Test
    void createVeganPancake_shouldHaveCorrectBaseIngredients() {
        // Arrange & Act
        Pancake pancake = Pancake.Builder.vegan().build();

        // Assert
        List<Ingredient> baseIngredients = pancake.baseIngredients();
        assertTrue(baseIngredients.contains(Ingredient.FLOUR));
        assertTrue(baseIngredients.contains(Ingredient.SOY_MILK));
        assertEquals(2, baseIngredients.size());
        assertTrue(pancake.customIngredients().isEmpty());
        assertEquals(1.5, pancake.getPrice());
    }

    @Test
    void addCustomIngredientsToStandardPancake_shouldIncludeCustomIngredients() {
        // Arrange & Act
        Pancake pancake = Pancake.Builder.standard()
                .addCustomIngredient(Ingredient.HAZELNUT)
                .addCustomIngredient(Ingredient.DARK_CHOCOLATE)
                .build();

        // Assert
        List<Ingredient> customIngredients = pancake.customIngredients();
        assertTrue(customIngredients.contains(Ingredient.HAZELNUT));
        assertTrue(customIngredients.contains(Ingredient.DARK_CHOCOLATE));
        assertEquals(2, customIngredients.size());
        assertEquals(5.75, pancake.getPrice()); // 2.25 (base) + 3.5 (custom)
    }

    @Test
    void addCustomIngredientToVeganPancake_shouldIncludeCustomIngredients() {
        // Arrange & Act
        Pancake pancake = Pancake.Builder.vegan()
                .addCustomIngredient(Ingredient.HAZELNUT)
                .build();

        // Assert
        List<Ingredient> customIngredients = pancake.customIngredients();
        assertTrue(customIngredients.contains(Ingredient.HAZELNUT));
        assertEquals(1, customIngredients.size());
        assertEquals(3.5, pancake.getPrice()); // 1.5 (base) + 2.0 (hazelnut)
    }

    @Test
    void createCustomPancake_shouldAllowAddingBaseAndCustomIngredients() {
        // Arrange & Act
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .addCustomIngredient(Ingredient.WHIPPED_CREAM)
                .addCustomIngredient(Ingredient.DARK_CHOCOLATE)
                .build();

        // Assert
        assertEquals(2, pancake.baseIngredients().size());
        assertEquals(2, pancake.customIngredients().size());
        assertEquals(3.25, pancake.getPrice());
    }

    @Test
    void createPancakeWithoutBaseIngredients_shouldThrowException() {
        // Arrange & Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            new Pancake.Builder()
                    .addCustomIngredient(Ingredient.MILK)
                    .build();
        });

        assertEquals("A pancake must have at least one base ingredient.", exception.getMessage());
    }

    @Test
    void addInvalidCustomIngredientAsBase_shouldThrowException() {
        // Arrange & Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Pancake.Builder()
                    .addBaseIngredient(Ingredient.DARK_CHOCOLATE)
                    .build();
        });

        assertEquals("Only base ingredients can be added here: DARK_CHOCOLATE", exception.getMessage());
    }
}
