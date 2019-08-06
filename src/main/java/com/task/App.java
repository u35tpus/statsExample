package com.task;

import com.task.io.AppInput;
import com.task.io.AppOutput;
import com.task.io.ConsoleInput;
import com.task.io.ConsoleOutput;
import com.task.lib.Statistics;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    static Logger logger = Logger.getLogger(App.class.getName());
    private Statistics statisticsLong = new Statistics();
    private final AppInput appInput;
    private final AppOutput appOutput;

    public App(AppInput appInput, AppOutput appOutput){
        this.appInput = appInput;
        this.appOutput = appOutput;
    }

    void loop() {

        do {
            print("Enter number. Press CTRL-C or type 'exit' to exit. Enter S for stats");

            String s = appInput.nextString();

            if (s == null || s.trim().length() == 0) {
                continue;
            }

            if (s.trim().toLowerCase().startsWith("exit")) {
                return;
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
        appOutput.print(s);
    }


    public static void main(String[] args) {

        App app = new App(new ConsoleInput(), new ConsoleOutput());

        app.loop();

    }


}
