package com.example.com_dayone.persist_entity.entity.entity;

import com.example.com_dayone.model.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter // 맴버 변수들의 값 읽어 올 수 있는 (가져올 수있는)
@ToString //인스턴스를 출력하기 위해 편의를 높혀주는
@NoArgsConstructor // 생성자 메소드를 만들어주는데 생성자에 Atgs 가 하나도 없는 생성자를 만들어주는
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    private String name;

    // 생성자 하나 생성
    public CompanyEntity(Company company) {
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
