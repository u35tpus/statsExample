package com.task.lib;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

public class Statistics {

    private BigDecimal min;

    private BigDecimal max;

    private BigDecimal sum = BigDecimal.ZERO;

    private Long count = Long.valueOf(0L);

    private final StampedLock lock = new StampedLock();

    public void put(final String number) {
        if (number == null) {
            throw new IllegalArgumentException("Can't put null");
        }

        put(new BigDecimal(number));
    }

    public void put(final BigDecimal number) {
        if (number == null) {
            throw new IllegalArgumentException("Can't put null");
        }

        if (! (number.getClass().equals(BigDecimal.class))) {
            throw new IllegalArgumentException("Only BigDecimal accepted");
        }

        doInWriteLock(() -> {
            count++;

            processMin(number);
            processMax(number);
            processMean(number);
            return null;
        });
    }

    public BigDecimal getMean() {
        return doInReadLock(() -> {
            if (count == 0) {
                throw new ArithmeticException("Can't calculate average for zero input");
            }

            return sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
        });
    }

    public BigDecimal getMin() {
        return doInReadLock(() -> {
            if (count == 0) {
                throw new ArithmeticException("Can't calculate min for zero input");
            }

            return min;
        });
    }

    public BigDecimal getMax() {
        return doInReadLock(() -> {
            if (count == 0) {
                throw new ArithmeticException("Can't calculate max for zero input");
            }
            return max;
        });
    }

    private void processMean(BigDecimal t) {
        sum = sum.add(t);
    }

    private void processMin(BigDecimal t) {
        if (min == null) {
            min = t;
        } else {
            min = min.min(t);
        }
    }

    private void processMax(BigDecimal t) {
        if (max == null) {
            max = t;
        } else {
            max = max.max(t);
        }
    }

    private <T> T doInReadLock(Supplier<T> supplier) {
        long stamp = lock.readLock();

        try {
            return supplier.get();
        } finally {
            lock.unlockRead(stamp);
        }
    }

    private <T> T doInWriteLock(Supplier<T> supplier) {
        long stamp = lock.writeLock();

        try {
            return supplier.get();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return doInReadLock(() -> {
            Statistics that = (Statistics) o;
            return equals(min, that.min) &&
                equals(max, that.max) &&
                equals(sum, that.sum) &&
                Objects.equals(count, that.count);
        });
    }

    private boolean equals(BigDecimal d1, BigDecimal d2){
        return (d1 == d2) || (d1 != null && d2 != null && d1.compareTo(d2) == 0);
    }

    @Override
    public int hashCode() {
        return doInReadLock(() -> Objects.hash(min, max, sum, count));
    }
}
