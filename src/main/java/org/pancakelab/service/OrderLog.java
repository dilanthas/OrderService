package org.pancakelab.service;

import org.pancakelab.model.Order;
import org.pancakelab.model.Pancake;

import java.util.List;

public class OrderLog {
    private static final StringBuilder log = new StringBuilder();

    public static void logAddPancake(Order order, Pancake pancake) {
        int pancakeCount = order.getPancakes().size();

        log.append("Added pancake with description '%s' ".formatted(pancake))
                .append("to order %s containing %d pancakes, ".formatted(order.getId(), pancakeCount))
                .append("for building %d, room %d.\n".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logRemovePancake(Order order, Pancake pancake) {
        int pancakeCount = order.getPancakes().size();

        log.append("Removed pancake with description '%s' ".formatted(pancake))
                .append("from order %s now containing %d pancakes, ".formatted(order.getId(), pancakeCount))
                .append("for building %d, room %d.\n".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logCancelOrder(Order order) {
        int pancakeCount = order.getPancakes().size();

        log.append("Cancelled order %s with %d pancake(s) ".formatted(order.getId(), pancakeCount))
                .append("for building %d, room %d.\n".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logDeliverOrder(Order order) {
        int pancakeCount = order.getPancakes().size();

        log.append("Order %s with %d pancake(s) ".formatted(order.getId(), pancakeCount))
                .append("for building %d, room %d out for delivery.\n".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logPlaceOrder(Order order) {
        int pancakeCount = order.getPancakes().size();

        log.append("Order %s with %d pancake(s) ".formatted(order.getId(), pancakeCount))
                .append("for building %d, room %d has been placed.\n".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logPrepareOrder(Order order) {
        int pancakeCount = order.getPancakes().size();

        log.append("Order %s with %d pancake(s) ".formatted(order.getId(), pancakeCount))
                .append("for building %d, room %d has been prepared.\n".formatted(order.getBuilding(), order.getRoom()));
    }

    public static String getLog() {
        return log.toString();
    }

    public static void clearLog() {
        log.setLength(0);
    }
}
