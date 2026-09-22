package org.labs;

import lombok.Getter;

public class Waiter implements Runnable {

    @Getter
    private final int id;
    private final Table table;
    private final Kitchen kitchen;

    @Getter
    private int servedOrders;

    public Waiter(int id, Table table, Kitchen kitchen) {
        this.id = id;
        this.table = table;
        this.kitchen = kitchen;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Programmer programmer = table.serveOrderBlocking();
                if (programmer.getId() == Table.SHUTDOWN_ORDER) {
                    return;
                }

                Boolean gotPortion = kitchen.getPortion(programmer.getId());
                if (gotPortion == null) {
                    table.placeOrder(programmer);
                    continue;
                }
                programmer.processOrderResult(gotPortion);

                if (gotPortion) {
                    servedOrders++;
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
