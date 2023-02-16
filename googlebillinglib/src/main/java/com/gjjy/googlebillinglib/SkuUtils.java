package com.gjjy.googlebillinglib;

import android.content.res.Resources;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Locale;

public class SkuUtils {
    public @interface DateSymbol {
        char NONE = '-';
        char YEAR = 'Y';
        char MONTH = 'M';
        char WEEK = 'W';
    }

    private static boolean checkSubscriptionPeriod(String subscriptionPeriod) {
        if (subscriptionPeriod == null) {
            return false;
        }
        return subscriptionPeriod.length() >= 3 && subscriptionPeriod.charAt(0) == 'P';
    }

    @NonNull
    public static String toSubscriptionPeriod(@NonNull Resources res,
                                              String subscriptionPeriod,
                                              boolean isCaps,
                                              boolean isPlural) {
        String dateStr = getDateString(res, subscriptionPeriod, isCaps, isPlural);
        return TextUtils.isEmpty(dateStr) ? "" : subscriptionPeriod.charAt(1) + " " + dateStr;
    }


    public static int toSubscriptionPeriodOfDate(String subscriptionPeriod) {
        if (!checkSubscriptionPeriod(subscriptionPeriod)) {
            return 0;
        }
        char num = subscriptionPeriod.charAt(1);
        return num >= 48 && num <= 57 ? Integer.parseInt(String.valueOf(num)) : 0;
    }

    @DateSymbol
    public static char toSubscriptionPeriodOfDateSymbol(String subscriptionPeriod) {
        if (!checkSubscriptionPeriod(subscriptionPeriod)) {
            return DateSymbol.NONE;
        }
        return subscriptionPeriod.charAt(2);
    }

    @NonNull
    public static String toSubscriptionPeriod(@NonNull Resources res, String subscriptionPeriod) {
        return toSubscriptionPeriod(res, subscriptionPeriod, true, true);
    }

    public static String toPriceAmountMicros(long priceAmountMicros) {
        return String.format(Locale.getDefault(), "%.2f", priceAmountMicros / 100_0000D);
    }

    public static int getPriceBeginIndex(String price) {
        if (TextUtils.isEmpty(price)) {
            return -1;
        }
        char[] priceArr = price.toCharArray();
        for (int i = 0; i < priceArr.length; i++) {
            char chr = priceArr[i];
            if (chr >= 48 && chr <= 57) {
                return i;
            }
        }

        return -1;
    }

    public static String getName(String title) {
        if (TextUtils.isEmpty(title)) return title;
        int index = title.indexOf("(");
        title = title.substring(0, index);
        return title
                .replaceAll("-->", "(")
                .replaceAll("<--", ")");
    }

    public static String getPrice(String price) {
        int index = SkuUtils.getPriceBeginIndex(price);
        return index != -1 ? price.substring(index) : price;
    }

    public static String getPriceSymbol(String price) {
        int index = getPriceBeginIndex(price);
        return index != -1 ? price.substring(0, index) : "";
    }

    public static String getDateString(Resources res, String subscriptionPeriod, boolean isCaps, boolean isPlural) {
        if (!checkSubscriptionPeriod(subscriptionPeriod)) {
            return "";
        }

        char num = subscriptionPeriod.charAt(1);
        int resId = 0;
        //复数形式（ 2 ~ 9 ）
        isPlural = isPlural && num >= 50 && num <= 57;
        switch (subscriptionPeriod.charAt(2)) {
            case DateSymbol.YEAR:
                if (isPlural) {
                    resId = isCaps ? R.string.stringYearsOfCaps : R.string.stringYears;
                    break;
                }
                resId = isCaps ? R.string.stringYearOfCaps : R.string.stringYear;
                break;
            case DateSymbol.MONTH:
                if (isPlural) {
                    resId = isCaps ? R.string.stringMonthsOfCaps : R.string.stringMonths;
                    break;
                }
                resId = isCaps ? R.string.stringMonthOfCaps : R.string.stringMonth;
                break;
            case DateSymbol.WEEK:
                if (isPlural) {
                    resId = isCaps ? R.string.stringWeeksOfCaps : R.string.stringWeeks;
                    break;
                }
                resId = isCaps ? R.string.stringWeekOfCaps : R.string.stringWeek;
                break;
        }
        return resId == 0 ? "" : res.getString(resId);
    }

    @NonNull
    public static String getDateString(@NonNull Resources res, String subscriptionPeriod) {
        return getDateString(res, subscriptionPeriod, true, true);
    }
}
