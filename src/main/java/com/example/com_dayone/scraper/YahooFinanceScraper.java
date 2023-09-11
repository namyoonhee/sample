package com.example.com_dayone.scraper;


import com.example.com_dayone.model.Company;
import com.example.com_dayone.model.Dividend;
import com.example.com_dayone.model.ScrapedResult;
import com.example.com_dayone.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    // 값이 바뀌면 안되는 상수이기 때문에 static final
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1693958400&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    private static final long START_TIME = 86400; // 1일- 60초 * 60분 * 24시간

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000; // 시작 날짜
            //long end = System.currentTimeMillis() / 1000; // 끝 날짜 ( 현재 시간을 가져온 값을 사용) , 초 단위로 바꾸기 위해 /1000

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();


            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0); // table 전체

            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                // month 값을 숫자로 바꾸도록 구현한 메소드 호출 / Month.strToNumber 를 사용할 수 있는 이유는 static 메소드로 구현했기 때문
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));

//                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }

            scrapedResult.setDividends(dividends);

        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }

        return scrapedResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[0].trim(); // 1번째 위치
            // abc - def - xzy
            // 데이터의 특성에 따라 적용 할 수 있다.

            return new Company(ticker, title);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
