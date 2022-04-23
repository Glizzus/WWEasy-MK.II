/*
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {



    public static void main(String[] args) throws java.io.IOException {
        Document doc =
                Jsoup.connect("https://www.wrestlingdata.com/index.php?befehl=quoten&art=2&liga=1&show=&sort=0&seite=1")
                        .get();
        Element table = doc.select("table").get(0);
        Elements rows = table.select("tr");

        System.out.println(rows);
    }

}
*/