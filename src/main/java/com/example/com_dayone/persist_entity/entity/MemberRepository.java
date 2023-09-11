package com.example.com_dayone.persist_entity.entity;

import com.example.com_dayone.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByUsername(String username);

    boolean existsByUsername(String username);
} //회원 정보를 데이터베이스를 넣고 가져오기 위한 과정
