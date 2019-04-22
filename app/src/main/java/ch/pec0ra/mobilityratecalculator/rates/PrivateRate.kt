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

class PrivateRate : Mobility() {
    override fun initRetMap(): HashMap<Category, Rate> {
        val ratesMap = hashMapOf(
                Category.BUDGET to Rate(BigDecimal("2.0"), BigDecimal("2.0"), BigDecimal("0.55"), BigDecimal("0.55")),
                Category.MICRO to Rate(BigDecimal("2.5"), BigDecimal("2.5"), BigDecimal("0.65"), BigDecimal("0.65")),
                Category.ECONOMY to Rate(BigDecimal("2.5"), BigDecimal("2.5"), BigDecimal("0.65"), BigDecimal("0.65")),
                Category.ELECTRO to Rate(BigDecimal("2.5"), BigDecimal("2.5"), BigDecimal("0.65"), BigDecimal("0.65")),
                Category.COMBI to Rate(BigDecimal("3.0"), BigDecimal("3.0"), BigDecimal("0.8"), BigDecimal("0.8")),
                Category.CABRIO to Rate(BigDecimal("4.0"), BigDecimal("4.0"), BigDecimal("0.95"), BigDecimal("0.95")),
                Category.EMOTION to Rate(BigDecimal("4.0"), BigDecimal("4.0"), BigDecimal("0.95"), BigDecimal("0.95")),
                Category.MINIVAN to Rate(BigDecimal("4.0"), BigDecimal("4.0"), BigDecimal("0.95"), BigDecimal("0.95")),
                Category.TRANSPORT to Rate(BigDecimal("4.0"), BigDecimal("4.0"), BigDecimal("0.95"), BigDecimal("0.95"))
        )
        return ratesMap
    }
}