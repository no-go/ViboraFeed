package de.vibora.viborafeed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

/**
 * Das ist ein Ja-Nein Fenster. Das Quit dialog Fragment wird genutzt, wenn man
 * in {@link MainActivity} auf den Back Button klickt.
 */
public class QuitDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.quit_dialog, null))
                // Add action buttons
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity callingActivity = (MainActivity) getActivity();
                        callingActivity.onUserExit();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        QuitDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
