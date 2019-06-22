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

package ch.pec0ra.mobilityratecalculator.rates

import java.math.BigDecimal
import java.util.*

class BusinessRate : Mobility() {
    override fun initRetMap(): HashMap<Category, Rate> {
        val ratesMap = hashMapOf(
                Category.BUDGET to Rate(BigDecimal("1.86"), BigDecimal("1.86"), BigDecimal("0.51"), BigDecimal("0.51")),
                Category.MICRO to Rate(BigDecimal("2.32"), BigDecimal("2.32"), BigDecimal("0.6"), BigDecimal("0.6")),
                Category.ECONOMY to Rate(BigDecimal("2.32"), BigDecimal("2.32"), BigDecimal("0.6"), BigDecimal("0.6")),
                Category.ELECTRO to Rate(BigDecimal("2.32"), BigDecimal("2.32"), BigDecimal("0.6"), BigDecimal("0.6")),
                Category.COMBI to Rate(BigDecimal("2.79"), BigDecimal("2.79"), BigDecimal("0.74"), BigDecimal("0.74")),
                Category.CABRIO to Rate(BigDecimal("3.71"), BigDecimal("3.71"), BigDecimal("0.88"), BigDecimal("0.88")),
                Category.EMOTION to Rate(BigDecimal("3.71"), BigDecimal("3.71"), BigDecimal("0.88"), BigDecimal("0.88")),
                Category.MINIVAN to Rate(BigDecimal("3.71"), BigDecimal("3.71"), BigDecimal("0.88"), BigDecimal("0.88")),
                Category.TRANSPORT to Rate(BigDecimal("3.71"), BigDecimal("3.71"), BigDecimal("0.88"), BigDecimal("0.88")),
                Category.PREMIUM to Rate(BigDecimal("8.36"), BigDecimal("8.36"), BigDecimal("1.39"), BigDecimal("1.39"))
        )
        return ratesMap
    }
}