package org.labs.equal;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.SneakyThrows;
import org.labs.Programmer;
import org.labs.Spoon;

public class EqualSpoon extends Spoon {

    private Programmer lastOwner = null;
    private final ReentrantLock ownerLock = new ReentrantLock(true);
    private final Condition ownerChanged = ownerLock.newCondition();

    public EqualSpoon(int id) {
        super(id);
    }

    @Override
    @SneakyThrows
    public void acquire(Programmer p) {
        ownerLock.lock();
        try {
            while (lastOwner == p) {
                ownerChanged.await();
            }
        } finally {
            ownerLock.unlock();
        }

        super.acquire(p);

        ownerLock.lock();
        try {
            lastOwner = p;
            ownerChanged.signalAll();
        } finally {
            ownerLock.unlock();
        }
    }

    @Override
    public void abandonOwnership(Programmer p) {
        lock.lock();

        try {
            ownerLock.lock();
            try {
                lastOwner = null;
                ownerChanged.signalAll();
            } finally {
                ownerLock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }
}
