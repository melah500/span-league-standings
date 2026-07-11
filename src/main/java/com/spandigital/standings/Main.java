package com.spandigital.standings;

public final class Main {

    private Main() {}

    public static void main(String[] args) {
        System.exit(new App(System.in, System.out, System.err).run(args));
    }
}
