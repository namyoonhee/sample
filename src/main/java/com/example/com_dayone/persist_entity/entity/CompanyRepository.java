package com.example.com_dayone.persist_entity.entity;

import com.example.com_dayone.persist_entity.entity.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name);  // 회사명 기준

    Optional<CompanyEntity> findByTicker(String ticker); // 회사의 명이 아니라 회사의 ticker 명으로 회사의 정보를 찾으려면

    // like 연산자 사용
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}
