package be.emich.labs.villohelper.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.AdapterView.OnItemClickListener;
import be.emich.villo.R;


public class AlertDialogFragment extends DialogFragment {

	private static final String NEUTRALBUTTON = "neutralbutton";
	private static final String OPTIONS = "options";
	private static final String POSITIVEBUTTON = "positivebutton";
	private static final String NEGATIVEBUTTON = "negativebutton";
	private static final String DIALOGCONTENT = "dialogcontent";
	private static final String TITLE = "title";
	
	public static final int TYPE_ABOUT = 1;
	public static final int TYPE_ERROR_PARSING = 2;
	public static final int TYPE_ERROR_CONNECTING = 3;
	public static final int TYPE_MAP_MODE = 4;

	public static AlertDialogFragment newInstance(int dialogtype) {

		if (dialogtype == TYPE_ABOUT) {
			AlertDialogFragment frag = new AlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt(TITLE, R.string.dialog_title_about);
			args.putInt(DIALOGCONTENT, R.string.dialog_content_about);
			args.putInt(NEGATIVEBUTTON, R.string.dialog_action_feedback);
			args.putInt(POSITIVEBUTTON, R.string.dialog_action_ok);
			frag.setArguments(args);
			return frag;
		}else if (dialogtype == TYPE_ERROR_PARSING) {
			AlertDialogFragment frag = new AlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt(TITLE, R.string.dialog_title_error_parsing);
			args.putInt(DIALOGCONTENT, R.string.dialog_content_error_parsing);
			args.putInt(POSITIVEBUTTON, R.string.dialog_action_ok);
			frag.setArguments(args);
			return frag;
		}else if (dialogtype == TYPE_ERROR_CONNECTING) {
			AlertDialogFragment frag = new AlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt(TITLE, R.string.dialog_title_error_connecting);
			args.putInt(DIALOGCONTENT, R.string.dialog_content_error_connecting);
			args.putInt(POSITIVEBUTTON, R.string.dialog_action_ok);
			frag.setArguments(args);
			return frag;
		}else if (dialogtype == TYPE_MAP_MODE){
			AlertDialogFragment frag = new AlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt(TITLE, R.string.dialog_title_map_mode);
			args.putInt(OPTIONS, R.array.array_map_mode);
			args.putInt(NEUTRALBUTTON, R.string.dialog_action_cancel);
			frag.setArguments(args);
			return frag;
		}
		
		return null;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int title = getArguments().getInt(TITLE, -1);
		int dialogContent = getArguments().getInt(DIALOGCONTENT, -1);
		int positiveButton = getArguments().getInt(POSITIVEBUTTON, -1);
		int negativeButton = getArguments().getInt(NEGATIVEBUTTON, -1);
		int neutralButton = getArguments().getInt(NEUTRALBUTTON, -1);
		int options = getArguments().getInt(OPTIONS,-1);

		Builder builder = new AlertDialog.Builder(getActivity());
		if (title != -1) {
			builder.setTitle(title);
		}
		if (dialogContent != -1) {
			builder.setMessage(dialogContent);
		}
		if (options != -1){
			builder.setItems(options, (AlertDialogListener) getActivity());
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
		if (neutralButton != -1) {
			builder.setNeutralButton(neutralButton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((AlertDialogListener) getActivity())
									.doNeutralClick();
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

	public static interface AlertDialogListener extends DialogInterface.OnClickListener {
		public void doPositiveClick();
		public void doNeutralClick();
		public void doNegativeClick();
	}
}
