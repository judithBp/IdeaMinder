package io.ideaweave.ideaminder;

import java.util.ArrayList;

import io.ideaweave.dialogs.AlertDecisionDialogFragment;
import io.ideaweave.ideaminder.R;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class Settings extends FragmentActivity implements
		OnItemSelectedListener, EnablesDialog,
		TextWatcher {

	private static final String TAG = "SettingsFragmentActivity";

	private boolean textChanged = false;

	private int languagePosition;
	private String language;
	private String tags = "";

	private Spinner languageSpinner;
	private MultiAutoCompleteTextView tagsText;
	private Button btnSave;
	private RelativeLayout deleteAccountLayout;
	private Button deleteAccount;
	private RelativeLayout btnLayout;

	private LinearLayout contentLayout;
	private RelativeLayout lightbulbLayout;
	private AnimationDrawable lightbulbAnimation;
	private ImageView lightbulb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);

		languageSpinner = (Spinner) findViewById(R.id.settings_language);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.language_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		languageSpinner.setAdapter(adapter);
		languageSpinner.setOnItemSelectedListener(this);

		tagsText = (MultiAutoCompleteTextView) findViewById(R.id.settings_tags);
		/*new ServerRequestTask(this).execute(
				"select tag from tags_users union select tag from tags_ideas;",
				"get all tags");*/
		tagsText.addTextChangedListener(this);
		btnSave = (Button) findViewById(R.id.settings_btn_save);
		deleteAccount = (Button) findViewById(R.id.settings_delete_account);
		deleteAccountLayout = (RelativeLayout) findViewById(R.id.settings_delete_account_layout);
		btnLayout = (RelativeLayout) findViewById(R.id.settings_button_layout);
		
		contentLayout = (LinearLayout) findViewById(R.id.settings_content);
		lightbulbLayout = (RelativeLayout) findViewById(R.id.settings_lightbulb_layout);
		lightbulb = (ImageView) findViewById(R.id.settings_lightbulb);
		lightbulb.setBackgroundResource(R.drawable.lightbulb_animation);
		lightbulbAnimation = (AnimationDrawable) lightbulb.getBackground();
		lightbulbAnimation.start();

	}

	@Override
	public void onBackPressed() {
		if (languageSpinner.getSelectedItemPosition() != languagePosition
				&& textChanged) {
			showDialog();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void positiveSelected() {
		super.onBackPressed();
	}

	@Override
	public void negativeSelected() {

	}

	private void showDialog() {
		DialogFragment newFragment = AlertDecisionDialogFragment
				.newInstance("Do you really want to leave this page? All entered information will be lost!");
		newFragment.show(getSupportFragmentManager(), TAG);
	}

	private void setText() {
		/*languageSpinner.setSelection(GeneralSettings.language);
		int n = GeneralSettings.tagsArray.length;
		if (n > 0) {
			for (int i = 0; i < GeneralSettings.tagsArray.length; i++) {
				tags += GeneralSettings.tagsArray[i] + ", ";
			}
		} else {
			tags = "#";
		}
		tagsText.setText(tags);*/
	}

	public void save(View v) {
		/*GeneralSettings.refresh = true;
		tags = tagsText.getText().toString();
		String tagString = "";
		if (!tags.contains("#")) {
			String[] tagsSplit = tags.split(",\\s");
			GeneralSettings.tagsArray = tagsSplit;
			if (tagsSplit.length > 0) {
				tagString = "insert into tags_users(tag, id) values";
				for (int i = 0; i < tagsSplit.length; i++) {
					if (i != tagsSplit.length - 1) {
						tagString += "('" + tagsSplit[i] + "', '"
								+ GeneralSettings.userID + "'), ";
					} else {
						tagString += "('" + tagsSplit[i] + "', '"
								+ GeneralSettings.userID + "')";
					}
				}
				GeneralSettings.tags = true;
			}
		} else {
			GeneralSettings.tags = false;
		}

		new ServerRequestTask(this).execute(
				"delete from tags_users where id='" + GeneralSettings.userID
						+ "'",
				tagString,
				"update users set language="
						+ languageSpinner.getSelectedItemPosition()
						+ " where id='" + GeneralSettings.userID + "'",
				"update table language");

		GeneralSettings.language = languageSpinner.getSelectedItemPosition();
		GeneralSettings.refresh = true;

		contentLayout.setVisibility(View.GONE);
		deleteAccountLayout.setVisibility(View.GONE);
		btnLayout.setVisibility(View.GONE);
		lightbulbLayout.setVisibility(View.VISIBLE);
		lightbulbAnimation.start();*/
	}
	
	public void deleteAccount(View v) {
		super.onBackPressed();
		GeneralSettings.deleteAccount = true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		language = (String) parent.getItemAtPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		textChanged = true;
	}

	/*@Override
	public void handleRequestResult(ArrayList<Object[]> result,
			String requestContext) {
		if (requestContext.equals("get all tags")) {
			int n = result.size();
			String[] tagsArray = new String[n];
			for (int i = 0; i < n; i++) {
				tagsArray[i] = (String) result.get(i)[0];
			}
			tagsText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
			ArrayAdapter<String> adapterTags = new ArrayAdapter<String>(this,
					R.layout.tag_list_item, tagsArray);
			tagsText.setThreshold(1);
			tagsText.setAdapter(adapterTags);

			lightbulbAnimation.stop();
			lightbulbLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
			deleteAccountLayout.setVisibility(View.VISIBLE);
			btnLayout.setVisibility(View.VISIBLE);
			setText();
		} else if (requestContext.equals("update table language")) {
			super.onBackPressed();
		}
	}*/

}
