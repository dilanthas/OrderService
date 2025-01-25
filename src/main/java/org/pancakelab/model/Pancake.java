package org.pancakelab.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Pancake(List<Ingredient> baseIngredients, List<Ingredient> customIngredients) {
    public Pancake(List<Ingredient> baseIngredients, List<Ingredient> customIngredients) {
        this.baseIngredients = Collections.unmodifiableList(baseIngredients);
        this.customIngredients = Collections.unmodifiableList(customIngredients);
    }

    public double getPrice() {
        double baseCost = baseIngredients.stream()
                .mapToDouble(IngredientMenu::getPrice)
                .sum();
        double customCost = customIngredients.stream()
                .mapToDouble(IngredientMenu::getPrice)
                .sum();
        return baseCost + customCost;
    }

    public static class Builder {
        private final List<Ingredient> baseIngredients = new ArrayList<>();
        private final List<Ingredient> customIngredients = new ArrayList<>();

        // Add a base ingredient
        public Builder addBaseIngredient(Ingredient ingredient) {
            if (ingredient.isBaseIngredient() && IngredientMenu.isValidIngredient(ingredient)) {
                baseIngredients.add(ingredient);
                return this;
            }
            throw new IllegalArgumentException("Only base ingredients can be added here: " + ingredient);

        }

        // Add a custom ingredient
        public Builder addCustomIngredient(Ingredient ingredient) {
            if (IngredientMenu.isValidIngredient(ingredient)) {
                customIngredients.add(ingredient);
                return this;
            }
            throw new IllegalArgumentException("Invalid ingredient" + ingredient);
        }

        // Static method for standard pancake recipe
        public static Builder standard() {
            Builder builder = new Builder();
            builder.addBaseIngredient(Ingredient.FLOUR)
                    .addBaseIngredient(Ingredient.EGG)
                    .addBaseIngredient(Ingredient.MILK);
            return builder;
        }

        // Static method for vegan pancake recipe
        public static Builder vegan() {
            Builder builder = new Builder();
            builder.addBaseIngredient(Ingredient.FLOUR)
                    .addBaseIngredient(Ingredient.SOY_MILK);
            return builder;
        }

        // Build the pancake
        public Pancake build() {
            if (baseIngredients.isEmpty()) {
                throw new IllegalStateException("A pancake must have at least one base ingredient.");
            }
            return new Pancake(new ArrayList<>(baseIngredients), new ArrayList<>(customIngredients));
        }
    }
}
