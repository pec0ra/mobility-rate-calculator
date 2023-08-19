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

class YoungRate : Mobility() {
    override fun initRetMap(): HashMap<Category, Rate> {
        return hashMapOf(
                Category.BUDGET to Rate(BigDecimal("2.0"), BigDecimal("2.0"), BigDecimal("0.60"), BigDecimal("0.60"), null),
                Category.ECONOMY to Rate(BigDecimal("2.5"), BigDecimal("2.5"), BigDecimal("0.70"), BigDecimal("0.70"), null),
                Category.COMBI to Rate(BigDecimal("3.0"), BigDecimal("3.0"), BigDecimal("0.80"), BigDecimal("0.80"), null),
                Category.CABRIO to Rate(BigDecimal("4.50"), BigDecimal("4.50"), BigDecimal("1.10"), BigDecimal("1.10"), null),
                Category.EMOTION to Rate(BigDecimal("4.50"), BigDecimal("4.50"), BigDecimal("1.10"), BigDecimal("1.10"), null),
                Category.MINIVAN to Rate(BigDecimal("4.50"), BigDecimal("4.50"), BigDecimal("1.10"), BigDecimal("1.10"), null),
                Category.TRANSPORT to Rate(BigDecimal("4.50"), BigDecimal("4.50"), BigDecimal("1.10"), BigDecimal("1.10"), null)
        )
    }
}