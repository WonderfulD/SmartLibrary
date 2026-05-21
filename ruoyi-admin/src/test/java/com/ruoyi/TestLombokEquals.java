package com.ruoyi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.ruoyi.book.domain.Books;

/**
 * @author wangrui <wangrui45@kuaishou.com>
 * Created on 2025-01-24
 */
@SpringBootTest
public class TestLombokEquals {
    @Test
    void test(){
        Books books = new Books();
        books.setBookId(1L);
        if(books.equals(new Books())) {
            System.out.println("空的");
        }else {
            System.out.println("非空");
        }
    }
}
