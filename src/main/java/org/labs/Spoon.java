package org.labs;

import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Spoon {

    @Getter
    private final int id;

    protected final ReentrantLock lock = new ReentrantLock(true);

    public Spoon(int id) {
        this.id = id;
    }

    @SneakyThrows
    public void acquire(Programmer p) {
        lock.lock();
    }

    void release(Programmer p) {
        lock.unlock();
    }

    public void abandonOwnership(Programmer p) {
        return;
    }
}
