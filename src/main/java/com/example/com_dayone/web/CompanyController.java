package com.example.com_dayone.web;

import com.example.com_dayone.model.Company;
import com.example.com_dayone.model.constants.CacheKey;
import com.example.com_dayone.persist_entity.entity.entity.CompanyEntity;
import com.example.com_dayone.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company") // 경로에 공통되는 부분은 다여기로 빼주기
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    private final CacheManager redisCacheManager;


    // 배당금 검색 할때 자동 완성 되도록 해주는
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        var result = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    // 회사 리스트를 조회
    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }


    // 배당금 데이터를 저장
    /**
     * 회사 및 배당금 정보 추가
     * @param request
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim(); //ticker 사용하여 앞뒤 공백 없앰
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }

        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName()); // 회사를 저장할 때 마다 트라이에 회사명이 저장됨

        return ResponseEntity.ok(company);
    }


    // 배당금 데이터를 삭제
    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = this.companyService.deleteCompany(ticker);
        this.clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName) {
        //Not implemented yet
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);

    }
}
