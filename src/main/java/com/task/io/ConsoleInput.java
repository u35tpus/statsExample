package com.task.io;

import java.util.Scanner;

public class ConsoleInput implements AppInput {

    private final Scanner scanner;

    public ConsoleInput() {
        scanner = new Scanner(System.in, "UTF8");
    }

    @Override
    public String nextString() {
        return scanner.nextLine();
    }
}
