/*
 *     Copyright (C) 2016 Basile
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
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by basile on 27.09.16.
 */
public class ItineraryConfirmationDialog extends DialogFragment {

    private DistanceCalculator.Itinerary itinerary;
    NoticeDialogListener mListener;


    public interface NoticeDialogListener {
        void onDialogPositiveClick(int distance);
    }

    public void setItinerary(DistanceCalculator.Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.itinerary_confirmation_dialog, null);

        final String twoWay;
        if (itinerary.isTwoWay) {
            twoWay = getString(R.string.two_way);
        } else {
            twoWay = getString(R.string.one_way);
        }

        int distance = (int) Math.ceil((double) itinerary.distance / 1000);
        if (itinerary.isTwoWay) {
            distance = distance * 2;
        }
        final ImageView image = (ImageView) v.findViewById(R.id.map_image);
        image.setImageBitmap(itinerary.image);
        ((TextView) v.findViewById(R.id.from_text_view)).setText(getString(R.string.from_format, itinerary.from));
        ((TextView) v.findViewById(R.id.to_text_view)).setText(getString(R.string.to_format, itinerary.to));
        ((TextView) v.findViewById(R.id.two_way_text_view)).setText(twoWay);
        ((TextView) v.findViewById(R.id.total_distance_text_view)).setText(getString(R.string.distance_format, distance));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final int finalDistance = distance;
        builder.setView(v)
                .setTitle(getString(R.string.itinerary))
                // Add action buttons
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(finalDistance);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ItineraryConfirmationDialog.this.getDialog().cancel();
                    }
                });
        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
