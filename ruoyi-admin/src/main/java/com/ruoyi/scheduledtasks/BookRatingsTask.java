package com.ruoyi.scheduledtasks;

import static com.ruoyi.RedisConstants.CACHE_BOOKRATING_EX;
import static com.ruoyi.RedisConstants.CACHE_BOOKRATING_KEY;
import static com.ruoyi.RedisConstants.CACHE_NULL_EX;
import static com.ruoyi.RedisConstants.CACHE_NULL_PLACEHOLDER;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruoyi.book.service.IBooksService;
import com.ruoyi.rate.service.IBookRatingsService;

@Component
public class BookRatingsTask {

    @Autowired
    private IBookRatingsService bookRatingsService;

    @Autowired
    private IBooksService booksService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 每天00:00执行的定时任务
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Shanghai")
    public void scheduledBookRatingsTask() {
        List<Long> bookIds = booksService.getBookIds();
        for (Long bookId : bookIds) {
            String key = CACHE_BOOKRATING_KEY + bookId;
            String ratingStr = bookRatingsService.getRating(bookId);
            //存Redis
            if (ratingStr != null) { //查得到
                stringRedisTemplate.opsForValue().set(key, ratingStr, CACHE_BOOKRATING_EX, TimeUnit.MINUTES);
            } else { //查不到
                stringRedisTemplate.opsForValue().set(key, CACHE_NULL_PLACEHOLDER, CACHE_NULL_EX, TimeUnit.MINUTES);
            }
        }
    }
}
