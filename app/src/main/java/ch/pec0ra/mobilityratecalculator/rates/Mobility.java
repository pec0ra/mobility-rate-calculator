/*
 *     Copyright (C) 2017 Basile Maret
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

package ch.pec0ra.mobilityratecalculator.rates;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.pec0ra.mobilityratecalculator.R;

public abstract class Mobility {

    public enum Category {
        BUDGET,
        ECONOMY,
        COMBI,
        CABRIO,
        EMOTION,
        MINIVAN,
        TRANSPORT
    }

    public enum SubscriptionType {
        MEMBER(new MemberRate(), 0),
        PLUS(new PlusRate(), 1),
        YOUNG(new YoungRate(), 2),
        LEARN(new LearnRate(), 3),
        EASY(new EasyRate(), 4),
        BUSINESS(new BusinessRate(), 5),
        BUSINESS_LITE(new BusinessLiteRate(), 6);

        private final Mobility rate;
        private final int intValue;

        SubscriptionType(Mobility rate, int intValue) {
            this.rate = rate;
            this.intValue = intValue;
        }

        public static SubscriptionType fromInt(int value) {
            if (value == MEMBER.intValue) {
                return MEMBER;
            } else if (value == PLUS.intValue) {
                return PLUS;
            } else if (value == YOUNG.intValue) {
                return YOUNG;
            } else if (value == LEARN.intValue) {
                return LEARN;
            } else if (value == BUSINESS.intValue) {
                return BUSINESS;
            } else if (value == EASY.intValue) {
                return EASY;
            } else if (value == BUSINESS_LITE.intValue) {
                return BUSINESS_LITE;
            } else {
                return MEMBER;
            }
        }

        public Mobility getRate() {
            return rate;
        }

        public int getIntValue() {
            return intValue;
        }
    }

    private static final int DAY_RATE_START = 7;
    private static final int DAY_RATE_END = 22;
    // Rates are not lower after 100km anymore so we use the highest value possible
    public static final int KM_HIGH_RATE_END = Integer.MAX_VALUE;

    public static boolean isDayHour(int hour) {
        return hour >= DAY_RATE_START && hour <= DAY_RATE_END;
    }

    private static boolean isKMLow(int km) {
        return km < KM_HIGH_RATE_END;
    }

    @NotNull
    private final Map<Category, Rate> ratesMap;

    Mobility() {
        ratesMap = initRetMap();
    }

    @NotNull
    abstract HashMap<Category, Rate> initRetMap();

    public BigDecimal getDayHourlyRate(Category category) {
        return Objects.requireNonNull(ratesMap.get(category)).hourlyRateDay;
    }

    public BigDecimal getNightHourlyRate(Category category) {
        return Objects.requireNonNull(ratesMap.get(category)).hourlyRateNight;
    }

    public BigDecimal getHighKmsRate(Category category) {
        return Objects.requireNonNull(ratesMap.get(category)).KMRateHigh;
    }

    public BigDecimal getLowKmsRate(Category category) {
        return Objects.requireNonNull(ratesMap.get(category)).KMRateLow;
    }

    public BigDecimal getAccessFee(Category category) {
        return Objects.requireNonNull(ratesMap.get(category)).accessFee;
    }

    public BigDecimal getHourRate(Category category, int hour) {
        if (isDayHour(hour)) {
            return Objects.requireNonNull(ratesMap.get(category)).hourlyRateDay;
        } else {
            return Objects.requireNonNull(ratesMap.get(category)).hourlyRateNight;
        }
    }

    public BigDecimal getKMRate(Category category, int kms) {
        if (isKMLow(kms)) {
            return Objects.requireNonNull(ratesMap.get(category)).KMRateLow.multiply(BigDecimal.valueOf(kms));
        } else {
            Rate r = Objects.requireNonNull(ratesMap.get(category));
            return r.KMRateLow.multiply(BigDecimal.valueOf(100)).add(r.KMRateHigh.multiply(BigDecimal.valueOf(kms - 100)));
        }
    }


    protected static class Rate {
        final BigDecimal hourlyRateDay;
        final BigDecimal hourlyRateNight;
        final BigDecimal KMRateHigh;
        final BigDecimal KMRateLow;
        @Nullable
        final BigDecimal accessFee;

        Rate(BigDecimal hourlyRateDay, BigDecimal hourlyRateNight, BigDecimal kmRateHigh, BigDecimal kmRateLow, @Nullable BigDecimal accessFee) {
            this.hourlyRateDay = hourlyRateDay;
            this.hourlyRateNight = hourlyRateNight;
            KMRateHigh = kmRateHigh;
            KMRateLow = kmRateLow;
            this.accessFee = accessFee;
        }
    }

    public static String categoryToString(Category category, Context context) {
        switch (category) {
            case BUDGET:
                return context.getString(R.string.budget);
            case ECONOMY:
                return context.getString(R.string.economy);
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
        switch (selectedItemPosition) {
            case 0:
                return Category.BUDGET;
            case 1:
                return Category.ECONOMY;
            case 2:
                return Category.COMBI;
            case 3:
                return Category.CABRIO;
            case 4:
                return Category.EMOTION;
            case 5:
                return Category.MINIVAN;
            case 6:
                return Category.TRANSPORT;
            default:
                return Category.BUDGET;
        }
    }
}
