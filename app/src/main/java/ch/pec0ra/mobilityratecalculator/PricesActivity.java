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

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Calendar;

import ch.pec0ra.mobilityratecalculator.rates.Mobility;

public class PricesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String FROM_DATE_EXTRA = "from_date_extra";
    public static final String TO_DATE_EXTRA = "to_date_extra";
    public static final String DISTANCE_EXTRA = "distance_extra";

    public static final String HOUR_FORMAT = "HH:mm";

    private DecimalFormat timeDF = new DecimalFormat("0.0");
    private DecimalFormat moneyDF = new DecimalFormat("0.00");

    ProgressDialog dialog;
    private Calendar fromDate;
    private Calendar toDate;
    private int distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prices);

        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        }

        fromDate = (Calendar) getIntent().getSerializableExtra(FROM_DATE_EXTRA);
        toDate = (Calendar) getIntent().getSerializableExtra(TO_DATE_EXTRA);
        distance = (int) getIntent().getSerializableExtra(DISTANCE_EXTRA);

        Mobility.SubscriptionType subscriptionType = getPreferredSubscriptionType();
        initSpinner(subscriptionType);
    }

    private void initSpinner(Mobility.SubscriptionType subscriptionType) {
        Spinner spinner = findViewById(R.id.subscription_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(subscriptionType.getIntValue());
    }

    private Mobility.SubscriptionType getPreferredSubscriptionType() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        int defaultValue = getResources().getInteger(R.integer.default_customer_type);
        int customerTypeInt = sharedPreferences.getInt(getString(R.string.customer_type), defaultValue);
        return Mobility.SubscriptionType.fromInt(customerTypeInt);
    }

    private void savePreferredSubscriptionType(Mobility.SubscriptionType subscriptionType) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.customer_type), subscriptionType.getIntValue());
        editor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void calculatePrices(Calendar fromDate, Calendar toDate, int distance, Mobility mobilityRate) {
        startLoad(getString(R.string.calculating));
        new CalculateAsyncTask(fromDate, toDate, distance, mobilityRate).execute();
    }

    private void addPriceLayouts(RateCalculator rateCalculator) {
        String duration = MessageFormat.format(getResources().getText(R.string.time_h_m_format).toString(), rateCalculator.getTotalHoursCount(), rateCalculator.getMinutesCount());
        ((TextView) findViewById(R.id.duration_text)).setText(duration);
        ((TextView) findViewById(R.id.kilometers_text)).setText(getString(R.string.kilometers_count, rateCalculator.getTotalKilometersCount()));

        LinearLayout pricesContainer = findViewById(R.id.prices_container);
        if (pricesContainer == null) {
            return;
        }
        pricesContainer.removeAllViews();
        for (Mobility.Category category : Mobility.Category.values()) {
            LayoutInflater inflater = LayoutInflater.from(getBaseContext());
            View v = inflater.inflate(R.layout.price_item, pricesContainer, false);

            ((TextView) v.findViewById(R.id.category_name)).setText(Mobility.categoryToString(category, getBaseContext()));
            RateCalculator.Price price = rateCalculator.getPrice(category);
            if (price.getAccessFee() != null) {
                v.findViewById(R.id.access_fee_title_row).setVisibility(View.VISIBLE);
                v.findViewById(R.id.access_fee_value_row).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.access_fee_price)).setText(getString(R.string.chf, moneyDF.format(price.getAccessFee())));
            } else {
                v.findViewById(R.id.access_fee_title_row).setVisibility(View.GONE);
                v.findViewById(R.id.access_fee_value_row).setVisibility(View.GONE);
            }
            ((TextView) v.findViewById(R.id.time_price)).setText(getString(R.string.chf, moneyDF.format(price.getTimePrice())));
            ((TextView) v.findViewById(R.id.distance_price)).setText(getString(R.string.chf, moneyDF.format(price.getDistancePrice())));
            ((TextView) v.findViewById(R.id.total_price)).setText(getString(R.string.chf, moneyDF.format(price.getTotalPrice())));

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

    private void startLoad(String message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = ProgressDialog.show(this, "", message, true, false);
    }

    private void endLoad() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = null;
    }

    @Subscribe
    public void onEvent(CalculationFinishedEvent event) {
        addPriceLayouts(event.rateCalculator);
        endLoad();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Mobility.SubscriptionType subscriptionType = Mobility.SubscriptionType.fromInt(i);
        savePreferredSubscriptionType(subscriptionType);
        calculatePrices(fromDate, toDate, distance, subscriptionType.getRate());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        calculatePrices(fromDate, toDate, distance, Mobility.SubscriptionType.MEMBER.getRate());
    }

    private static class CalculateAsyncTask extends AsyncTask<Void, Void, Void> {

        private final RateCalculator rateCalculator;

        CalculateAsyncTask(Calendar fromDate, Calendar toDate, int kms, Mobility mobilityRate) {
            rateCalculator = new RateCalculator(fromDate, toDate, kms, mobilityRate);
        }

        @Override
        protected Void doInBackground(Void... params) {
            rateCalculator.calculate();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            EventBus.getDefault().post(new CalculationFinishedEvent(rateCalculator));
        }
    }

    private static class CalculationFinishedEvent {
        final RateCalculator rateCalculator;

        CalculationFinishedEvent(RateCalculator rateCalculator) {
            this.rateCalculator = rateCalculator;
        }
    }
}
