package org.pancakelab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.Ingredient;
import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;
import org.pancakelab.model.Pancake;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PancakeServiceTest {
    private PancakeService pancakeService;

    @BeforeEach
    void setUp() {
        pancakeService = new PancakeService();
    }

    @Test
    void testCreateOrderWithValidInput() {
        assertDoesNotThrow(() -> pancakeService.createOrder(5, 101), "Valid inputs should not throw an exception.");
    }

    @Test
    void testCreateOrderWithInvalidBuilding() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pancakeService.createOrder(-1, 101),
                "Negative building numbers should throw an exception."
        );
        assertEquals("Building number must be positive. Provided: -1", exception.getMessage());
    }

    @Test
    void testCreateOrderWithInvalidRoom() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pancakeService.createOrder(5, 0),
                "Room number must be positive."
        );
        assertEquals("Room number must be positive. Provided: 0", exception.getMessage());
    }
    @Test
    void createOrder_shouldAddOrderToPendingOrders() {
        // Arrange
        int building = 5;
        int room = 101;

        // Act
        Order order = pancakeService.createOrder(building, room);

        // Assert
        assertNotNull(order);
        assertEquals(building, order.getBuilding());
        assertEquals(room, order.getRoom());
        assertTrue(pancakeService.getPendingOrders().containsKey(order.getId()));
    }

    @Test
    void addPancakeToOrder_shouldAddPancakeToPendingOrder() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);
        Pancake pancake = Pancake.Builder
                .standard()
                .build();

        // Act
        pancakeService.addPancakeToOrder(order.getId(), pancake);

        // Assert
        assertEquals(1, order.getPancakes().size());
        assertEquals(pancake, order.getPancakes().get(0));
    }

    @Test
    void placeOrder_shouldMoveOrderFromPendingToNewOrders() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);
        Pancake pancake = Pancake.Builder
                .standard()
                .build();
        pancakeService.addPancakeToOrder(order.getId(), pancake);

        // Act
        pancakeService.placeOrder(order.getId());

        // Assert
        assertFalse(pancakeService.getPendingOrders().containsKey(order.getId()));
        assertTrue(pancakeService.getNewOrders().contains(order));
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void prepareOrder_shouldMoveOrderFromNewToPreparedOrders() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);
        Pancake pancake = Pancake.Builder
                .standard()
                .build();
        pancakeService.addPancakeToOrder(order.getId(), pancake);
        pancakeService.placeOrder(order.getId());

        // Act
        pancakeService.prepareOrder();

        // Assert
        assertFalse(pancakeService.getNewOrders().contains(order));
        assertTrue(pancakeService.getPreparedOrders().contains(order));
        assertEquals(OrderStatus.PREPARED, order.getStatus());
    }

    @Test
    void deliverOrder_shouldMoveOrderFromPreparedToDeliveredOrders() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();
        pancakeService.addPancakeToOrder(order.getId(), pancake);
        pancakeService.placeOrder(order.getId());
        pancakeService.prepareOrder();

        // Act
        pancakeService.deliverOrder();

        // Assert
        assertFalse(pancakeService.getPreparedOrders().contains(order));
        assertTrue(pancakeService.getDeliveredOrders().contains(order));
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void cancelOrder_shouldRemoveOrderFromPendingOrders() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);

        // Act
        pancakeService.cancelOrder(order.getId());

        // Assert
        assertFalse(pancakeService.getPendingOrders().containsKey(order.getId()));
    }

    @Test
    void cancelOrder_shouldRemoveOrderFromNewOrders() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();
        pancakeService.addPancakeToOrder(order.getId(), pancake);
        pancakeService.placeOrder(order.getId());

        // Act
        pancakeService.cancelOrder(order.getId());

        // Assert
        assertFalse(pancakeService.getNewOrders().contains(order));
    }

    @Test
    void placeOrder_shouldThrowExceptionIfNoPancakesAdded() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> pancakeService.placeOrder(order.getId()));
        assertEquals("Cannot place an order without pancakes: " + order.getId(), exception.getMessage());
    }

    @Test
    void cancelOrder_shouldThrowExceptionIfOrderCannotBeCanceled() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();
        pancakeService.addPancakeToOrder(order.getId(), pancake);
        pancakeService.placeOrder(order.getId());
        pancakeService.prepareOrder();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> pancakeService.cancelOrder(order.getId()));
        assertEquals("Order cannot be canceled in its current state: " + order.getId(), exception.getMessage());
    }

    @Test
    void cancelOrder_shouldSetStatusToCanceled() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);

        // Act
        pancakeService.cancelOrder(order.getId());

        // Assert
        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    void cancelOrder_shouldSetStatusToCanceledFromNewOrders() {
        // Arrange
        Order order = pancakeService.createOrder(5, 101);
        Pancake pancake = new Pancake.Builder()
                .addBaseIngredient(Ingredient.FLOUR)
                .addBaseIngredient(Ingredient.MILK)
                .build();
        pancakeService.addPancakeToOrder(order.getId(), pancake);
        pancakeService.placeOrder(order.getId());

        // Act
        pancakeService.cancelOrder(order.getId());

        // Assert
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        assertTrue(pancakeService.getOrders().containsKey(order.getId()));
    }

    @Test
    void testConcurrentOrderProcessing() throws InterruptedException {
        PancakeService service = new PancakeService();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Create 100 orders
        for (int i = 1; i <= 100; i++) {
            int building = i;
            executor.execute(() -> {
                Order order = service.createOrder(building, 101);
                Pancake pancake = new Pancake.Builder()
                        .addBaseIngredient(Ingredient.FLOUR)
                        .addBaseIngredient(Ingredient.MILK)
                        .build();
                service.addPancakeToOrder(order.getId(), pancake);
                service.placeOrder(order.getId());
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Assert all orders are placed and logged
        assertEquals(100, service.getOrders().size());
    }
    @Test
    void testConcurrentCancelAndPlaceOrder() throws InterruptedException {
        PancakeService service = new PancakeService();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Create an order
        Order order = service.createOrder(5, 101);
        Pancake pancake = Pancake.Builder
                .standard()
                .addCustomIngredient(Ingredient.DARK_CHOCOLATE)
                .build();
        service.addPancakeToOrder(order.getId(), pancake);
        // Thread 1: Cancels the order
        executor.execute(() -> {
            try {
                Thread.sleep(50);
                service.cancelOrder(order.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Thread 2: Places the order
        executor.execute(() -> {
            try {
                Thread.sleep(50);
                service.placeOrder(order.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Ensure only one operation (cancel or place) succeeds
        assertTrue(service.getPendingOrders().isEmpty(), "Pending orders should be empty");
        assertTrue(service.getNewOrders().isEmpty(), "New orders should be empty if canceled");
        assertTrue(order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.CREATED,
                "Order should either be canceled or placed");
    }

    @Test
    void testConcurrentModificationOfSameOrder() throws InterruptedException {
        PancakeService service = new PancakeService();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Create a single order
        Order order = service.createOrder(5, 101);

        // Thread 1: Add pancakes to the order
        executor.execute(() -> {
            for (int i = 0; i < 5; i++) {
                Pancake pancake = new Pancake.Builder()
                        .addBaseIngredient(Ingredient.FLOUR)
                        .addBaseIngredient(Ingredient.MILK)
                        .build();
                service.addPancakeToOrder(order.getId(), pancake);
            }
        });

        // Thread 2: Place the same order
        executor.execute(() -> {
            try {
                Thread.sleep(50); // Allow some pancakes to be added before placing
                service.placeOrder(order.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Thread 3: Add more pancakes to the same order
        executor.execute(() -> {
            for (int i = 0; i < 5; i++) {
                Pancake pancake = new Pancake.Builder()
                        .addBaseIngredient(Ingredient.FLOUR)
                        .addBaseIngredient(Ingredient.MILK)
                        .build();
                service.addPancakeToOrder(order.getId(), pancake);
            }
        });

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        synchronized (order) {
            // The order should not contain more pancakes than expected
            assertEquals(10, order.getPancakes().size(), "Order should contain at most 10 pancakes");

            // The order should be in the correct state (either pending or placed)
            assertTrue(
                    order.getStatus() == OrderStatus.CREATED || order.getStatus() == OrderStatus.INIT,
                    "Order should either be placed or pending"
            );
        }
    }
}
