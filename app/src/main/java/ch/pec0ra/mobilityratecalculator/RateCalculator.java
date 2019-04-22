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

package ch.pec0ra.mobilityratecalculator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

class RateCalculator implements Serializable {

    private static final long HALF_HOUR_IN_MS = 1800000;

    private final int kms;
    private final Calendar startDate;
    private final Calendar endDate;

    private int dayHalfHoursCount;
    private int nightHalfHoursCount;

    private int highRateKms;
    private int lowRateKms;

    private Map<Mobility.Category, Price> priceMap;

    RateCalculator(Calendar startDate, Calendar endDate, int kms) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.kms = kms;
    }

    void calculate() {
        roundTimes();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate.getTime());

        dayHalfHoursCount = 0;
        nightHalfHoursCount = 0;
        while (cal.getTimeInMillis() < endDate.getTimeInMillis()) {
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (Mobility.isDayHour(hour)) {
                dayHalfHoursCount++;
            } else {
                nightHalfHoursCount++;
            }
            cal.add(Calendar.MINUTE, 30);
        }

        highRateKms = Math.min(kms, Mobility.KM_HIGH_RATE_END);
        lowRateKms = Math.max(kms - Mobility.KM_HIGH_RATE_END, 0);

        priceMap = new HashMap<>();
        for (Mobility.Category category : Mobility.Category.values()) {
            BigDecimal dayHoursPrice = Mobility.getDayHourlyRate(category).multiply(new BigDecimal(dayHalfHoursCount)).divide(new BigDecimal(2), 2, RoundingMode.HALF_EVEN);
            BigDecimal nightHoursPrice = Mobility.getNightHourlyRate(category).multiply(new BigDecimal(nightHalfHoursCount)).divide(new BigDecimal(2), 2, RoundingMode.HALF_EVEN);

            BigDecimal highRateKmsPrice = Mobility.getHighKmsRate(category).multiply(new BigDecimal(highRateKms));
            BigDecimal lowRateKmsPrice = Mobility.getLowKmsRate(category).multiply(new BigDecimal(lowRateKms));

            Price price = new Price(dayHoursPrice, nightHoursPrice, highRateKmsPrice, lowRateKmsPrice);
            priceMap.put(category, price);
        }
    }

    int getMinutesCount() {
        if ((nightHalfHoursCount + dayHalfHoursCount) % 2 == 1) {
            return 30;
        } else {
            return 0;
        }
    }

    int getTotalHoursCount() {
        return new BigDecimal(dayHalfHoursCount + nightHalfHoursCount).divide(new BigDecimal(2), RoundingMode.DOWN).intValue();
    }

    int getTotalKilometersCount() {
        return highRateKms + lowRateKms;
    }


    Price getPrice(Mobility.Category category) {
        return priceMap.get(category);
    }

    private void roundTimes() {
        long roundedStartTime = (long) (Math.floor((double) startDate.getTimeInMillis() / (double) HALF_HOUR_IN_MS) * HALF_HOUR_IN_MS);
        long roundedEndTime = (long) (Math.ceil((double) endDate.getTimeInMillis() / (double) HALF_HOUR_IN_MS) * HALF_HOUR_IN_MS);
        startDate.setTimeInMillis(roundedStartTime);
        endDate.setTimeInMillis(roundedEndTime);
    }

    class Price implements Serializable {
        final BigDecimal dayHoursPrice;
        final BigDecimal nightHoursPrice;

        final BigDecimal highRateKmsPrice;
        final BigDecimal lowRateKmsPrice;

        Price(BigDecimal dayHoursPrice, BigDecimal nightHoursPrice, BigDecimal highRateKmsPrice, BigDecimal lowRateKmsPrice) {
            this.dayHoursPrice = dayHoursPrice;
            this.nightHoursPrice = nightHoursPrice;
            this.highRateKmsPrice = highRateKmsPrice;
            this.lowRateKmsPrice = lowRateKmsPrice;
        }

        BigDecimal getTotalPrice() {
            return dayHoursPrice.add(nightHoursPrice).add(highRateKmsPrice).add(lowRateKmsPrice);
        }

        BigDecimal getTimePrice() {
            return dayHoursPrice.add(nightHoursPrice);
        }

        BigDecimal getDistancePrice() {
            return highRateKmsPrice.add(lowRateKmsPrice);
        }
    }
}
