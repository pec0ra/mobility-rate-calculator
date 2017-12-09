/*
 *     Copyright (C) 2016 Basile
 *
 *     This file is part of Mobility Rate Calculator.
 *
 *     Mobility Rate Calculator is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Mobility Rate Calculator is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Mobility Rate Calculator.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.pec0ra.mobilityratecalculator;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class Mobility {



    public enum Category {
        BUDGET,
        MICRO,
        ECONOMY,
        ELECTRO,
        COMBI,
        CABRIO,
        EMOTION,
        MINIVAN,
        TRANSPORT
    }

    public static final int DAY_RATE_START = 7;
    public static final int DAY_RATE_END = 22;
    // Rates are not lower after 100km anymore so we use the highest value possible
    public static final int KM_HIGH_RATE_END = Integer.MAX_VALUE;

    public static boolean isDayHour(int hour){
        return hour >= DAY_RATE_START && hour <= DAY_RATE_END;
    }
    public static boolean isKMLow(int km){
        return km < KM_HIGH_RATE_END;
    }

    public static Map<Category, Rate> ratesMap;
    static {
        ratesMap = new HashMap<>();
        ratesMap.put(Category.BUDGET, new Rate(new BigDecimal("2.0"), new BigDecimal("2.0"), new BigDecimal("0.55"), new BigDecimal("0.55")));
        ratesMap.put(Category.MICRO, new Rate(new BigDecimal("2.5"), new BigDecimal("2.5"), new BigDecimal("0.65"), new BigDecimal("0.65")));
        ratesMap.put(Category.ECONOMY, new Rate(new BigDecimal("2.5"), new BigDecimal("2.5"), new BigDecimal("0.65"), new BigDecimal("0.65")));
        ratesMap.put(Category.ELECTRO, new Rate(new BigDecimal("2.5"), new BigDecimal("2.5"), new BigDecimal("0.65"), new BigDecimal("0.65")));
        ratesMap.put(Category.COMBI, new Rate(new BigDecimal("3.0"), new BigDecimal("3.0"), new BigDecimal("0.8"), new BigDecimal("0.8")));
        ratesMap.put(Category.CABRIO, new Rate(new BigDecimal("4.0"), new BigDecimal("4.0"), new BigDecimal("0.95"), new BigDecimal("0.95")));
        ratesMap.put(Category.EMOTION, new Rate(new BigDecimal("4.0"), new BigDecimal("4.0"), new BigDecimal("0.95"), new BigDecimal("0.95")));
        ratesMap.put(Category.MINIVAN, new Rate(new BigDecimal("4.0"), new BigDecimal("4.0"), new BigDecimal("0.95"), new BigDecimal("0.95")));
        ratesMap.put(Category.TRANSPORT, new Rate(new BigDecimal("4.0"), new BigDecimal("4.0"), new BigDecimal("0.95"), new BigDecimal("0.95")));
    }

    public static BigDecimal getDayHourlyRate(Category category) {
        return ratesMap.get(category).hourlyRateDay;
    }
    public static BigDecimal getNightHourlyRate(Category category) {
        return ratesMap.get(category).hourlyRateNight;
    }
    public static BigDecimal getHighKmsRate(Category category) {
        return ratesMap.get(category).KMRateHigh;
    }
    public static BigDecimal getLowKmsRate(Category category) {
        return ratesMap.get(category).KMRateLow;
    }

    public static BigDecimal getHourRate(Category category, int hour){
        if(isDayHour(hour)) {
            return ratesMap.get(category).hourlyRateDay;
        } else {
            return ratesMap.get(category).hourlyRateNight;
        }
    }
    public static BigDecimal getKMRate(Category category, int kms) {
        if(isKMLow(kms)){
            return ratesMap.get(category).KMRateLow.multiply(new BigDecimal(kms));
        } else {
            Rate r = ratesMap.get(category);
            return r.KMRateLow.multiply(new BigDecimal(100)).add(r.KMRateHigh.multiply(new BigDecimal(kms - 100)));
        }
    }


    private static class Rate {
        public final BigDecimal hourlyRateDay;
        public final BigDecimal hourlyRateNight;
        public final BigDecimal KMRateHigh;
        public final BigDecimal KMRateLow;

        private Rate(BigDecimal hourlyRateDay, BigDecimal hourlyRateNight, BigDecimal kmRateHigh, BigDecimal kmRateLow) {
            this.hourlyRateDay = hourlyRateDay;
            this.hourlyRateNight = hourlyRateNight;
            KMRateHigh = kmRateHigh;
            KMRateLow = kmRateLow;
        }
    }
    public static SpinnerAdapter getCategorySpinnerAdapter(Context context){
        String[] items = new String[]{
            context.getString(R.string.budget),
            context.getString(R.string.micro),
            context.getString(R.string.economy),
            context.getString(R.string.electro),
            context.getString(R.string.combi),
            context.getString(R.string.cabrio),
            context.getString(R.string.emotion),
            context.getString(R.string.minivan),
            context.getString(R.string.transport)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, items);
        return adapter;
    }

    public static String categoryToString(Category category, Context context){
        switch (category){
            case BUDGET:
                return context.getString(R.string.budget);
            case MICRO:
                return context.getString(R.string.micro);
            case ECONOMY:
                return context.getString(R.string.economy);
            case ELECTRO:
                return context.getString(R.string.electro);
            case COMBI:
                return context.getString(R.string.combi);
            case CABRIO:
                return context.getString(R.string.cabrio);
            case EMOTION:
                return context.getString(R.string.emotion);
            case MINIVAN:
                return context.getString(R.string.minivan);
            case TRANSPORT:
                return context.getString(R.string.transport);
            default:
                return context.getString(R.string.budget);
        }
    }

    public static Category getCategoryFromPos(int selectedItemPosition) {
        switch (selectedItemPosition){
            case 0:
                return Category.BUDGET;
            case 1:
                return Category.MICRO;
            case 2:
                return Category.ECONOMY;
            case 3:
                return Category.ELECTRO;
            case 4:
                return Category.COMBI;
            case 5:
                return Category.CABRIO;
            case 6:
                return Category.EMOTION;
            case 7:
                return Category.MINIVAN;
            case 8:
                return Category.TRANSPORT;
            default:
                return Category.BUDGET;
        }
    }
}
