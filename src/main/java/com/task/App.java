package com.task;

import com.task.input.ConsoleInput;
import com.task.lib.Statistics;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    static Logger logger = Logger.getLogger(App.class.getName());
    private Statistics statisticsLong = new Statistics();

    void loop() {
        ConsoleInput consoleInput = new ConsoleInput();

        do {
            print("Enter number. Press CTRL-C to exit. Enter S for stats");

            String s = consoleInput.nextString();

            if (s == null) {
                continue;
            }

            s = s.trim();

            if (s.toLowerCase().startsWith("s")) {
                printResult(() -> {
                    print(format("min", statisticsLong.getMin()));
                });
                printResult(() -> {
                    print(format("max", statisticsLong.getMax()));
                });
                printResult(() -> {
                    print(format("mean", statisticsLong.getMean()));
                });
                continue;
            }

            BigDecimal l;
            try {
                s = s.replaceAll(",", "");
                l = new BigDecimal(s);
            } catch (Exception e) {
                logger.log(Level.WARNING, String.format("Failed to parse number %s. Skipping it!", s));
                continue;
            }

            statisticsLong.put(l);
        }
        while (true);
    }

    private String format(String tag, BigDecimal number) {
        return String.format("%s %.10f [%s]", tag, number, number);
    }

    private void printResult(Runnable run) {
        try {
            run.run();
        } catch (Exception ae) {
            logger.log(Level.WARNING, ae.getMessage());
        }
    }

    private void print(String s) {
        System.out.println(s);
    }


    public static void main(String[] args) {

        App app = new App();

        app.loop();

    }


}
