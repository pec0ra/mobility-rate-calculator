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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Locale;

public class PricesActivity extends AppCompatActivity {

    public static final String RATE_CALCULATOR_EXTRA = "rate_calculator_extra";

    public static final String HOUR_FORMAT = "HH:mm";

    private RateCalculator rateCalculator;

    private DecimalFormat timeDF = new DecimalFormat("0.0");
    private DecimalFormat moneyDF = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prices);

        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
        }

        rateCalculator = (RateCalculator) getIntent().getSerializableExtra(RATE_CALCULATOR_EXTRA);

        String duration = MessageFormat.format(getResources().getText(R.string.time_h_m_format).toString(), rateCalculator.getTotalHoursCount(), rateCalculator.getMinutesCount());
        ((TextView)findViewById(R.id.duration_text)).setText(duration);
        ((TextView)findViewById(R.id.kilometers_text)).setText(getString(R.string.kilometers_count, rateCalculator.getTotalKilometersCount()));

        addPriceLayouts();
    }

    private void addPriceLayouts(){
        LinearLayout pricesContainer = findViewById(R.id.prices_container);
        if(pricesContainer == null){
            return;
        }
        for(Mobility.Category category : Mobility.Category.values()){
            LayoutInflater inflater = LayoutInflater.from(getBaseContext());
            View v = inflater.inflate(R.layout.price_item, pricesContainer, false);

            ((TextView)v.findViewById(R.id.category_name)).setText(Mobility.categoryToString(category, getBaseContext()));
            RateCalculator.Price price = rateCalculator.getPrice(category);
            ((TextView)v.findViewById(R.id.time_price)).setText(getString(R.string.chf, moneyDF.format(price.getTimePrice())));
            ((TextView)v.findViewById(R.id.distance_price)).setText(getString(R.string.chf, moneyDF.format(price.getDistancePrice())));
            ((TextView)v.findViewById(R.id.total_price)).setText(getString(R.string.chf, moneyDF.format(price.getTotalPrice())));

            pricesContainer.addView(v);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
