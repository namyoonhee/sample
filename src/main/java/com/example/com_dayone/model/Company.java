package com.example.com_dayone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Entity 는 데이터 베이스 테이블과 직접적으로 메핑되기 위한 클래스이기 때문에
// @Entity 를 서비스 내부에서 데이터를 주고 받기 위한 용도로 쓰거나 이 과정에서 데이터 내용을 변경하고 로직이 들어가게 되면
//클래스의 원래 역활 범위를 벗어나게 되는 걸로 볼 수 있다.

public class Company {

    private String ticker;

    private String name;
}
