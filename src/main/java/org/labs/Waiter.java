package org.labs;

import java.util.List;
import lombok.Getter;

public class Waiter implements Runnable {

    @Getter
    private final int id;
    private final Table table;
    private final Kitchen kitchen;
    private final List<Programmer> programmers;

    @Getter
    private int servedOrders;

    public Waiter(int id, Table table, Kitchen kitchen, List<Programmer> programmers) {
        this.id = id;
        this.table = table;
        this.kitchen = kitchen;
        this.programmers = programmers;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int programmerId = table.serveOrderBlocking();
                if (programmerId == Table.SHUTDOWN_ORDER) {
                    return;
                }

                Programmer programmer = programmers.get(programmerId);
                Boolean gotPortion = kitchen.getPortion(programmerId);
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
