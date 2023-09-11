package com.example.com_dayone.model.constants;

public enum Month {

    JAN("Jan", 1),
    FEB("Feb", 2),
    MAR("Mar", 3),
    APR("Apr", 4),
    MAY("May", 5),
    JUN("Jun", 6),
    JUL("Jul", 7),
    AUG("Aug", 8),
    SEP("Sep", 9),
    OCT("Oct", 10),
    NOV("Nov", 11),
    DEC("Dec", 12);

    private String s;
    private int number;

    Month(String s, int n) {
        this.s = s;
        this.number = n;
    }

    public static int strToNumber(String s) {// 파라미터로는 바꾸고자 하는 문자열 값으로 받고
        for (var m : Month.values()) { // 파라미터와 동일한 값을 만나면 그 숫자 값을 반환 하도록
            if (m.s.equals(s)) {
                return m.number;
            }
        }

        return -1; // 다돌때 까지 못 찾으면

    }
}
