package com.example.com_dayone.scheduler;

import com.example.com_dayone.model.Company;
import com.example.com_dayone.model.Dividend;
import com.example.com_dayone.model.ScrapedResult;
import com.example.com_dayone.model.constants.CacheKey;
import com.example.com_dayone.persist_entity.entity.entity.CompanyEntity;
import com.example.com_dayone.persist_entity.entity.entity.DividendEntity;
import com.example.com_dayone.persist_entity.entity.CompanyRepository;
import com.example.com_dayone.persist_entity.entity.DividendRepository;
import com.example.com_dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    private static DividendEntity apply(Dividend e) {
        return new DividendEntity(company.getId(), e);
    }

//
//    @Scheduled(fixedDelay = 1000)
//    public void test1() throws InterruptedException {
//        Thread.sleep(1000); // 10초간 일시 정지
//        System.out.println(Thread.currentThread().getName() + " -> 테스트 1 : " + LocalDateTime.now());
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void test2() {
//        System.out.println(Thread.currentThread().getName() + " -> 테스트 2 : " + LocalDateTime.now());
//    }

    // 일정 주기마다 수행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) // finance 에 해당하는 데이터는 모두다 비운다는 뜻
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
               log.info("scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        ScrapedResult scrapedResult = null;
        for (var company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());
            scrapedResult = this.yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName()));
        }


        // 스크래핑 배당금 정보 중 데이터베이스에 없는 값을 저장
        scrapedResult.getDividends().stream()
                // 디비든 모델을 디비든 엔티티로 매핑
                .map(ScraperScheduler::apply)
                // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                .forEach(this::accept);

        // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
        try {
            Thread.sleep(3000); // 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }

    private void accept(DividendEntity e) {
        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
        if (!exists) {
            this.dividendRepository.save(e);
            log.info("insert new dividend -> " + e.toString());
        }
    }
}
