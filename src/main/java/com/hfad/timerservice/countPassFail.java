package com.hfad.timerservice;

public class countPassFail {

    private static int passCount = 0;
    private static int failCount = 0;

    public static void count(String result) {
        if (result.equals("pass")) {
            passCount++;
        }
        else
            failCount++;
    }

    public static String getResults() {

        String results = ("Pass: " + passCount + "\nFail: " + failCount);

        return results;
    }

}