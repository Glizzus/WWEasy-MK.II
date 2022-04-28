import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * A class for fetching Yahoo! Finance .csv files.
 * This works for any Stack on Yahoo! Finance, and the .csv file gets downloaded to the desired directory.
 *
 * @author Cal Crosby
 */
public class StockDataCsvGrabber {

    private StockDataCsvGrabber() {
    } // Don't let anybody instantiate this class


    /**
     * Converts a date to Unix Time.
     *
     * @param dateIn the input date in format yyyy-MM-dd
     * @return A long that represents the given date in Unix time
     */
    private static long dateToUnixTime(String dateIn) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.parse(dateIn)
                .getTime()
                / 1000L; // converts milliseconds to seconds
    }

    private static Boolean isValidIncrement(String increment) {
        String[] validIntervals = new String[]{"daily", "weekly", "monthly"};
        for (String type : validIntervals) {
            if (type.equals(increment)) return true;
        }
        return false;
    }

    /**
     * Creates the URL for the given information that leads to the desired .csv file.
     * This works for any stock, but we will set it to WWE most of the time.
     *
     * @param ticker    The stock to get a .csv of
     * @param date1     the start date in Unix time
     * @param date2     the end date in Unix time
     * @param increment the interval you want data to be in (daily, weekly, monthly)
     * @return A String that is the assembled URL
     */
    private static String buildYahooUrl(String ticker, Long date1, Long date2, String increment) {
        System.out.println("Creating URL");
        String baseUrl = "https://query1.finance.yahoo.com/v7/finance/download/";
        String period1 = "?period1=" + date1;
        String period2 = "&period2=" + date2;
        String interval = "&interval=1" + switch (increment.toLowerCase()) {
            case "daily" -> "d";
            case "weekly" -> "mo";
            case "monthly" -> "wk";
            default -> throw new IllegalArgumentException();
        };
        String endUrl = "&events=history&includeAdjustedClose=true";
        return baseUrl + ticker + period1 + period2 + interval + endUrl;
    }

    /**
     * Creates a file name for the .csv to be downloaded to.
     *
     * @param path      the file path
     * @param start     the start date in the Gregorian calendar
     * @param end       the end date in the Gregorian calendar
     * @param increment the interval of data (daily, weekly, monthly)
     * @return a String that is the filename of the finished file
     */
    private static String makeFileName(String path, String ticker, String start, String end, String increment) {
        if (!path.endsWith("/")) {
            path += "/"; // Done for Unix-based directories
        }
        else if (!path.endsWith("\\")) {
            path += "\\"; // Done for Windows directories
        }
        return path + ticker + "_" + start + "to" + end + "_" + increment;
    }

    /**
     * Downloads a file from any url to a specified directory.
     *
     * @param url  the url of the file to download
     * @param path  the filename of the file to be downloaded
     * @throws IOException if the url is incorrect or file is not found
     */
    private static void getFileFromUrl(String url, String path) throws IOException {
        System.out.printf("Downloading file from %s%n", url);
        ReadableByteChannel readChannel = Channels.newChannel(new URL(url).openStream());
        try (FileOutputStream output = new FileOutputStream(path)) {
            output.getChannel()
                    .transferFrom(readChannel, 0, Long.MAX_VALUE);
        }
    }

    private static String[] getInputs() {
        String[] inputs = new String[5];
        Scanner scan = new Scanner(System.in);
        System.out.print("Stock: " );
        inputs[0] = scan.nextLine();

        do {
            System.out.print("Start date: ");
            inputs[1] = scan.nextLine();
            System.out.print("End date: ");
            inputs[2] = scan.nextLine();
            if (inputs[2].compareTo(inputs[1]) < 0) System.out.println("End date must come after Start date.");
        }
        while (inputs[2].compareTo(inputs[1]) < 0);

        do {
            System.out.print("Interval: ");
            inputs[3] = scan.nextLine();
            if (!isValidIncrement(inputs[3])) System.out.println("Invalid increment, options include\n" +
                                                                    "[daily], [weekly], [monthly]");
        }
        while (!isValidIncrement(inputs[3]));

        Path toCheck;
        do {
            System.out.print("Directory to download to: ");
            inputs[4] = scan.nextLine();
            toCheck = Path.of(inputs[4]);
            if (!Files.isDirectory(toCheck)) System.out.println("Directory does not exist.");
        }
        while (!Files.isDirectory(toCheck));
        return inputs;
    }

    private static void getStockDataCsv(String[] args) throws IOException, ParseException {
        if (args.length != 5) throw new IllegalArgumentException();
        String ticker = args[0];
        String start = args[1];
        String end = args[2];
        if (end.compareTo(start) < 0) throw new IllegalArgumentException("Error: end date must be after start date");
        String increment;
        if (isValidIncrement(args[3])) {
            increment = args[3];
        }
        else throw new IllegalArgumentException("Error: Invalid Increment");
        String path = args[4];
        if (!Files.isDirectory(Path.of(path))) throw new IllegalArgumentException("Error: Directory does not exists");

        long unixStart = dateToUnixTime(start);
        long unixEnd = dateToUnixTime(end);
        String url = buildYahooUrl(ticker, unixStart, unixEnd, increment);
        String fullPath = makeFileName(path, ticker, start, end, increment);
        getFileFromUrl(url, fullPath);
        System.out.printf("File loaded into %s%n", fullPath);
    }

    public static void main(String[] args) throws ParseException, IOException {
        if (args.length == 0) getStockDataCsv(getInputs());
        else if (args.length != 5) throw new IllegalArgumentException();
        else {
            getStockDataCsv(args);
        }
    }
}