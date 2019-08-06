package com.task.input;

import java.nio.charset.Charset;
import java.util.Scanner;

public class ConsoleInput {

    Scanner scanner;

    public ConsoleInput() {
        scanner = new Scanner(System.in, "UTF8");
    }

    public String nextString() {
        return scanner.nextLine();
    }
}
