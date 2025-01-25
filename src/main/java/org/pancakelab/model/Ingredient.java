package org.pancakelab.model;

public enum Ingredient {
    DARK_CHOCOLATE(false),
    WHIPPED_CREAM(false),
    HAZELNUT(false),
    MILK_CHOCOLATE(false),
    MILK(true),
    FLOUR(true),
    EGG(true),
    SOY_MILK(true);

    private final boolean isBaseIngredient;

    Ingredient(boolean isBaseIngredient) {
        this.isBaseIngredient = isBaseIngredient;
    }

    public boolean isBaseIngredient() {
        return isBaseIngredient;
    }
}
