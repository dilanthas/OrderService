package org.pancakelab;

import org.pancakelab.model.Ingredient;
import org.pancakelab.model.Pancake;
import org.pancakelab.service.PancakeService;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        PancakeService service = new PancakeService();

        // Step 1: Create an order
        UUID orderId1 = service.createOrder(5, 101).getId();

        // Step 2: Add pancakes to the order
        Pancake pancake = Pancake.Builder
                .standard()
                .addCustomIngredient(Ingredient.DARK_CHOCOLATE)
                .build();
        service.addPancakeToOrder(orderId1, pancake);

        // Step 3: Place the order
        service.placeOrder(orderId1);

        // Step 4: Prepare the order
        service.prepareOrder();

        // Step 5: Deliver the order
        service.deliverOrder();

        printOrderDetails(service, orderId1);
    }

    private static void printOrderDetails(PancakeService service, UUID orderId) {
        System.out.println("\nFinal Order Details:");
        service.getOrders().forEach((id, order) -> {
            if (id.equals(orderId)) {
                System.out.println("Order ID: " + id);
                System.out.println("Building: " + order.getBuilding());
                System.out.println("Room: " + order.getRoom());
                System.out.println("Status: " + order.getStatus());
                System.out.println("Pancakes:");
                order.getPancakes().forEach(p -> System.out.println("  - " + p));
            }
        });
    }
}