package be.emich.labs.villohelper.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import be.emich.villo.R;


public class AlertDialogFragment extends DialogFragment {

	public static final int TYPE_ABOUT = 1;
	public static final int TYPE_ERROR_PARSING = 2;
	public static final int TYPE_ERROR_CONNECTING = 3;

	public static AlertDialogFragment newInstance(int dialogtype) {

		if (dialogtype == TYPE_ABOUT) {
			AlertDialogFragment frag = new AlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", R.string.dialog_title_about);
			args.putInt("dialogcontent", R.string.dialog_content_about);
			args.putInt("negativebutton", R.string.dialog_action_feedback);
			args.putInt("positivebutton", R.string.dialog_action_ok);
			frag.setArguments(args);
			return frag;
		}else if (dialogtype == TYPE_ERROR_PARSING) {
			AlertDialogFragment frag = new AlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", R.string.dialog_title_error_parsing);
			args.putInt("dialogcontent", R.string.dialog_content_error_parsing);
			args.putInt("positivebutton", R.string.dialog_action_ok);
			frag.setArguments(args);
			return frag;
		}else if (dialogtype == TYPE_ERROR_CONNECTING) {
			AlertDialogFragment frag = new AlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", R.string.dialog_title_error_connecting);
			args.putInt("dialogcontent", R.string.dialog_content_error_connecting);
			args.putInt("positivebutton", R.string.dialog_action_ok);
			frag.setArguments(args);
			return frag;
		}
		
		return null;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int title = getArguments().getInt("title", -1);
		int dialogContent = getArguments().getInt("dialogcontent", -1);
		int positiveButton = getArguments().getInt("positivebutton", -1);
		int negativeButton = getArguments().getInt("negativebutton", -1);

		Builder builder = new AlertDialog.Builder(getActivity());
		if (title != -1) {
			builder.setTitle(title);
		}
		if (dialogContent != -1) {
			builder.setMessage(dialogContent);
		}
		if (positiveButton != -1) {
			builder.setPositiveButton(positiveButton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((AlertDialogListener) getActivity())
									.doPositiveClick();
						}
					});
		}
		if (negativeButton != -1) {
			builder.setNegativeButton(negativeButton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((AlertDialogListener) getActivity())
									.doNegativeClick();
						}
					});
		}

		return builder.create();
	}

	public static interface AlertDialogListener {
		public void doPositiveClick();

		public void doNegativeClick();
	}
}
