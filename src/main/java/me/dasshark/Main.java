package me.dasshark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    static String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "outages.txt";

    public static void main(String[] args) {
        boolean wasConnected = true;
        Date offlineSince = null;
        logStarted();
        while (true) {
            try {
                InetAddress inet = InetAddress.getByName("8.8.8.8");
                boolean isConnected = inet.isReachable(5000); // Timeout is set to 5 seconds
                if (!isConnected && wasConnected) {
                    logOutage();
                    wasConnected = false;
                    offlineSince = new Date();
                } else if (isConnected && !wasConnected) {
                    logOnline(offlineSince);
                    wasConnected = true;
                }
                Thread.sleep(120000); // Wait for 2 minutes before checking the connection again
            } catch (IOException | InterruptedException ignored) {}
        }
    }

    private static void logOutage() {
        try {
            File file = new File(path);
            FileWriter writer = new FileWriter(file, true);
            Date date = new Date();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            writer.write("Internet outage detected at " + formattedDate + ".\n");
            writer.close();
        } catch (IOException ignored) {}
    }

    private static void logOnline(Date offlineSince) {
        try {
            File file = new File(path);
            FileWriter writer = new FileWriter(file, true);
            Date date = new Date();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            long outageDuration = (new Date().getTime() - offlineSince.getTime());
            writer.write("Internet online again at " + formattedDate + ". It was offline for " + format(outageDuration) + ".\n");
            writer.close();
        } catch (IOException ignored) {}
    }

    private static void logStarted() {
        try {
            File file = new File(path);
            FileWriter writer = new FileWriter(file, true);
            Date date = new Date();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            writer.write("InternetOutageLogger by DasShark started at " + formattedDate + ".\n");
            writer.close();
            System.out.println("Done! " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String format(long outageDurationMillis) {
        long seconds = outageDurationMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        String durationStr;
        if (hours > 0) {
            durationStr = hours + " hours " + minutes + " minutes " + seconds + " seconds";
        } else if (minutes > 0) {
            durationStr = minutes + " minutes " + seconds + " seconds";
        } else {
            durationStr = seconds + " seconds";
        }
        return durationStr;
    }
}