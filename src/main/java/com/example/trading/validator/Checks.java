package com.example.trading.validator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;


public final class Checks {

    public static boolean isValidHour(LocalDateTime date) {
        DayOfWeek day = date.getDayOfWeek();
        if (DayOfWeek.SATURDAY == day || DayOfWeek.SUNDAY == day) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (date.isAfter(now)) {
            return false;
        }
        LocalTime time1 = date.toLocalTime();
        LocalTime time2 = LocalTime.of(9, 00);
        LocalTime time3 = LocalTime.of(11, 30);
        if (!time1.isBefore(time2) && !time1.isAfter(time3)) {
            return true;
        }
        LocalTime time4 = LocalTime.of(12, 30);
        LocalTime time5 = LocalTime.of(15, 30);
        if (!time1.isBefore(time4) && !time1.isAfter(time5)) {
            return true;
        }
        return false;
    }


    public static boolean isValidSymbol(String inputSymbol) {
        String code = inputSymbol;
        if (code.length() != 4) {
            return false;
        } else {
            char a = code.charAt(0);
            if (a < '0' || '9' < a) {
                return false;
            } else {
                char c = code.charAt(2);
                if (c < '0' || '9' < c) {
                    return false;
                } else {
                    String regex = ".[0123456789ACDFGHJKLMNPRSTUWXY].[0123456789ACDFGHJKLMNPRSTUWXY]";
                    return code.matches(regex);
                }
            }
        }
    }


    public static boolean isValidPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (price.compareTo(new BigDecimal("999999999")) > 0) {
            return false;
        }
        if (price.scale() > 1) {
            return false;
        }
        return true;
    }


    public static boolean isValidQuantity(Long quantity) {
        if (quantity <= 0) {
            return false;
        }
        if (quantity % 100 != 0) {
            return false;
        }
        if (quantity > 9999999999999L) {
            return false;
        }
        return true;
    }
}