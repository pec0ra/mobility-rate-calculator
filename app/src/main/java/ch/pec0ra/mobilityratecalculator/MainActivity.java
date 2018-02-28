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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static ch.pec0ra.mobilityratecalculator.AnimationUtils.ANIMATION_COLOR;
import static ch.pec0ra.mobilityratecalculator.AnimationUtils.CENTER_X_EXTRA;
import static ch.pec0ra.mobilityratecalculator.AnimationUtils.CENTER_Y_EXTRA;
import static ch.pec0ra.mobilityratecalculator.ItineraryActivity.DISTANCE_EXTRA;

public class MainActivity extends AppCompatActivity{

    public static final int REQUEST_DISTANCE = 4;

    private static final String DATE_PICKER_DIALOG = "date_picker_dialog";
    private static final String TIME_PICKER_DIALOG = "time_picker_dialog";

    EditText fromField;
    EditText toField;
    EditText kmInput;

    Calendar fromDate;
    Calendar toDate;
    Calendar tmpDate;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startCalculateJob());

        fromField = findViewById(R.id.from_field);
        toField = findViewById(R.id.to_field);
        kmInput = findViewById(R.id.kilometers_input);

        fromDate = Calendar.getInstance();
        long roundedTime = (long) (Math.ceil((double)fromDate.getTimeInMillis() / (double)TimeUnit.MINUTES.toMillis(30)) * TimeUnit.MINUTES.toMillis(30));
        fromDate.setTimeInMillis(roundedTime);
        toDate = Calendar.getInstance();
        toDate.setTimeInMillis(roundedTime + TimeUnit.HOURS.toMillis(1));

        findViewById(R.id.itinerary_button).setOnTouchListener(new OnClickTouchListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItineraryActivity.class);
                intent.putExtra(CENTER_X_EXTRA, getX());
                intent.putExtra(CENTER_Y_EXTRA, getY());
                intent.putExtra(ANIMATION_COLOR, ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, REQUEST_DISTANCE);
            }
        });

        updateFields();

        fromField.setOnClickListener(new MyClickListener(true));
        toField.setOnClickListener(new MyClickListener(false));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DISTANCE) {
            if (resultCode == Activity.RESULT_OK) {
                long distance = data.getLongExtra(DISTANCE_EXTRA, 0);
                kmInput.setText(String.valueOf(distance));
            }
        }
    }

    private class MyClickListener implements View.OnClickListener {

        private final boolean isStart;
        private final Calendar cal;

        MyClickListener(boolean isStart){
            this.isStart = isStart;
            if(isStart){
                this.cal = fromDate;
            } else {
                this.cal = toDate;
            }
        }

        @Override
        public void onClick(View v) {
            DatePickerDialog dialog = DatePickerDialog.newInstance(new MyDateSetListener(isStart), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.setMinDate(Calendar.getInstance());
            dialog.show(getFragmentManager(), DATE_PICKER_DIALOG);
        }
    }

    private class MyDateSetListener implements DatePickerDialog.OnDateSetListener {
        private final Calendar cal;
        private final boolean isStart;

        MyDateSetListener(boolean isStart){
            this.isStart = isStart;
            if(isStart){
                this.cal = fromDate;
            } else {
                this.cal = toDate;
            }
        }

        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            tmpDate = Calendar.getInstance();
            tmpDate.setTime(cal.getTime());
            tmpDate.set(Calendar.YEAR, year);
            tmpDate.set(Calendar.MONTH, monthOfYear);
            tmpDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            TimePickerDialog dialog = TimePickerDialog.newInstance(new MyTimeSetListener(isStart), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            dialog.setTimeInterval(1, 30);
            dialog.show(getFragmentManager(), TIME_PICKER_DIALOG);
        }
    }
    private class MyTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        private final Calendar cal;
        private final boolean isStart;

        MyTimeSetListener(boolean isStart){
            this.isStart = isStart;
            if(isStart){
                this.cal = fromDate;
            } else {
                this.cal = toDate;
            }
        }

        @Override
        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
            cal.setTime(tmpDate.getTime());
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            tmpDate = null;

            correctDates(isStart);

            updateFields();
        }
    }

    private void correctDates(boolean isStart) {
        long difference = toDate.getTimeInMillis() - fromDate.getTimeInMillis();
        if(isStart){
            if(difference < TimeUnit.HOURS.toMillis(1)){
                toDate.setTimeInMillis(fromDate.getTimeInMillis() + TimeUnit.HOURS.toMillis(1));
            }
        } else {
            if(difference < TimeUnit.HOURS.toMillis(1)){
                fromDate.setTimeInMillis(toDate.getTimeInMillis() - TimeUnit.HOURS.toMillis(1));
            }
        }
    }

    private void updateFields() {
        fromField.setText(DateFormat.format("dd.MM kk:mm", fromDate));
        toField.setText(DateFormat.format("dd.MM kk:mm", toDate));
    }

    private void startCalculateJob() {
        int kms;
        try {
            kms = Integer.parseInt(kmInput.getText().toString());
        } catch(NumberFormatException e){
            kmInput.setError(getString(R.string.wrong_format));
            return;
        }

        startLoad(getString(R.string.calculating));
        new CalculateAsyncTask(fromDate, toDate, kms).execute();
    }

    private void showPrices(RateCalculator rateCalculator) {
        Intent intent = new Intent(this, PricesActivity.class);
        intent.putExtra(PricesActivity.RATE_CALCULATOR_EXTRA, rateCalculator);
        startActivity(intent);
    }

    private void startLoad(String message){
        if(dialog != null){
            dialog.dismiss();
        }
        dialog = ProgressDialog.show(this, "", message, true, false);
    }
    private void endLoad(){
        if(dialog != null) {
            dialog.dismiss();
        }
        dialog = null;
    }

    @Subscribe
    public void onEvent(CalculationFinishedEvent event){
        showPrices(event.rateCalculator);
        endLoad();
    }

    private static class CalculateAsyncTask extends AsyncTask<Void, Void, Void>{

        private final RateCalculator rateCalculator;

        CalculateAsyncTask(Calendar fromDate, Calendar toDate, int kms){
            rateCalculator = new RateCalculator(fromDate, toDate, kms);
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
