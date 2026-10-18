package com.example.trading.validator;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class Checks {
    private Checks() {
    }
    public static boolean isValidSymbol(String str) {
        String code = str;
        if (code.length() !=4) {
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
                    {
                        for (int i = 0; i < 4;i++) {
                        char z = code.charAt(i);
                        String forbidden = "BEIOQVZabcdefghijklmnopqrstuvwxyz";
                        if (forbidden.indexOf(z) >=0) {
                            return false;
                        }
                    }
                        return false;
                    }
                }
            }
        }
    }
    public static boolean isValidHour(LocalDateTime date) {
        DayOfWeek b = date.getDayOfWeek();
        if (DayOfWeek.SATURDAY == b || DayOfWeek.SUNDAY == b) {
            return false;
        }
        LocalTime time1 = date.toLocalTime();
        LocalTime time2 = LocalTime.of(9,00);
        LocalTime time3 = LocalTime.of(11,30);
        if(!time1 .isBefore(time2) && !time1 .isAfter(time3)) {
            return true;
        }
        LocalTime time4 = LocalTime.of(12,30);
        LocalTime time5 = LocalTime.of(15,30);
        if(!time1 .isBefore(time4) && !time1 .isAfter(time5)) {
            return true;
        }
        LocalTime time6 = LocalTime.now();
        if (time1. isBefore(time6)) {
            return true;
        }
        return false;
    }
    public static boolean isValidPrice (String prices) {
        double price = Double.parseDouble(prices);
        if(price <= 0) {
            return false;
        }
        if (price > 999999999) {
            return false;
        }
        return true;
    }
    public static boolean isValidQuantity(String quantitys) {
        long quantity = Long.parseLong(quantitys);
        if (quantity <= 0) {
            return false;
        }
        if (quantity % 100 !=0) {
            return false;
        } else {
            String quantity1 = Long.toString(quantity);
            if (quantity1.length() > 12) {
                return false;
            }
            return true;
        }
    }
 }
