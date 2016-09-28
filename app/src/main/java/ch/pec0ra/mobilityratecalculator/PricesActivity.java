package ch.pec0ra.mobilityratecalculator;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

public class PricesActivity extends AppCompatActivity {

    public static final String RATE_CALCULATOR_EXTRA = "rate_calculator_extra";

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

        ((TextView)findViewById(R.id.day_hours_count)).setText(timeDF.format(rateCalculator.getDayHoursCount()));
        ((TextView)findViewById(R.id.night_hours_count)).setText(timeDF.format(rateCalculator.getNightHoursCount()));
        ((TextView)findViewById(R.id.total_hours_count)).setText(timeDF.format(rateCalculator.getTotalHoursCount()));
        ((TextView)findViewById(R.id.kilometers_text)).setText(getString(R.string.kilometers_count, rateCalculator.getTotalKilometersCount()));

        addPriceLayouts();
    }

    private void addPriceLayouts(){
        LinearLayout pricesContainer = (LinearLayout)findViewById(R.id.prices_container);
        if(pricesContainer == null){
            return;
        }
        for(Mobility.Category category : Mobility.Category.values()){
            LayoutInflater inflater = LayoutInflater.from(getBaseContext());
            View v = inflater.inflate(R.layout.price_item, null, false);

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
