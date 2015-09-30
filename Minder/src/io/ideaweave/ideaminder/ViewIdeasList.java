package io.ideaweave.ideaminder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import io.ideaweave.ideaminder.R;
import io.ideaweave.models.Idea;
import io.ideaweave.models.IdeaWithOwner;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class ViewIdeasList extends Activity implements
		OnItemClickListener, ActiveActivity {

	public String[] ideaIds;
	public String[] titles;
	public String[] creators;

	public RelativeLayout messageLayout;
	public TextView message;
	public RelativeLayout contentLayout;
	public RelativeLayout lightbulbLayout;
	public ImageView lightbulb;
	public AnimationDrawable lightbulbAnimation;
	public ListView ideasList;
	public String context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_ideas_list);

		Bundle extras = getIntent().getExtras();
		context = extras.getString("context");

		messageLayout = (RelativeLayout) findViewById(R.id.list_view_message_layout);
		message = (TextView) findViewById(R.id.list_view_message);
		contentLayout = (RelativeLayout) findViewById(R.id.content_view_layout);
		lightbulbLayout = (RelativeLayout) findViewById(R.id.list_view_lightbulb_layout);
		lightbulb = (ImageView) findViewById(R.id.list_view_lightbulb);
		lightbulb.setBackgroundResource(R.drawable.lightbulb_animation);
		lightbulbAnimation = (AnimationDrawable) lightbulb.getBackground();
		lightbulbAnimation.start();

		ideasList = (ListView) findViewById(R.id.ideas_list_view);
		ideasList.setOnItemClickListener(this);
	}

	protected void getIdeas() {
		if (context.equals("myIdeas")) {
			Callback<List<Idea>> cbMyIdea = new Callback<List<Idea>>() {

				@Override
				public void failure(RetrofitError arg0) {
					// TODO Auto-generated method stub
					System.out.println("Problem retrieving own ideas");
				}

				@Override
				public void success(List<Idea> ideas, Response arg1) {
					if(ideas.isEmpty()) {
						lightbulbAnimation.stop();
						lightbulbLayout.setVisibility(View.GONE);
						messageLayout.setVisibility(View.VISIBLE);
						message.setText(R.string.no_created_ideas);
					}
					else {
						ideaIds = new String[ideas.size()];
						titles = new String[ideas.size()];
						for (int i = 0; i < ideas.size(); i++) {
							ideaIds[i] = ideas.get(i)._id;
							titles[i] = ideas.get(i).title;
						}
						next();
					}
				}
			};
			GeneralSettings.ideaweave.getMyIdeas("Bearer " + GeneralSettings.token, GeneralSettings.userId, cbMyIdea);
			
			/*new ServerRequestTask(this).execute(
					"select id, title from ideas where user_id=?;",
					GeneralSettings.userID, "get ideas");*/
		} else {
			Callback<List<IdeaWithOwner>> cbLikedIdeas = new Callback<List<IdeaWithOwner>>() {

				@Override
				public void failure(RetrofitError arg0) {
					// TODO Auto-generated method stub
					System.out.println("Problem retrieving liked ideas");
				}

				@Override
				public void success(List<IdeaWithOwner> ideas, Response arg1) {
					if(ideas.isEmpty()) {
						lightbulbAnimation.stop();
						lightbulbLayout.setVisibility(View.GONE);
						messageLayout.setVisibility(View.VISIBLE);
						message.setText(R.string.no_liked_ideas);
					}
					else {
						ideaIds = new String[ideas.size()];
						titles = new String[ideas.size()];
						creators = new String[ideas.size()];
						for (int i = 0; i < ideas.size(); i++) {
							ideaIds[i] = ideas.get(i)._id;
							titles[i] = ideas.get(i).title;
							creators[i] = ideas.get(i).owner.email;
						}
						next();
					}
				}
			};
			GeneralSettings.ideaweave.getLikedIdeas("Bearer " + GeneralSettings.token, GeneralSettings.userId, cbLikedIdeas);
			
			/*new ServerRequestTask(this)
					.execute(
							"select ideas.id, ideas.title, users.name from users, "
									+ "ideas inner join rated_ideas on ideas.id=rated_ideas.idea_id "
									+ "where rated_ideas.user_id=? and rated_ideas.rating=1 and ideas.user_id=users.id;",
							GeneralSettings.userID, "get ideas");*/
		}
	}

	@Override
	public void onResume() {
		getIdeas();
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void next() {
		if (context.equals("myIdeas")) {
			CustomArrayAdapter adapter = new CustomArrayAdapter(this,
					titles, null);
			ideasList.setAdapter(adapter);
		}
		else {
			CustomArrayAdapter adapter = new CustomArrayAdapter(this, titles, creators);
			ideasList.setAdapter(adapter);
		}
		lightbulbAnimation.stop();
		lightbulbLayout.setVisibility(View.GONE);
		contentLayout.setVisibility(View.VISIBLE);
	}

	/*@Override
	public void handleRequestResult(ArrayList<Object[]> result,
			String requestContext) {
		int n = result.size();
		if (n == 0) {
			lightbulbAnimation.stop();
			lightbulbLayout.setVisibility(View.GONE);
			messageLayout.setVisibility(View.VISIBLE);
			if (context.equals("myIdeas")) {
				message.setText(R.string.no_created_ideas);
			} else {
				message.setText(R.string.no_liked_ideas);
			}
		} else {
			ideaIds = new Integer[n];
			titles = new String[n];
			for (int i = 0; i < n; i++) {
				ideaIds[i] = (Integer) result.get(i)[0];
				titles[i] = (String) result.get(i)[1];
			}
			if (context.equals("myIdeas")) {
				CustomArrayAdapter adapter = new CustomArrayAdapter(this,
						titles, null);
				ideasList.setAdapter(adapter);

				lightbulbAnimation.stop();
				lightbulbLayout.setVisibility(View.GONE);
				contentLayout.setVisibility(View.VISIBLE);
			}
		}
	}*/

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}
}
