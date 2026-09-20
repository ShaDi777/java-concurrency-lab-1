package org.labs;

public class Main {

    public static void main(String[] args) {
        // var restaurant = new EqualRestaurant(7, 1_000_000, 2);
        var restaurant = new Restaurant(7, 1_000_000, 2);

        restaurant.simulateBlocking(10);

        System.out.println("Programmer results:");
        restaurant
            .getProgrammerResult()
            .forEach((programmer, portions) -> System.out.println(programmer + ": " + portions));

        System.out.println();
        System.out.println("Waiter results:");
        restaurant
            .getWaiterResult()
            .forEach((waiter, orders) -> System.out.println(waiter + ": " + orders));
    }
}
