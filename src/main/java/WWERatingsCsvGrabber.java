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
public final class WWERatingsCsvGrabber {


    private WWERatingsCsvGrabber() {} // don't let anybody instantiate this class


     private Document tryGetDocument(String urlIn) {
        try {
            return Jsoup.connect(urlIn).get();
        }
        catch (IOException e) {
            throw new RuntimeException("Could not retrieve the webpage");
         }
    }

    private static class ScrapeResults {
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> shows = new ArrayList<>();
        ArrayList<String> relRatings = new ArrayList<>();
        ArrayList<String> absRatings = new ArrayList<>();

        /**
         * Formats ScrapeResults into a csv String.
         *
         * @param i an integer representing the index of the ScrapeResults to use
         * @return a formatting String to be added to a .csv file
         */
        private String resultFormat(int i, int page) {
            // Ensures there is no newline for the last line of a .csv file
            String form = (i == 0 && page == 1) ? "%s,%s,%s,%s" : "%s,%s,%s,%s\n";
            return String.format(form,
                    dates.get(i).replace('/', '-'),
                    shows.get(i),
                    relRatings.get(i),
                    absRatings.get(i).replaceAll(",", ""));
        }
    }

    private class Scraper implements Runnable {
          Thread thread;
          ScrapeResults results = new ScrapeResults();
          String toScrape;
          String url;
          Scraper(String whatToScrape, String urlIn) {
              toScrape = whatToScrape;
              url = urlIn;
        }
        public void run() {
              Document doc = tryGetDocument(url);
              Elements elems;
              switch (toScrape) {
                    case "dates" -> {
                        elems = doc.select("table")
                              .select("td[align=center][bgcolor=#660000][style=with:20%;]");
                        for (Element e : elems) {
                            results.dates.add(e.text());
                        }
                    }
                    case "shows" -> {
                        elems = doc.select("table").select("div[title=Show event]");
                        for (Element e : elems) {
                            results.shows.add(e.text());
                        }
                    }
                    case "ratings" -> {
                        elems = doc.select("table").select("td[style=width:13%;][bgcolor=#660000]");
                        for (int i = 0; i < elems.size(); i += 2) {
                            results.relRatings.add(elems.get(i).text());
                        }
                        for (int i = 1; i < elems.size(); i += 2) {
                            results.absRatings.add(elems.get(i).text());
                        }
                    }
                    default -> throw new IllegalArgumentException();
            }
        }
        private void start() {
              thread = new Thread(this);
              thread.start();
        }
        private void join() throws InterruptedException {
              this.thread.join();
        }
        private ScrapeResults scrape() throws InterruptedException {
              start();
              return results;
        }
    }

    private ScrapeResults scrapePage(String url) throws InterruptedException {
        Scraper dateScraper = new Scraper("dates", url);
        Scraper showScraper = new Scraper("shows", url);
        Scraper ratingsScraper = new Scraper("ratings", url);

        ScrapeResults results = new ScrapeResults();
        results.dates = dateScraper.scrape().dates;
        results.shows = showScraper.scrape().shows;

        ScrapeResults ratings = ratingsScraper.scrape();
        results.relRatings = ratings.relRatings;
        results.absRatings = ratings.absRatings;

        dateScraper.join();
        showScraper.join();
        ratingsScraper.join();

        return results;
    }


    /**
     * This creates a new .csv file, and adds the data to it starting from oldest to newest.
     * @throws InterruptedException if the thread is interrupted
     * @throws IOException if the file cannot be opened
     */
    private void writeFile() throws InterruptedException, IOException {

        try (FileWriter f = new FileWriter("src/Resources/main_ratings.csv")) {
            f.write("Date,Event,Relative Rating, Absolute Rating\n");
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create new file");
        }
        int pageCount = 69;
        for (int page = pageCount; page > 0; page--) {

            // This is the base of the url that contains the data. Each page number is tacked on at the end.
            String baseUrl = "https://www.wrestlingdata.com/index.php?befehl=quoten&art=2&liga=1&show=&sort=0&seite=";
            String url = baseUrl + page;
            ScrapeResults results = scrapePage(url);

            FileWriter csv = null;
            try {
                csv = new FileWriter("src/Resources/main_ratings.csv", true);
                for (int i = results.dates.size() - 1; i >= 0; i--) {
                    csv.write(results.resultFormat(i, page));
                }
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to write to file");
            }
            finally {
                assert csv != null;
                csv.close();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new WWERatingsCsvGrabber().writeFile();
    }
}