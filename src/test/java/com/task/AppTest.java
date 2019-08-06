package com.task;


import com.task.io.AppInput;
import com.task.io.AppOutput;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest
    extends TestCase {

    public AppTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void test1(){
        execTestInFolder("test1");
    }

    public void test2(){
        execTestInFolder("test2");
    }

    public void test3(){
        execTestInFolder("test3");
    }

    public void test4(){
        execTestInFolder("test4");
    }

    public void testLongInput() {
        final AtomicInteger limit = new AtomicInteger(100000);
        Random random = new Random();
        random.setSeed(System.nanoTime());


        AppInput appInput = () ->    {
            int ctr = limit.decrementAndGet();
            if (ctr > 0) {
                return new BigDecimal(random.nextDouble()).toString();
            }
            if (ctr == 0) {
                return "s";
            }

            return "exit";
        };


        final ArrayDeque<String> outputs = new ArrayDeque<>();

        final AppOutput appOutput = (s) -> outputs.addLast(s);


        App app = new App(appInput, appOutput);
        app.loop();
    }


    public void execTestInFolder(String folderName) {
        final ArrayDeque<String> inputs = readFile(folderName + "/in.txt");
        final ArrayDeque<String> outputs = new ArrayDeque<>();


        AppInput appInput = () ->    inputs.pollFirst();

        final AppOutput appOutput = (s) -> outputs.addLast(s);


        App app = new App(appInput, appOutput);



        ArrayDeque<String> testOutputs = readFile(folderName + "/out.txt");


        app.loop();

        while ( !outputs.isEmpty()) {
            String e1 = outputs.pop();
            String e2 = testOutputs.pop();

            assertEquals(e1, e2);
        }

    }


    private ArrayDeque<String> readFile(String fileName) {
        File source;
        Scanner input;

        ArrayDeque<String> out = new ArrayDeque<>();

        try {
            source = new File(
                getClass().getClassLoader().getResource(fileName).getFile()
            );


            input = new Scanner(source);
            String line;
            while((line = input.nextLine()) != null) {
                out.addLast(line);
            }
        } catch(Exception e) {
        }

        return out;
    }


}
