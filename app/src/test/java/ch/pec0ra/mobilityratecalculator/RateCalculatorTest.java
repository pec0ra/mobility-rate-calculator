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

import android.util.Log;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit testing for the Rate Calculator
 */
public class RateCalculatorTest {

    @Test
    public void timePrice_isCorrect() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd kk:mm");
        Calendar fromDate = Calendar.getInstance();
        Calendar toDate = Calendar.getInstance();

        fromDate.set(Calendar.MINUTE, 0);
        fromDate.set(Calendar.SECOND, 0);
        fromDate.set(Calendar.MILLISECOND, 0);
        toDate.set(Calendar.MINUTE, 0);
        toDate.set(Calendar.SECOND, 0);
        toDate.set(Calendar.MILLISECOND, 0);

        toDate.add(Calendar.HOUR, 1);
        int kms = 1;

        for(int i=1; i <= 48; i++) {
            for(float j=1f; j <= 80; j+=0.5f) {

                RateCalculator rateCalculator = new RateCalculator(fromDate, toDate, kms);
                rateCalculator.calculate();

                RateCalculator.Price priceBudget = rateCalculator.getPrice(Mobility.Category.BUDGET);
                assertTrue(Mobility.getDayHourlyRate(Mobility.Category.BUDGET).multiply(new BigDecimal(j)).compareTo(priceBudget.getTimePrice()) == 0);

                RateCalculator.Price priceMicro = rateCalculator.getPrice(Mobility.Category.MICRO);
                assertTrue(Mobility.getDayHourlyRate(Mobility.Category.MICRO).multiply(new BigDecimal(j)).compareTo(priceMicro.getTimePrice()) == 0);

                RateCalculator.Price priceCombi = rateCalculator.getPrice(Mobility.Category.COMBI);
                assertTrue(Mobility.getDayHourlyRate(Mobility.Category.COMBI).multiply(new BigDecimal(j)).compareTo(priceCombi.getTimePrice()) == 0);

                RateCalculator.Price priceEmotion = rateCalculator.getPrice(Mobility.Category.EMOTION);
                assertTrue(Mobility.getDayHourlyRate(Mobility.Category.EMOTION).multiply(new BigDecimal(j)).compareTo(priceEmotion.getTimePrice()) == 0);


                toDate.add(Calendar.MINUTE, 30);
            }
            fromDate.add(Calendar.MINUTE, 30);
            // Reinitialize the toDate to 1 hour from now
            toDate.setTime(fromDate.getTime());
            toDate.add(Calendar.HOUR, 1);
        }


        assertEquals(4, 2 + 2);
    }

    @Test
    public void kmsPrice_isCorrect() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd kk:mm");
        Calendar fromDate = Calendar.getInstance();
        Calendar toDate = Calendar.getInstance();
        fromDate.set(Calendar.MINUTE, 0);
        fromDate.set(Calendar.SECOND, 0);
        fromDate.set(Calendar.MILLISECOND, 0);
        toDate.set(Calendar.MINUTE, 0);
        toDate.set(Calendar.SECOND, 0);
        toDate.set(Calendar.MILLISECOND, 0);
        toDate.add(Calendar.HOUR, 1);

        int kms = 1;

        for(; kms <= 200; kms++){

            RateCalculator rateCalculator = new RateCalculator(fromDate, toDate, kms);
            rateCalculator.calculate();

            RateCalculator.Price priceBudget = rateCalculator.getPrice(Mobility.Category.BUDGET);
            assertTrue(Mobility.getHighKmsRate(Mobility.Category.BUDGET).multiply(new BigDecimal(kms)).compareTo(priceBudget.getDistancePrice()) == 0);

            RateCalculator.Price priceMicro = rateCalculator.getPrice(Mobility.Category.MICRO);
            assertTrue(Mobility.getHighKmsRate(Mobility.Category.MICRO).multiply(new BigDecimal(kms)).compareTo(priceMicro.getDistancePrice()) == 0);

            RateCalculator.Price priceCombi = rateCalculator.getPrice(Mobility.Category.COMBI);
            assertTrue(Mobility.getHighKmsRate(Mobility.Category.COMBI).multiply(new BigDecimal(kms)).compareTo(priceCombi.getDistancePrice()) == 0);

            RateCalculator.Price priceEmotion = rateCalculator.getPrice(Mobility.Category.EMOTION);
            assertTrue(Mobility.getHighKmsRate(Mobility.Category.EMOTION).multiply(new BigDecimal(kms)).compareTo(priceEmotion.getDistancePrice()) == 0);

        }
    }

    @Test
    public void totalPrice_isCorrect() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd kk:mm");
        Calendar fromDate = Calendar.getInstance();
        Calendar toDate = Calendar.getInstance();

        fromDate.set(Calendar.MINUTE, 0);
        fromDate.set(Calendar.SECOND, 0);
        fromDate.set(Calendar.MILLISECOND, 0);
        toDate.set(Calendar.MINUTE, 0);
        toDate.set(Calendar.SECOND, 0);
        toDate.set(Calendar.MILLISECOND, 0);

        toDate.add(Calendar.HOUR, 1);
        int kms = 1;

        for(int i=1; i <= 48; i++) {
            for(float j=1f; j <= 80; j+=0.5f) {
                for(; kms <= 200; kms++) {

                    RateCalculator rateCalculator = new RateCalculator(fromDate, toDate, kms);
                    rateCalculator.calculate();

                    RateCalculator.Price priceBudget = rateCalculator.getPrice(Mobility.Category.BUDGET);
                    BigDecimal totalPrice = Mobility.getDayHourlyRate(Mobility.Category.BUDGET).multiply(new BigDecimal(j)).add(Mobility.getHighKmsRate(Mobility.Category.BUDGET).multiply(new BigDecimal(kms)));
                    assertTrue(totalPrice.compareTo(priceBudget.getTotalPrice()) == 0);

                    RateCalculator.Price priceMicro = rateCalculator.getPrice(Mobility.Category.MICRO);
                    totalPrice = Mobility.getDayHourlyRate(Mobility.Category.MICRO).multiply(new BigDecimal(j)).add(Mobility.getHighKmsRate(Mobility.Category.MICRO).multiply(new BigDecimal(kms)));
                    assertTrue(totalPrice.compareTo(priceMicro.getTotalPrice()) == 0);

                    RateCalculator.Price priceCombi = rateCalculator.getPrice(Mobility.Category.COMBI);
                    totalPrice = Mobility.getDayHourlyRate(Mobility.Category.COMBI).multiply(new BigDecimal(j)).add(Mobility.getHighKmsRate(Mobility.Category.COMBI).multiply(new BigDecimal(kms)));
                    assertTrue(totalPrice.compareTo(priceCombi.getTotalPrice()) == 0);

                    RateCalculator.Price priceEmotion = rateCalculator.getPrice(Mobility.Category.EMOTION);
                    totalPrice = Mobility.getDayHourlyRate(Mobility.Category.EMOTION).multiply(new BigDecimal(j)).add(Mobility.getHighKmsRate(Mobility.Category.EMOTION).multiply(new BigDecimal(kms)));
                    assertTrue(totalPrice.compareTo(priceEmotion.getTotalPrice()) == 0);


                }
                toDate.add(Calendar.MINUTE, 30);
            }
            fromDate.add(Calendar.MINUTE, 30);
            // Reinitialize the toDate to 1 hour from now
            toDate.setTime(fromDate.getTime());
            toDate.add(Calendar.HOUR, 1);
        }


        assertEquals(4, 2 + 2);
    }
}