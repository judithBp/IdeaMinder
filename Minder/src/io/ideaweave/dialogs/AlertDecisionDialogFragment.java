package io.ideaweave.dialogs;

import io.ideaweave.ideaminder.EnablesDialog;
import io.ideaweave.ideaminder.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AlertDecisionDialogFragment extends DialogFragment {

	public static AlertDecisionDialogFragment newInstance(String message) {
		AlertDecisionDialogFragment dialogFragment = new AlertDecisionDialogFragment();
		Bundle args = new Bundle();
		args.putString("message", message);
		dialogFragment.setArguments(args);
		return dialogFragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String message = getArguments().getString("message");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(message)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								((EnablesDialog) getActivity())
										.positiveSelected();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								getDialog().cancel();
								((EnablesDialog) getActivity())
										.negativeSelected();
							}
						});
		return builder.create();
	}
}
