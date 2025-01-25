package org.pancakelab.model;

import java.util.HashMap;
import java.util.Map;

public class IngredientMenu {
    private static final Map<Ingredient, Double> ingredients = new HashMap<>();

    static {
        ingredients.put(Ingredient.DARK_CHOCOLATE, 1.50);
        ingredients.put(Ingredient.MILK_CHOCOLATE, 1.00);
        ingredients.put(Ingredient.HAZELNUT, 2.00);
        ingredients.put(Ingredient.WHIPPED_CREAM, 0.25);
        ingredients.put(Ingredient.MILK, 1.0);
        ingredients.put(Ingredient.SOY_MILK, 1.0);
        ingredients.put(Ingredient.FLOUR, 0.5);
        ingredients.put(Ingredient.EGG, 0.75);
    }

    public static double getPrice(Ingredient ingredient) {
        return ingredients.get(ingredient);
    }

    public static boolean isValidIngredient(Ingredient ingredient) {
        return ingredients.containsKey(ingredient);
    }

    public static void addIngredient(Ingredient ingredient, double price) {
        ingredients.put(ingredient, price);
    }

}
