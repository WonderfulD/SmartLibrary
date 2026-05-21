package com.ruoyi.Utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @author wangrui <wangrui45@kuaishou.com>
 * Created on 2025-04-12
 */
public class DateUtil {
    public static int calculateDateDifference(LocalDate d1, LocalDate d2) {
        return (int) ChronoUnit.DAYS.between(d1, d2);
    }
}
