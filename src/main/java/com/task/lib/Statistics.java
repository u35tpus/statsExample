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

        long stamp = lock.writeLock();
        try {
            count++;

            processMin(number);
            processMax(number);
            processMean(number);
        } finally {
            lock.unlockWrite(stamp);
        }
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
            return Objects.equals(min, that.min) &&
                Objects.equals(max, that.max) &&
                Objects.equals(sum, that.sum) &&
                Objects.equals(count, that.count);
        });
    }

    @Override
    public int hashCode() {
        return doInReadLock(() -> Objects.hash(min, max, sum, count));
    }
}