import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This aids in gathering WWE ratings data from <a href="www.wrestlingdata.com">www.wrestlingdata.com</a>.
 * It employs multithreading to web scrape the aforementioned website and creates a csv file for use elsewhere.
 *
 * @author Cal Crosby
 */
public class RatingsCsvCreator {

    // This is the base of the url that contains the data. Each page number is tacked on at the end.
    public static final String BASE_URL =
            "https://www.wrestlingdata.com/index.php?befehl=quoten&art=2&liga=1&show=&sort=0&seite=";
    String url;
    private final ArrayList<String> dates = new ArrayList<>();
    private final ArrayList<String> events = new ArrayList<>();
    private final ArrayList<String> ratings = new ArrayList<>();


     Document tryGetDocument() throws IOException {
        return Jsoup.connect(url).get();
    }

    /**
     * Scrapes the dates from the table.
     */
     class DateScraper implements Runnable {
        Thread thread;
        @Override
        public void run() {
            Document doc;
            try {
                doc = tryGetDocument();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements elems = doc.select("table") // The HTML was not well designed; this was the only way
                    .select("td[align=center][bgcolor=#660000][style=with:20%;]");
            for (Element e : elems) {
                dates.add(e.text());
            }
        }
        public void start() {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }
        }
        public void join() throws InterruptedException {
            thread.join();
        }
    }

    /**
     * Scrapes the titles of each show from the table.
     */
    class EventScraper implements Runnable {
        Thread thread;
        @Override
        public void run() {
            Document doc;
            try {
                doc = tryGetDocument();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements elems = doc.select("table")
                    .select("div[title=Show event]");
            for (Element e : elems) {
                String event = e.text();
                events.add(event);
            }
        }
        public void start() {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }
        }
        public void join() throws InterruptedException {
            thread.join();
        }
    }

    /**
     * Scrapes the ratings of each show from the table.
     */
     class RatingsScraper implements Runnable {
        Thread thread;
        @Override
        public void run() {
            Document doc;
            try {
                doc = tryGetDocument();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements elems = doc.select("table")
                    .select("td[style=width:13%;][bgcolor=#660000]");
            for (int i = 1; i < elems.size(); i += 2) {
                ratings.add(elems.get(i).text());
            }
        }
        public void start() {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }
        }
        public void join() throws InterruptedException {
            thread.join();
        }
    }

    /**
     * This creates a new .csv file, and adds the data to it starting from oldest to newest.
     * @throws InterruptedException if the thread is interrupted
     * @throws IOException if the file cannot be opened
     */
    public void writeFile() throws InterruptedException, IOException {

        try (FileWriter f = new FileWriter("src/main/Resources/ratings.csv")) {
            f.write("Date,Event,Rating");
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create new file");
        }
        // Currently, the website has 69 pages of data
        for (int page = 69; page > 0; page--) { // TODO: Make the page count a constant

            url = BASE_URL + page;

            DateScraper dateScrape = new DateScraper();
            dateScrape.start();
            EventScraper eventScrape = new EventScraper();
            eventScrape.start();
            RatingsScraper rateScrape = new RatingsScraper();
            rateScrape.start();

            dateScrape.join();
            eventScrape.join();
            rateScrape.join();

            FileWriter csv = null;
            try {
                csv = new FileWriter("src/main/Resources/ratings.csv", true);
                for (int j = dates.size() - 1; j >= 0; j--) {
                    csv.write(String.format("%s,%s,%s\n",
                            dates.get(j).replace('/', '-'),
                            events.get(j),
                            ratings.get(j).replaceAll(",", "")));
                }
                dates.clear();
                events.clear();
                ratings.clear();
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to write to file");
            } finally {
                assert csv != null;
                csv.close();
            }
        }
    }
}