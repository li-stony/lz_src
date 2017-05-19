package lizl.smth.stock;

import lizl.smth.stock.crawler.Crawler;

/**
 * Created by cussyou on 17-2-17.
 */
public class AppMain {
    public static void main(String[] args) {
        String dbUrl = "mongodb://127.0.0.1:27017";
        Crawler crawler = new Crawler(dbUrl);
        crawler.run();
    }
}
