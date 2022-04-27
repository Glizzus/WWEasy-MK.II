

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/** This aids in gathering WWE ratings data from www.wrestlingdata.com.
 *
 * It employs multithreading to web scrape the aforementioned website and creates a csv file for use elsewhere.
 *
 * @author Cal Crosby
 */
public class RatingsCsvCreator {

    private String url;
    private final ArrayList<String> dates = new ArrayList<>();
    private final ArrayList<String> events = new ArrayList<>();
    private final ArrayList<String> relRatings = new ArrayList<>();
    private final ArrayList<String> absRatings = new ArrayList<>();


     Document tryGetDocument(String urlIn) {
        try {
            return Jsoup.connect(urlIn).get();
        }
        catch (IOException e) {
            throw new RuntimeException("Could not retrieve the webpage");
         }
    }

    /**
     * Scrapes the dates from the table.
     */
     class DateScraper implements Runnable {
        Thread thread;
        @Override
        public void run() {
            Document doc = tryGetDocument(url);
            Elements elems = doc.select("table") // The HTML was not well-designed; this was the only way
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
            Document doc = tryGetDocument(url);
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
            Document doc = tryGetDocument(url);
            Elements elems = doc.select("table")
                    .select("td[style=width:13%;][bgcolor=#660000]");
            for (int i = 1; i < elems.size(); i += 2) {
                absRatings.add(elems.get(i).text());
            }
            for (int i = 0; i < elems.size(); i += 2) {
                relRatings.add(elems.get(i).text());
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

        try (FileWriter f = new FileWriter("src/Resources/ratings.csv")) {
            f.write("Date,Event,Rating\n");
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create new file");
        }
        int pageCount = 69;
        for (int page = pageCount; page > 0; page--) {

            // This is the base of the url that contains the data. Each page number is tacked on at the end.
            String baseUrl = "https://www.wrestlingdata.com/index.php?befehl=quoten&art=2&liga=1&show=&sort=0&seite=";
            url = baseUrl + page;

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
                csv = new FileWriter("src/Resources/ratings.csv", true);
                for (int j = dates.size() - 1; j >= 0; j--) {
                    csv.write(String.format("%s,%s,%s,%s\n",
                            dates.get(j).replace('/', '-'),
                            events.get(j),
                            relRatings.get(j),
                            absRatings.get(j).replaceAll(",", "")));
                }
                dates.clear();
                events.clear();
                relRatings.clear();
                absRatings.clear();
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to write to file");
            } finally {
                assert csv != null;
                csv.close();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new RatingsCsvCreator().writeFile();
    }
}