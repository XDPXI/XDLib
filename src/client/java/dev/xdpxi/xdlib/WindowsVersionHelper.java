package dev.xdpxi.xdlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WindowsVersionHelper {
    public static boolean isWindows11Build22000OrHigher() {
        int buildNumber = 0;
        Runtime rt;
        Process pr;
        BufferedReader in;
        String line;
        try {
            rt = Runtime.getRuntime();
            pr = rt.exec(new String[]{"wmic.exe", "os", "get", "BuildNumber"});
            in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            int i = 0;
            while ((line = in.readLine()) != null) {
                i += 1;
                if (i == 3) {
                    buildNumber = Integer.parseInt(line.replaceAll(" ", ""));
                }
            }

        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        if (buildNumber == 0)
            return false;
        return buildNumber >= 22000;
    }
}