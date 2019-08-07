package com.task.lib;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

/**
 * Basic stats online calculator.
 * Calculates min, max, mean.
 * Thread safe in relation to methods put, getMean, getMax, getMin, hashCode, equals
 */
public class Statistics {

    private BigDecimal min;

    private BigDecimal max;

    private BigDecimal sum = BigDecimal.ZERO;

    private Long count = Long.valueOf(0L);

    private final StampedLock lock = new StampedLock();

    /**
     * Puts number to collect statistics on
     * @param number - number in string format compatible with {@code BigDecimal}
     */
    public void put(final String number) {
        if (number == null) {
            throw new IllegalArgumentException("Can't put null");
        }

        put(new BigDecimal(number));
    }

    /**
     * Puts number to collect statistics on
     * @param number - number
     */
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

    /**
     * Method to get the average of all numbers Statistics instance was provided so far
     * @return returns the average of all numbers Statistics instance was provided so far
     */
    public BigDecimal getMean() {
        return doInReadLock(() -> {
            if (count == 0) {
                throw new ArithmeticException("Can't calculate average for zero input");
            }

            return sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP);
        });
    }

    /**
     * Method to get the minimal number of all numbers Statistics instance was provided so far
     * @return returns the minimal number of all numbers Statistics instance was provided so far
     */
    public BigDecimal getMin() {
        return doInReadLock(() -> {
            if (count == 0) {
                throw new ArithmeticException("Can't calculate min for zero input");
            }

            return min;
        });
    }

    /**
     * Method to get the maximum number of all numbers Statistics instance was provided so far
     * @return returns the maximum number of all numbers Statistics instance was provided so far
     */
    public BigDecimal getMax() {
        return doInReadLock(() -> {
            if (count == 0) {
                throw new ArithmeticException("Can't calculate max for zero input");
            }
            return max;
        });
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

    @Override
    public int hashCode() {
        return doInReadLock(() -> Objects.hash(min, max, sum, count));
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

    private boolean equals(BigDecimal d1, BigDecimal d2){
        return (d1 == d2) || (d1 != null && d2 != null && d1.compareTo(d2) == 0);
    }


}
