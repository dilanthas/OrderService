package org.pancakelab.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final int building;
    private final int room;
    private final List<Pancake> pancakes = new ArrayList<>();
    private OrderStatus status;

    public Order(int building, int room) {
        this.id = UUID.randomUUID();
        this.building = building;
        this.room = room;
        this.status = OrderStatus.INIT;
    }

    public UUID getId() {
        return id;
    }

    public int getBuilding() {
        return building;
    }

    public int getRoom() {
        return room;
    }

    public List<Pancake> getPancakes() {
        return pancakes;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public synchronized void addPancake(Pancake pancake) {
        if (status != OrderStatus.INIT) {
            throw new IllegalStateException("Order already created.");
        }
        pancakes.add(pancake);
    }

    public synchronized void placeOrder() {
        if (status != OrderStatus.INIT) {
            throw new IllegalStateException("Order already processed.");
        }
        if (pancakes.isEmpty()) {
            throw new IllegalStateException("Cannot place an order without pancakes: " + id);
        }
        status = OrderStatus.CREATED;
    }

    public synchronized void prepareOrder() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Order can only be prepared from CREATED status.");
        }
        status = OrderStatus.PREPARED;
    }

    public synchronized void deliverOrder() {
        if (status != OrderStatus.PREPARED) {
            throw new IllegalStateException("Order can only be delivered from PREPARED status.");
        }
        status = OrderStatus.DELIVERED;
    }

    public synchronized void cancelOrder() {
        if (status == OrderStatus.DELIVERED || status == OrderStatus.PREPARED) {
            throw new IllegalStateException("Cannot cancel an order that is already delivered or prepared.");
        }
        this.status = OrderStatus.CANCELED;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", building=" + building +
                ", room=" + room +
                ", pancakes=" + pancakes +
                ", status=" + status +
                '}';
    }
}
