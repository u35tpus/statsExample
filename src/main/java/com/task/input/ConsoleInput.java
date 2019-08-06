package com.task.input;

import java.util.Scanner;

public class ConsoleInput {

    private final Scanner scanner;

    public ConsoleInput() {
        scanner = new Scanner(System.in, "UTF8");
    }

    public String nextString() {
        return scanner.nextLine();
    }
}
