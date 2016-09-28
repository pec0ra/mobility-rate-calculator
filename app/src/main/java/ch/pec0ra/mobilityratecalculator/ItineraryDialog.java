package ch.pec0ra.mobilityratecalculator;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by basile on 26.09.16.
 */
public class ItineraryDialog extends DialogFragment {

    NoticeDialogListener mListener;


    public interface NoticeDialogListener {
        void onDialogPositiveClick(String from, String to, boolean isTwoWay);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.itinerary_dialog, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                .setTitle(getString(R.string.find_itinerary))
                // Add action buttons
                .setPositiveButton(R.string.calculate_distance, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        final String from = ((TextView) v.findViewById(R.id.itinerary_from)).getText().toString();
                        final String to = ((TextView) v.findViewById(R.id.itinerary_to)).getText().toString();
                        final boolean isTwoWay = ((CheckBox) v.findViewById(R.id.two_way_checkbox)).isChecked();
                        mListener.onDialogPositiveClick(from, to, isTwoWay);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ItineraryDialog.this.getDialog().cancel();
                    }
                });
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
