package com.example.com_dayone.service;


import com.example.com_dayone.exception.impl.NoCompanyException;
import com.example.com_dayone.model.Company;
import com.example.com_dayone.model.ScrapedResult;
import com.example.com_dayone.persist_entity.entity.entity.CompanyEntity;
import com.example.com_dayone.persist_entity.entity.entity.DividendEntity;
import com.example.com_dayone.persist_entity.entity.CompanyRepository;
import com.example.com_dayone.persist_entity.entity.DividendRepository;
import com.example.com_dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {
    private final Trie trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }
    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사의 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        // 저장된 아이디 값을 가져올 수 있음
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
                        .map(e -> new DividendEntity(companyEntity.getId(), e))
                        .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntityList);
        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword){
        Pageable limit = PageRequest.of(0, 10); // 한번에 10개씩
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(CompanyEntity::getName)
                .collect(Collectors.toList());
    }

    public void addAutocompleteKeyword(String keyword) { // 회사명 저장
        this.trie.put(keyword,null);
    }

    public List<String> autocomplete(String keyword) { // 검색
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    // tick 에 저장된 키워드를 삭제하는 기능
    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {

        var company = this.companyRepository.findByTicker(ticker)
                        .orElseThrow(() -> new NoCompanyException());

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
