package com.task.io;

public class ConsoleOutput implements AppOutput {

    @Override
    public void print(String s) {
        System.out.println(s);
    }
}
