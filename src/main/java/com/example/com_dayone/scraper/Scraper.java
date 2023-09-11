package com.example.com_dayone.scraper;

import com.example.com_dayone.model.Company;
import com.example.com_dayone.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
