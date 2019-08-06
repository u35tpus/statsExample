package com.task;

import com.task.lib.Statistics;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StatisticsTest
    extends TestCase {

    public StatisticsTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(StatisticsTest.class);
    }

    /**
     * Interestingly this test proved that incrementally calculated average (http://datagenetics.com/blog/november22017/index.html) provided
     * less accurate result than just summing up and diving by count
     */
    public void testEquals() {
        Statistics s1 = new Statistics();
        Statistics s2 = new Statistics();

        assert s1.equals(s1);
        assert !s1.equals(null);
        assert !s1.equals(new Object());
        assert s1.equals(s2);
        assert s1.hashCode() == s2.hashCode();

        assertEquals(s1.hashCode(), s2.hashCode());

        Random rand = new Random();
        rand.setSeed(System.nanoTime());

        int count = 100;

        //check that mean calculation doesn't depend on the orders of put
        List<Double> doubles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double d = rand.nextDouble();
            doubles.add(d);
            s1.put(new BigDecimal(d));
        }

        for (int i = count - 1; i >= 0; i--) {
            s2.put(new BigDecimal(doubles.get(i)));
        }

        System.out.println(s1.getMean().subtract(s2.getMean()));
        assertEquals(0, s1.getMean().compareTo(s2.getMean()));

        assertEquals(s1, s2);

        assert s1.hashCode() == s2.hashCode();
    }

    public void testNonTerminatingDecimalExpansion() {
        Statistics stats = new Statistics();

        stats.put(new BigDecimal("1"));
        stats.put("535345314.452523");
        stats.put(new BigDecimal("535345314.452523"));

        assertEquals(new BigDecimal("356896876.635015"), stats.getMean());
    }

    public void testPossibleConcurrencyIssues() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        int nThreads = 1000;

        Statistics stats = new Statistics();

        Statistics compareTo = new Statistics();

        Random rand = new Random();
        rand.setSeed(System.nanoTime());

        List<Double> doubles = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            double d = rand.nextDouble();
            doubles.add(d);
            compareTo.put(new BigDecimal(d));
        }

        for (int i = 0; i < nThreads; i++) {
            final int j = i;
            executorService.submit(() -> {
                stats.put(new BigDecimal(doubles.get(j)));
                countDownLatch.countDown();
            });
        }

        assert countDownLatch.await(1, TimeUnit.MINUTES);

        assertEquals(0, compareTo.getMean().compareTo(stats.getMean()));
        assertEquals(0, compareTo.getMax().compareTo(stats.getMax()));
        assertEquals(0, compareTo.getMin().compareTo(stats.getMin()));
    }

    public void testMinMax() {
        Statistics statisticsLong = new Statistics();
        statisticsLong.put(BigDecimal.ONE);
        statisticsLong.put(BigDecimal.ONE);
        statisticsLong.put(BigDecimal.ONE);
        statisticsLong.put(BigDecimal.ONE.add(BigDecimal.ONE));

        assertEquals(0, BigDecimal.ONE.compareTo(statisticsLong.getMin()));
        assertEquals(0, statisticsLong.getMax().compareTo(new BigDecimal(2L)));
    }

    public void testMinMaxNull() {
        Statistics statisticsLong = new Statistics();

        try {
            statisticsLong.getMax();
            fail();
        } catch (ArithmeticException ae) {
            assert ae instanceof ArithmeticException;
        }

        try {
            statisticsLong.getMin();
            fail();
        } catch (ArithmeticException ae) {
            assert ae instanceof ArithmeticException;
        }

        try {
            statisticsLong.getMean();
            fail();
        } catch (ArithmeticException ae) {
            assert ae instanceof ArithmeticException;
        }
    }

    public void testMean() {
        Statistics stats = new Statistics();
        stats.put(new BigDecimal(100));

        assertEquals(new BigDecimal(100), stats.getMean());

        stats.put(new BigDecimal(200));

        assertEquals(new BigDecimal(150), stats.getMean());
    }


    public void testMeanLongSequence() {
        Statistics stats = new Statistics();
        for (int i = 0; i < 1_000_000; i++) {
            stats.put(new BigDecimal(40));
        }

        assertEquals(new BigDecimal(40), stats.getMean());
    }

    public void testNull() {
        Statistics statistics = new Statistics();
        try {
            statistics.put((String) null);
            fail();
        } catch (Exception e) {
            assert e instanceof IllegalArgumentException;
        }
        try {
            statistics.put((BigDecimal) null);
            fail();
        } catch (Exception e) {
            assert e instanceof IllegalArgumentException;
        }
    }


    public void testEquals2() {
        Statistics s1 = new Statistics();
        Statistics s2 = new Statistics();

        s1.put(BigDecimal.ONE);
        assert !s1.equals(s2);
        assert s1.hashCode() != s2.hashCode();

        s2.put(BigDecimal.ONE);
        assert s1.equals(s2);

        s1.put(BigDecimal.ZERO);
        assert !s1.equals(s2);

        s2.put(BigDecimal.ZERO);
        assert s1.equals(s2);

        s1.put(new BigDecimal(2));
        assert !s1.equals(s2);

        s2.put(new BigDecimal(2));
        assert s1.equals(s2);

        s1.put(new BigDecimal(1.5D));
        assert !s1.equals(s2);

        s2.put(new BigDecimal(1.5D));
        assert s1.equals(s2);

        s1.put(new BigDecimal(0.5D));
        s1.put(new BigDecimal(0.5D));
        s1.put(new BigDecimal(0.5D));
        s2.put(new BigDecimal(1.5D));
        assert !s1.equals(s2);
    }
}
