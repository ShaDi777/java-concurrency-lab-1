package org.labs;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Programmer implements Runnable {

    @Getter
    private final int id;

    private final Table table;

    private final int leftSpoonId;
    private final int rightSpoonId;

    private final Object portionMonitor = new Object();

    private volatile PortionState portionState;

    @Getter
    private volatile int totalEatenPortions;

    public Programmer(int id, int totalCount, Table table) {
        this.id = id;

        this.table = table;


        this.leftSpoonId = Math.min(id, (id + 1) % totalCount);
        this.rightSpoonId = Math.max(id, (id + 1) % totalCount);

        this.portionState = PortionState.WAITING_FOR_PORTION;
    }

    public void processOrderResult(boolean canRefill) {
        boolean finished = false;

        synchronized (portionMonitor) {
            if (portionState != PortionState.WAITING_FOR_PORTION) {
                throw new IllegalStateException("Unexpected portion for programmer-" + id + ": state=" + portionState);
            }

            if (canRefill) {
                portionState = PortionState.HAVE_PORTION;
            } else {
                portionState = PortionState.FINISHED;
                finished = true;
            }

            portionMonitor.notifyAll();
        }

        if (finished) {
            table.getSpoon(leftSpoonId).abandonOwnership(this);
            table.getSpoon(rightSpoonId).abandonOwnership(this);

            table.programmerFinished();
        }
    }

    @Override
    public void run() {
        try {
            while (table.isActive()) {
                // food
                synchronized (portionMonitor) {
                    if (portionState == PortionState.WAITING_FOR_PORTION) {
                        table.placeOrder(this);
                    }

                    while (portionState == PortionState.WAITING_FOR_PORTION && table.isActive()) {
                        portionMonitor.wait();
                    }
                }

                if (portionState == PortionState.FINISHED || !table.isActive()) {
                    return;
                }

                if (portionState != PortionState.HAVE_PORTION) {
                    throw new IllegalStateException("Unexpected state for programmer-" + id + ": " + portionState);
                }
                log.info("Food: p-{}", id);

                // eat
                Spoon leftSpoon = table.getSpoon(leftSpoonId);
                leftSpoon.acquire(this);
                log.info("SL: p-{}", id);

                Spoon rightSpoon = table.getSpoon(rightSpoonId);
                rightSpoon.acquire(this);
                log.info("SR: p-{}", id);

                Thread.sleep(Config.EATING_WAIT_TIME_MS.getBetween());
                totalEatenPortions++;
                synchronized (portionMonitor) {
                    portionState = PortionState.WAITING_FOR_PORTION;
                }

                log.info("Eaten: p-{}", id);
                rightSpoon.release(this);
                leftSpoon.release(this);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private enum PortionState {
        WAITING_FOR_PORTION,
        HAVE_PORTION,
        FINISHED
    }
}
