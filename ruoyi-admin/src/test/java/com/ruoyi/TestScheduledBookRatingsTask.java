package com.ruoyi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ruoyi.scheduledtasks.BookRatingsTask;

@SpringBootTest
public class TestScheduledBookRatingsTask {
    @Autowired
    public BookRatingsTask bookRatingsTask;

    @Test
    public void TestSetAllBookRatingsCache() {
        bookRatingsTask.scheduledBookRatingsTask();
    }
}
