package io.ideaweave.ideaminder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import io.ideaweave.dialogs.AlertDecisionDialogFragment;
import io.ideaweave.dialogs.NoTextDialogFragment;
import io.ideaweave.ideaminder.R;
import io.ideaweave.models.Idea;
import io.ideaweave.models.Tag;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView.CommaTokenizer;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateIdea extends FragmentActivity implements
		OnItemSelectedListener, EnablesDialog, TextWatcher, ActiveActivity {

	private static final String TAG = "CreateIdeaFragmentActivity";
	private boolean textChanged = false;

	private Spinner languageSpinner;
	private EditText ideaText;
	private MultiAutoCompleteTextView tagsText;
	private EditText infoText;
	private EditText contactText;
	private Button btnCreate;

	private int languagePosition;
	private String language;
	private String idea;
	private String[] tags;
	private String info;
	private String contact;
	private String context;
	private String ideaId;

	private ScrollView contentView;
	private RelativeLayout lightbulbLayout;
	private AnimationDrawable lightbulbAnimation;
	private ImageView lightbulb;
	
	//new code
	private Idea ideaBody;
	private String[] tagObjList;
	private String[] tagsData;
	public int count;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_idea);
		
		GeneralSettings.activity = this;

		languageSpinner = (Spinner) findViewById(R.id.language_idea);
		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(this, R.array.language_array,
						android.R.layout.simple_spinner_item);
		adapterSpinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		languageSpinner.setAdapter(adapterSpinner);
		languageSpinner.setOnItemSelectedListener(this);

		ideaText = (EditText) findViewById(R.id.new_idea);
		ideaText.addTextChangedListener(this);

		tagsText = (MultiAutoCompleteTextView) findViewById(R.id.create_tags);
		
		contentView = (ScrollView) findViewById(R.id.create_scroll_view);
		lightbulbLayout = (RelativeLayout) findViewById(R.id.create_lightbulb_layout);
		lightbulb = (ImageView) findViewById(R.id.create_lightbulb);
		lightbulb.setBackgroundResource(R.drawable.lightbulb_animation);
		lightbulbAnimation = (AnimationDrawable) lightbulb.getBackground();
		
		Callback<List<Tag>> cbTags = new Callback<List<Tag>>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO handle error
				Toast.makeText((Context) GeneralSettings.activity, "Problem with retrieving the tags", Toast.LENGTH_LONG).show();
			    lightbulbAnimation.start();
			    System.out.println(arg0.getMessage());
			    Log.d(TAG, arg0.getMessage());
			}

			@Override
			public void success(List<Tag> tags, Response arg1) {
				GeneralSettings.tagsArray = tags;
				tagsData = new String[GeneralSettings.tagsArray.size()];
				for(int i = 0; i < GeneralSettings.tagsArray.size(); i++) {
					tagsData[i] = GeneralSettings.tagsArray.get(i).title;
				}
				ArrayAdapter<String> adapterMultiAutoText = new ArrayAdapter<String>((Context) GeneralSettings.activity, R.layout.tag_list_item, tagsData);
				tagsText.setThreshold(1);
				tagsText.setAdapter(adapterMultiAutoText);
				tagsText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
				
				//lightbulbAnimation.start();
				lightbulbLayout.setVisibility(View.GONE);
				contentView.setVisibility(View.VISIBLE);
			}
		};
		GeneralSettings.ideaweave.getTags(cbTags);

		tagsText.addTextChangedListener(this);
		
		infoText = (EditText) findViewById(R.id.info_create);
		infoText.addTextChangedListener(this);

		contactText = (EditText) findViewById(R.id.create_contact);
		contactText.addTextChangedListener(this);

		btnCreate = (Button) findViewById(R.id.btn_create);

		Bundle extras = getIntent().getExtras();
		context = extras.getString("context");


		if (context.equals("edit")) {

			ideaId = extras.getString("ideaId");
			idea = extras.getString("idea");
			tags = extras.getStringArray("tags");
			info = extras.getString("info");
			contact = extras.getString("contact");
			language = extras.getString("language");

			ideaText.setFocusable(false);
			ideaText.setEnabled(false);

			ideaText.setText(idea);
			
			infoText.setText(info);
			contactText.setText(contact);
			languageSpinner.setSelection(languagePosition);

			btnCreate.setBackgroundResource(R.drawable.minder_ok);

			textChanged = false;
		} else {
			btnCreate.setBackgroundResource(R.drawable.minder_add);
		}
	}

	@Override
	public void onBackPressed() {
		if (textChanged && !ideaText.getText().toString().equals("")) {
			showDialog();
		} else {
			super.onBackPressed();
		}
	}

	public void create(View v) {
		idea = ideaText.getText().toString();

		if (idea.equals("") || idea.equals("Title")) {
			DialogFragment dialog = new NoTextDialogFragment();
			dialog.show(getSupportFragmentManager(), "NoTextDialogFragment");
		} else {
			info = infoText.getText().toString();
			if (info.equals("Additional information")) {
				info = "";
			}
			if (!tagsText.getText().toString().contains("#")) {
				tags = tagsText.getText().toString().split(",\\s");
			} else {
				tags = new String[0];
			}
			languagePosition = languageSpinner.getSelectedItemPosition();
			contact = contactText.getText().toString();
			if (contact.equals("Contact information")) {
				contact = "";
			}

			if (context.equals("edit")) {
				contentView.setVisibility(View.GONE);
				lightbulbLayout.setVisibility(View.VISIBLE);
				lightbulbAnimation.start();
				
				ideaBody = new Idea(idea, info, GeneralSettings.languages[languagePosition]);
				GeneralSettings.ideaweave.updateIdea("Bearer " + GeneralSettings.token, ideaId, ideaBody)
				.observeOn(AndroidSchedulers.mainThread()).
				subscribe(new Action1<ResultMessage>() {

					@Override
					public void call(ResultMessage m) {
						lightbulbAnimation.stop();
						lightbulbLayout.setVisibility(View.GONE);
						contentView.setVisibility(View.VISIBLE);
						Toast.makeText((Context) GeneralSettings.activity, "The idea has been updated", Toast.LENGTH_LONG).show();
						Log.d(TAG, m._id + ", " + m.title + ", " + m.owner);

						GeneralSettings.activity.next();
					}
				}, new Action1<Throwable>() {

					@Override
					public void call(Throwable t1) {
						lightbulbAnimation.stop();
						lightbulbLayout.setVisibility(View.GONE);
						contentView.setVisibility(View.VISIBLE);
						Toast.makeText((Context) GeneralSettings.activity, t1.getMessage(), Toast.LENGTH_LONG).show();
						Log.d(TAG, "problem with updating idea: " + t1.getMessage());
					}
				});

			} else {
				contentView.setVisibility(View.GONE);
				lightbulbLayout.setVisibility(View.VISIBLE);
				lightbulbAnimation.start();

				/*tagObjList = new String[tags.length];
				for(int i = 0; i < tags.length; i++) {
					count = i;
					GeneralSettings.ideaweave.createTag(new Tag(tags[count]))
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<ResultMessage>() {

						@Override
						public void call(ResultMessage m) {
							tagObjList[count] = m._id;
							count++;
						}
						
					}, new Action1<Throwable>() {

						@Override
						public void call(Throwable arg0) {
							Toast.makeText((Context) GeneralSettings.activity, "Problem with saving the tags", Toast.LENGTH_LONG).show();
							Log.d(TAG, arg0.getMessage());
							System.out.println(arg0.getMessage());
						}
					});
				}
				count = 0;*/
				
				ideaBody = new Idea(idea, info, GeneralSettings.languages[languagePosition]);
				GeneralSettings.ideaweave.createIdea("Bearer " + GeneralSettings.token, ideaBody)
				.observeOn(AndroidSchedulers.mainThread()).
				subscribe(new Action1<ResultMessage>() {

					@Override
					public void call(ResultMessage m) {
						lightbulbAnimation.stop();
						lightbulbLayout.setVisibility(View.GONE);
						contentView.setVisibility(View.VISIBLE);
						Toast.makeText((Context) GeneralSettings.activity, "The idea has been created", Toast.LENGTH_LONG).show();
						Log.d(TAG, m._id + ", " + m.title + ", " + m.owner);

						GeneralSettings.activity.next();
					}
				}, new Action1<Throwable>() {

					@Override
					public void call(Throwable t1) {
						lightbulbAnimation.stop();
						lightbulbLayout.setVisibility(View.GONE);
						contentView.setVisibility(View.VISIBLE);
						Toast.makeText((Context) GeneralSettings.activity, t1.getMessage(), Toast.LENGTH_LONG).show();
						Log.d(TAG, "problem with creating idea: " + t1.getMessage());
					}
				});
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		language = (String) parent.getItemAtPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		language = "English";
	}

	private void showDialog() {
		DialogFragment newFragment = AlertDecisionDialogFragment
				.newInstance("Do you really want to leave this page? All entered information will be lost!");
		newFragment.show(getSupportFragmentManager(), TAG);
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

	@Override
	public void positiveSelected() {
		super.onBackPressed();
	}

	@Override
	public void negativeSelected() {
	}

	@Override
	public void next() {
		super.onBackPressed();
	}
}