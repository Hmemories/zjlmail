package com.mail.test;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {

    @Test
    public void test1(){
        System.out.println(0.05+0.01);
        System.out.println(1.0-4.2);
        System.out.println(4.015*100);
        System.out.println(123.3/100);
    }
//    0.060000000000000005
//            -3.2
//            401.49999999999994
//            1.2329999999999999
    @Test
    public void test2(){
        BigDecimal b1 = new BigDecimal(0.05);
        BigDecimal b2 = new BigDecimal(0.01);
        System.out.println(b1.add(b2));
    }
//    0.06000000000000000298372437868010820238851010799407958984375
    @Test
    public void test3(){
        //解决精度问题  要用String构造器
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.01");
        System.out.println(b1.add(b2));
    }
//    0.06
}
