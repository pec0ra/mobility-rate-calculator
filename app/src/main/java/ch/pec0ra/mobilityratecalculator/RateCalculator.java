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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RateCalculator implements Serializable{

    private static final long HALF_HOUR_IN_MS = 1800000;

    public final int kms;
    public final Calendar startDate;
    public final Calendar endDate;

    private int dayHalfHoursCount;
    private int nightHalfHoursCount;

    public int highRateKms;
    public int lowRateKms;

    public Map<Mobility.Category, Price> priceMap;

    public RateCalculator(Calendar startDate, Calendar endDate, int kms){
        this.startDate = startDate;
        this.endDate = endDate;
        this.kms = kms;
    }

    public void calculate(){
        roundTimes();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate.getTime());

        dayHalfHoursCount = 0;
        nightHalfHoursCount = 0;
        while(cal.getTimeInMillis() < endDate.getTimeInMillis()){
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if(Mobility.isDayHour(hour)){
                dayHalfHoursCount++;
            } else {
                nightHalfHoursCount++;
            }
            cal.add(Calendar.MINUTE, 30);
        }

        highRateKms = Math.min(kms, Mobility.KM_HIGH_RATE_END);
        lowRateKms = Math.max(kms - Mobility.KM_HIGH_RATE_END, 0);

        priceMap = new HashMap<>();
        for(Mobility.Category category : Mobility.Category.values()){
            BigDecimal dayHoursPrice = Mobility.getDayHourlyRate(category).multiply(new BigDecimal(dayHalfHoursCount).divide(new BigDecimal(2)));
            BigDecimal nightHoursPrice = Mobility.getNightHourlyRate(category).multiply(new BigDecimal(nightHalfHoursCount).divide(new BigDecimal(2)));

            BigDecimal highRateKmsPrice = Mobility.getHighKmsRate(category).multiply(new BigDecimal(highRateKms));
            BigDecimal lowRateKmsPrice = Mobility.getLowKmsRate(category).multiply(new BigDecimal(lowRateKms));

            Price price = new Price(dayHoursPrice, nightHoursPrice, highRateKmsPrice, lowRateKmsPrice);
            priceMap.put(category, price);
        }
    }

    public BigDecimal getDayHoursCount(){
        return new BigDecimal(dayHalfHoursCount).divide(new BigDecimal(2));
    }
    public BigDecimal getNightHoursCount(){
        return new BigDecimal(nightHalfHoursCount).divide(new BigDecimal(2));
    }
    public BigDecimal getTotalHoursCount(){
        return getDayHoursCount().add(getNightHoursCount());
    }
    public int getTotalKilometersCount(){
        return highRateKms + lowRateKms;
    }


    public Price getPrice(Mobility.Category category){
        return priceMap.get(category);
    }

    private void roundTimes(){
        long roundedStartTime = (long) (Math.floor((double)startDate.getTimeInMillis() / (double)HALF_HOUR_IN_MS) * HALF_HOUR_IN_MS);
        long roundedEndTime = (long) (Math.ceil((double)endDate.getTimeInMillis() / (double)HALF_HOUR_IN_MS) * HALF_HOUR_IN_MS);
        startDate.setTimeInMillis(roundedStartTime);
        endDate.setTimeInMillis(roundedEndTime);
    }

    public class Price implements Serializable{
        public final BigDecimal dayHoursPrice;
        public final BigDecimal nightHoursPrice;

        public final BigDecimal highRateKmsPrice;
        public final BigDecimal lowRateKmsPrice;

        public Price(BigDecimal dayHoursPrice, BigDecimal nightHoursPrice, BigDecimal highRateKmsPrice, BigDecimal lowRateKmsPrice) {
            this.dayHoursPrice = dayHoursPrice;
            this.nightHoursPrice = nightHoursPrice;
            this.highRateKmsPrice = highRateKmsPrice;
            this.lowRateKmsPrice = lowRateKmsPrice;
        }

        public BigDecimal getTotalPrice(){
            return dayHoursPrice.add(nightHoursPrice).add(highRateKmsPrice).add(lowRateKmsPrice);
        }

        public BigDecimal getTimePrice(){
            return dayHoursPrice.add(nightHoursPrice);
        }
        public BigDecimal getDistancePrice(){
            return highRateKmsPrice.add(lowRateKmsPrice);
        }
    }
}
