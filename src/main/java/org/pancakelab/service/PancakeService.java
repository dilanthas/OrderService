package org.pancakelab.service;

import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;
import org.pancakelab.model.Pancake;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PancakeService {
    private final Map<UUID, Order> pendingOrders = new ConcurrentHashMap<>(); // Temporary storage for orders without pancakes
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();
    private final Queue<Order> newOrders = new ConcurrentLinkedQueue<>();
    private final Queue<Order> preparedOrders = new ConcurrentLinkedQueue<>();
    private final Queue<Order> deliveredOrders = new ConcurrentLinkedQueue<>();

    // Step 1: Create a new order
    public Order createOrder(int building, int room) {
        validateBuildingAndRoom(building, room);
        Order order = new Order(building, room);
        pendingOrders.put(order.getId(), order); // Store temporarily until pancakes are added
        return order;
    }

    // Step 2: Add a pancake to the order
    public void addPancakeToOrder(UUID orderId, Pancake pancake) {
        Order order = pendingOrders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found or already placed: " + orderId);
        }
        synchronized (order) {
            order.addPancake(pancake);
        }
        OrderLog.logAddPancake(order, pancake);
    }

    // Step 3: Place the order (move from pendingOrders to newOrders)
    public void placeOrder(UUID orderId) {
        Order order = pendingOrders.remove(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found or already placed: " + orderId);
        }
        synchronized (order) {
            if (order.getPancakes().isEmpty()) {
                throw new IllegalStateException("Cannot place an order without pancakes: " + orderId);
            }
            order.placeOrder();
        }
        newOrders.add(order);
        orders.put(orderId, order); // Track all orders
        OrderLog.logPlaceOrder(order);
    }

    // Step 4: Prepare the order (move from newOrders to preparedOrders)
    public void prepareOrder() {
        Order order = newOrders.poll(); // Get the next order to prepare
        if (order == null) {
            System.out.println("No orders to prepare.");
            return;
        }
        synchronized (order) {
            if (order.getStatus() != OrderStatus.CREATED) {
                throw new IllegalStateException("Order is not in a valid state for preparation: " + order.getId());
            }
            order.prepareOrder();
        }
        preparedOrders.add(order);
        OrderLog.logPrepareOrder(order);
    }

    // Step 5: Deliver the order (move from preparedOrders to deliveredOrders)
    public void deliverOrder() {
        Order order = preparedOrders.poll();
        if (order == null) {
            System.out.println("No orders in PREPARED state to deliver.");
            return;
        }

        synchronized (order) {
            if (order.getStatus() != OrderStatus.PREPARED) {
                throw new IllegalStateException("Order is not in a valid state for delivery: " + order.getId());
            }
            order.deliverOrder();
        }
        deliveredOrders.add(order);
        OrderLog.logDeliverOrder(order);
    }

    // Step 6: Cancel an order (from pendingOrders or newOrders)
    public void cancelOrder(UUID orderId) {
        Order order = pendingOrders.remove(orderId);
        if (order != null) {
            synchronized (order) {
                order.cancelOrder();
            }
            OrderLog.logCancelOrder(order);
            return;
        }

        boolean removedFromNew = removeOrderFromQueue(newOrders, orderId);
        if (removedFromNew) {
            order = orders.get(orderId);
            if (order != null) {
                synchronized (order) {
                    order.cancelOrder();
                }
                OrderLog.logCancelOrder(order);
            }
            return;
        }

        throw new IllegalStateException("Order cannot be canceled in its current state: " + orderId);
    }

    // Helper: Get an order from a queue by ID
    private Order getOrderFromQueue(Queue<Order> queue, UUID orderId, String stateName) {
        return queue.stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order not found in " + stateName + " state: " + orderId));
    }

    private void validateBuildingAndRoom(int building, int room) {
        if (building <= 0) {
            throw new IllegalArgumentException("Building number must be positive. Provided: " + building);
        }
        if (room <= 0) {
            throw new IllegalArgumentException("Room number must be positive. Provided: " + room);
        }
    }

    // Helper: Remove an order from a queue by ID
    private boolean removeOrderFromQueue(Queue<Order> queue, UUID orderId) {
        return queue.removeIf(order -> order.getId().equals(orderId));
    }

    public Map<UUID, Order> getPendingOrders() {
        return Collections.unmodifiableMap(pendingOrders);
    }

    // Getter for orders (immutable view)
    public Map<UUID, Order> getOrders() {
        return Collections.unmodifiableMap(orders);
    }

    // Getter for newOrders (read-only list)
    public List<Order> getNewOrders() {
        return newOrders.stream().toList();
    }

    // Getter for preparedOrders (read-only list)
    public List<Order> getPreparedOrders() {
        return preparedOrders.stream().toList();
    }

    // Getter for deliveredOrders (read-only list)
    public List<Order> getDeliveredOrders() {
        return List.copyOf(deliveredOrders);
    }
}
