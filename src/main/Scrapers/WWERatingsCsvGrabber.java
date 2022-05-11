import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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


    private static Document tryGetDocument(String urlIn) {
        try {
            return Jsoup.connect(urlIn).get();
        }
        catch (IOException e) {
            throw new RuntimeException("Could not retrieve the webpage");
        }
    }

    /**
     * Encapsulates data for web scraping, as well as provides a method for formatting the dat as a csv line
     */
    private static class ScrapeResults {
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> shows = new ArrayList<>();
        ArrayList<String> relRatings = new ArrayList<>();
        ArrayList<String> absRatings = new ArrayList<>();

        /**
         * Formats ScrapeResults into a csv String.
         *
         * @param lineNum an int representing the index of the ScrapeResults to use
         * @return a formatting String to be added to a .csv file
         */
        private String resultFormat(int lineNum, int page) {
            // Ensures there is no newline for the last line of a .csv file
            String format = (page == 1 && lineNum == 0) ? "%s,%s,%s,%s" : "%s,%s,%s,%s\n";
            return String.format(format,
                    dates.get(lineNum).replace('/', '-'),
                    shows.get(lineNum),
                    relRatings.get(lineNum),
                    absRatings.get(lineNum).replaceAll(",", ""));
        }
    }

    private static class ScrapeWorker implements Runnable {

          Thread thread;
          ScrapeResults results = new ScrapeResults();
          String whatToScrape;
          String url;

          ScrapeWorker(String whatToScrapeIn, String urlIn) {
              whatToScrape = whatToScrapeIn;
              url = urlIn;
        }
        public void run() {
              Document doc = tryGetDocument(url);
              Elements tables = doc.select("table");
              switch (whatToScrape) {
                    case "dates" ->
                            tables.select("td[align=center][bgcolor=#660000][style=with:20%;]").
                                    forEach(x -> results.dates.add(x.text()));

                    case "shows" ->
                            tables.select("div[title=Show event]")
                                .forEach(x -> results.shows.add(x.text()));

                    case "ratings" -> {
                        Elements ratings = tables.select("td[style=width:13%;][bgcolor=#660000]");
                        for (int i = 0; i < ratings.size(); i++) {
                            results.relRatings.add(ratings.get(i).text()); // relative ratings at even indices
                            results.absRatings.add(ratings.get(++i).text()); // absolute ratings are at odd indices
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
        private ScrapeResults scrape() {
              start();
              return results;
        }
    }

    private ScrapeResults scrapePage(String url) throws InterruptedException {
        ScrapeWorker dateScraper = new ScrapeWorker("dates", url);
        ScrapeWorker showScraper = new ScrapeWorker("shows", url);
        ScrapeWorker ratingsScraper = new ScrapeWorker("ratings", url);

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

        try (FileWriter f = new FileWriter("src/Resources/ratings.csv")) {
            f.write("Date,Event,Relative Rating, Absolute Rating\n");
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create new file");
        }
        System.out.println("\rNew file initialized. Scraping...");
        System.out.println("\rThis may take a few minutes...");
        int pageCount = 69;
        for (int page = pageCount; page > 0; page--) {

            // This is the base of the url that contains the data. Each page number is tacked on at the end.
            String baseUrl = "https://www.wrestlingdata.com/index.php?befehl=quoten&art=2&liga=1&show=&sort=0&seite=";
            String url = baseUrl + page;
            ScrapeResults results = scrapePage(url);

            FileWriter csv = null;
            try {
                csv = new FileWriter("src/Resources/ratings.csv", true);
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

    public static void grabRatings() throws IOException, InterruptedException {
        main(new String[]{});
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new WWERatingsCsvGrabber().writeFile();
        System.out.println("Scraping done");
    }
}