package org.pancakelab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.Ingredient;
import org.pancakelab.model.Order;
import org.pancakelab.model.Pancake;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderLogTest {
    @BeforeEach
    void setUp() {
        OrderLog.clearLog();
    }

    @Test
    void logAddPancake_shouldLogCorrectMessage() {
        // Arrange
        Order order = new Order(5, 101);
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();

        // Act
        OrderLog.logAddPancake(order, pancake);

        // Assert
        String log = OrderLog.getLog();
        assertTrue(log.contains("Added pancake with description"));
        assertTrue(log.contains("order " + order.getId()));
        assertTrue(log.contains("for building 5, room 101"));
    }

    @Test
    void logPrepareOrder_shouldLogCorrectMessage() {
        // Arrange
        Order order = new Order(5, 101);

        // Act
        OrderLog.logPrepareOrder(order);

        // Assert
        String log = OrderLog.getLog();
        assertTrue(log.contains("Order " + order.getId() + " with 0 pancake(s)"));
        assertTrue(log.contains("for building 5, room 101 has been prepared."));
    }
}
