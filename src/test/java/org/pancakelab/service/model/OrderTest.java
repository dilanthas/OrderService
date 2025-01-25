package org.pancakelab.service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.Ingredient;
import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;
import org.pancakelab.model.Pancake;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order(5, 101);
    }

    @Test
    void createOrder_shouldInitializeCorrectly() {
        // Assert
        assertNotNull(order.getId());
        assertEquals(5, order.getBuilding());
        assertEquals(101, order.getRoom());
        assertEquals(OrderStatus.INIT, order.getStatus());
        assertTrue(order.getPancakes().isEmpty());
    }

    @Test
    void addPancake_shouldAddPancakeToOrder() {
        // Arrange
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();

        // Act
        order.addPancake(pancake);

        // Assert
        assertEquals(1, order.getPancakes().size());
        assertEquals(pancake, order.getPancakes().get(0));
    }

    @Test
    void placeOrder_shouldChangeStatusToCreated() {
        // Act
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();

        order.addPancake(pancake);
        order.placeOrder();

        // Assert
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void placeOrder_shouldThrowExceptionIfAlreadyProcessed() {
        // Arrange
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();

        order.addPancake(pancake);
        order.placeOrder();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::placeOrder);
        assertEquals("Order already processed.", exception.getMessage());
    }

    @Test
    void prepareOrder_shouldChangeStatusToPrepared() {
        // Arrange
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();

        order.addPancake(pancake);
        order.placeOrder();

        // Act
        order.prepareOrder();

        // Assert
        assertEquals(OrderStatus.PREPARED, order.getStatus());
    }

    @Test
    void prepareOrder_shouldThrowExceptionIfNotInCreatedStatus() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::prepareOrder);
        assertEquals("Order can only be prepared from CREATED status.", exception.getMessage());
    }

    @Test
    void deliverOrder_shouldChangeStatusToDelivered() {
        // Arrange
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();
        order.addPancake(pancake);
        order.placeOrder();
        order.prepareOrder();

        // Act
        order.deliverOrder();

        // Assert
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void deliverOrder_shouldThrowExceptionIfNotInPreparedStatus() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::deliverOrder);
        assertEquals("Order can only be delivered from PREPARED status.", exception.getMessage());
    }

    @Test
    void cancelOrder_shouldChangeStatusToCanceled() {
        // Act
        order.cancelOrder();

        // Assert
        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    void cancelOrder_shouldThrowExceptionIfAlreadyDelivered() {
        // Arrange
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();

        order.addPancake(pancake);
        order.placeOrder();
        order.prepareOrder();
        order.deliverOrder();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::cancelOrder);
        assertEquals("Cannot cancel an order that is already delivered or prepared.", exception.getMessage());
    }

    @Test
    void cancelOrder_shouldThrowExceptionIfAlreadyPrepared() {
        // Arrange
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();

        order.addPancake(pancake);
        order.placeOrder();
        order.prepareOrder();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::cancelOrder);
        assertEquals("Cannot cancel an order that is already delivered or prepared.", exception.getMessage());
    }

    @Test
    void testAddPancakeThrowsExceptionIfStatusNotInit() {
        // Arrange
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();

        order.addPancake(pancake);
        // Transition the order to CREATED state
        order.placeOrder();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.addPancake(pancake),
                "Should throw IllegalStateException if the order is not in INIT state."
        );
        assertEquals("Order already created.", exception.getMessage());
    }

    @Test
    void toString_shouldReturnCorrectStringRepresentation() {
        // Arrange
        String expected = "Order{" +
                "id=" + order.getId() +
                ", building=5" +
                ", room=101" +
                ", pancakes=[]" +
                ", status=INIT" +
                '}';

        // Act
        String actual = order.toString();

        // Assert
        assertEquals(expected, actual);
    }
}
