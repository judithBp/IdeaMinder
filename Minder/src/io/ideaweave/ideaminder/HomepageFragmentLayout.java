package io.ideaweave.ideaminder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import io.ideaweave.ideaminder.R;
import io.ideaweave.models.Idea;
import io.ideaweave.models.IdeaWithOwner;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomepageFragmentLayout extends Fragment implements
		OnClickListener {

	private static final String TAG = "HomepageFragmentLayout";

	private boolean newIdea = false;
	private boolean btnClickable = false;
	private boolean swipable = false;

	private String creator;
	private String ideaTitle;
	private String info;
	private String contact;
	private String ideaId;
	private String creatorId;
	private boolean oldIdea;

	private TextView textIdea;
	private TextView textCreator;
	private Button btnViewOldIdeas;
	private Button btnRefresh;
	private Button btnLike;
	private Button btnDislike;
	private Button btnReport;
	private ImageView lightbulb;
	private AnimationDrawable lightbulbAnimation;
	private RelativeLayout viewOldIdeasLayout;
	private RelativeLayout creatorViewLayout;
	private RelativeLayout buttonsLayout;
	private RelativeLayout lightbulbLayout;
	private RelativeLayout infoLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.homepage_fragment_layout,
				container, false);
		
		final GestureDetector swipe = new GestureDetector(getActivity(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						final int SWIPE_MIN_DISTANCE = 120;
						final int SWIPE_MAX_OFF_PATH = 250;
						final int SWIPE_THRESHOLD_VELOCITY = 150;
						try {
							if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
								return false;
							}
							if (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
								if (swipable) {
									if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
										btnDislike.performClick();
									} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
										btnLike.performClick();
									}
								}
							}
						} catch (Exception e) {
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
		view.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return swipe.onTouchEvent(event);
			}
		});

		textIdea = (TextView) view.findViewById(R.id.text_idea);
		textCreator = (TextView) view.findViewById(R.id.home_fragment_creator);
		viewOldIdeasLayout = (RelativeLayout) view
				.findViewById(R.id.home_fragment_view_old_ideas_layout);
		creatorViewLayout = (RelativeLayout) view
				.findViewById(R.id.home_fragment_creator_layout);
		buttonsLayout = (RelativeLayout) view
				.findViewById(R.id.home_fragment_buttons_layout);
		lightbulbLayout = (RelativeLayout) view
				.findViewById(R.id.home_fragment_lightbulb_layout);
		infoLayout = (RelativeLayout) view
				.findViewById(R.id.home_fragment_info_layout);

		lightbulb = (ImageView) view.findViewById(R.id.home_fragment_lightbulb);
		lightbulb.setBackgroundResource(R.drawable.lightbulb_animation);
		lightbulbAnimation = (AnimationDrawable) lightbulb.getBackground();
		lightbulbAnimation.start();

		btnViewOldIdeas = (Button) view.findViewById(R.id.btn_view_old_ideas);
		btnRefresh = (Button) view.findViewById(R.id.home_fragment_btn_refresh);
		btnLike = (Button) view.findViewById(R.id.btn_like);
		btnDislike = (Button) view.findViewById(R.id.btn_dislike);
		btnReport = (Button) view.findViewById(R.id.btn_report);

		btnViewOldIdeas.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
		btnLike.setOnClickListener(this);
		btnDislike.setOnClickListener(this);
		btnReport.setOnClickListener(this);

		GeneralSettings.language = 0;

		getIdea();
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (newIdea || GeneralSettings.refresh) {
			if(!GeneralSettings.deleteAccount) {
				getIdea();
				GeneralSettings.refresh = false;
				newIdea = false;
			}
			else {
				GeneralSettings.deleteAccount = false;
			}
		}
	}

	public void getIdea() {
		oldIdea = false;

		swipable = false;
		infoLayout.setVisibility(View.GONE);
		buttonsLayout.setVisibility(View.GONE);
		lightbulbLayout.setVisibility(View.VISIBLE);
		lightbulbAnimation.start();
		//if (GeneralSettings.tags) {
			/*new ServerRequestTask(this)
					.execute(
							"select ideas.id, ideas.user_id, ideas.title, ideas.info, ideas.contact from ideas inner join "
									+ "(select tags_ideas.id as idea_id from tags_ideas inner join "
									+ "tags_users on tags_ideas.tag = tags_users.tag) as tags_table on "
									+ "ideas.id = idea_id where ideas.id not in "
									+ "(select ideas.id from ideas inner join rated_ideas "
									+ "on rated_ideas.idea_id = ideas.id and rated_ideas.user_id = ?) "
									+ "and ideas.user_id != ? and ideas.language = ? limit 1;",
							GeneralSettings.userID, GeneralSettings.userID,
							Integer.toString(GeneralSettings.language),
							"get idea");*/
		//} else {
			Callback<IdeaWithOwner> cbIdeas = new Callback<IdeaWithOwner>() {

				@Override
				public void failure(RetrofitError arg0) {
					// TODO Auto-generated method stub
					System.out.println("Problem retrieving ideas: " + arg0.getMessage());
				}

				@Override
				public void success(IdeaWithOwner idea, Response arg1) {
					if (idea == null) {
						lightbulbAnimation.stop();
						lightbulbLayout.setVisibility(View.GONE);
						infoLayout.setVisibility(View.VISIBLE);
						buttonsLayout.setVisibility(View.GONE);
						creatorViewLayout.setVisibility(View.INVISIBLE);
						viewOldIdeasLayout.setVisibility(View.VISIBLE);

						textIdea.setText(R.string.no_new_ideas);
						btnClickable = true;
						swipable = false;
					} else {
						ideaId = idea._id;
						creator = idea.owner.email;
						ideaTitle = idea.title;
						info = idea.brief;
						contact = "";

						//creator = "";
						textIdea.setText(ideaTitle);
						textCreator.setText("");

						lightbulbAnimation.stop();
						lightbulbLayout.setVisibility(View.GONE);
						infoLayout.setVisibility(View.VISIBLE);
						buttonsLayout.setVisibility(View.VISIBLE);
						creatorViewLayout.setVisibility(View.INVISIBLE);
						viewOldIdeasLayout.setVisibility(View.GONE);
						btnClickable = true;
						swipable = true;
						
						/*new ServerRequestTask(this).execute(
								"select name from users where id=?;", creatorId,
								"get name");*/
					}
				}
				
			};
			GeneralSettings.ideaweave.popularIdea("Bearer " + GeneralSettings.token, cbIdeas);
			
			/*new ServerRequestTask(this)
					.execute(
							"select ideas.id, ideas.user_id, ideas.title, ideas.info, ideas.contact from ideas where ideas.id not in (select ideas.id from ideas "
									+ "inner join rated_ideas on rated_ideas.idea_id=ideas.id and rated_ideas.user_id=?) "
									+ "and ideas.user_id != ? and ideas.language = ? limit 1;",
							GeneralSettings.userID, GeneralSettings.userID,
							Integer.toString(GeneralSettings.language),
							"get idea");*/

		//}
	}

	@Override
	public void onClick(View view) {
		if (btnClickable) {
			btnClickable = false;
			switch (view.getId()) {
			/*case R.id.btn_view_old_ideas:
				swipable = false;
				infoLayout.setVisibility(View.GONE);
				lightbulbLayout.setVisibility(View.VISIBLE);
				lightbulbAnimation.start();
				new ServerRequestTask(this)
						.execute(
								"select ideas.id, ideas.user_id, ideas.title, ideas.info, ideas.contact from "
										+ "(select rated_ideas.idea_id as idea_id from rated_ideas where user_id=?) as rating_table inner join ideas "
										+ "on ideas.id = idea_id limit 1;",
								GeneralSettings.userID, "get idea");
				oldIdea = true;
				break;*/
			case R.id.home_fragment_btn_refresh:
				getIdea();
				break;
			case R.id.btn_dislike:
				swipable = false;
				infoLayout.setVisibility(View.GONE);
				lightbulbLayout.setVisibility(View.VISIBLE);
				lightbulbAnimation.start();
				
				GeneralSettings.ideaweave.dislikeIdea("Bearer " + GeneralSettings.token, ideaId, GeneralSettings.userId)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<ResultMessage>() {

					@Override
					public void call(ResultMessage arg0) {
						System.out.print("dislike: " + arg0.message);
						getIdea();
					}
				}, new Action1<Throwable>() {

					@Override
					public void call(Throwable arg0) {
						// TODO Auto-generated method stub
						System.out.println("Problem with disliking the idea: " + arg0.getMessage());
					}
				});
				break;
				
				/*if (oldIdea) {
					new ServerRequestTask(this)
							.execute(
									"select rating from rated_ideas where idea_id=? and user_id=?;",
									Integer.toString(ideaId),
									GeneralSettings.userID, "get rating 0");
				} else {
					new ServerRequestTask(this).execute(
							"insert into rated_ideas(idea_id, user_id, rating) values("
									+ ideaId + ", '" + GeneralSettings.userID
									+ "', 0)",
							"update ideas set dislikes=dislikes+1 where id="
									+ ideaId, "update table");

				}*/
			case R.id.btn_like:
				swipable = false;
				infoLayout.setVisibility(View.GONE);
				lightbulbLayout.setVisibility(View.VISIBLE);
				lightbulbAnimation.start();
				
				GeneralSettings.ideaweave.likeIdea("Bearer " + GeneralSettings.token, ideaId, GeneralSettings.userId)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<ResultMessage>() {

					@Override
					public void call(ResultMessage arg0) {
						next();
					}
				}, new Action1<Throwable>() {

					@Override
					public void call(Throwable arg0) {
						// TODO Auto-generated method stub
						System.out.println("Problem with liking the idea: " + arg0.getMessage());
					}
				});
				
				/*if (oldIdea) {
					new ServerRequestTask(this)
							.execute(
									"select rating from rated_ideas where idea_id=? and user_id=?;",
									Integer.toString(ideaId),
									GeneralSettings.userID, "get rating 1");
				} else {
					new ServerRequestTask(this)
							.execute(
									"insert into rated_ideas(idea_id, user_id, rating) values("
											+ ideaId + ", '"
											+ GeneralSettings.userID + "', 1)",
									"update ideas set likes=likes+1 where id="
											+ ideaId, "update table");

				}*/
				
				break;
			}
		}
	}
	
	public void next() {
		newIdea = true;
		Intent mIntent = new Intent(this.getActivity(),
				ViewSelectedIdea.class);
		mIntent.putExtra("context", "newLiked");
		mIntent.putExtra("idea", ideaTitle);
		mIntent.putExtra("ideaId", ideaId);
		mIntent.putExtra("creator", creator);
		mIntent.putExtra("info", info);
		mIntent.putExtra("contact", contact);
		mIntent.putExtra("language", 0);
		mIntent.putExtra("likes", 0);
		mIntent.putExtra("dislikes", 0);
		startActivity(mIntent);
	}

	/*@Override
	public void handleRequestResult(ArrayList<Object[]> result,
			String requestContext) {
		if (requestContext.equals("check user") && result != null) {

			if (result.isEmpty()) {
				GeneralSettings.language = 0;
				new ServerRequestTask(this).execute(
						"insert into users(id, name, language, email) values('"
								+ GeneralSettings.userID + "', '"
								+ GeneralSettings.userName + "', 0, '"
								+ GeneralSettings.email + "')", "update table");

			} else {
				GeneralSettings.language = (int) result.get(0)[0];
			}
			new ServerRequestTask(this).execute(
					"select tag from tags_users where id=?;",
					GeneralSettings.userID, "get tags");
		} else if (requestContext.equals("get tags")) {
			int n = result.size();
			GeneralSettings.tagsArray = new String[n];
			if (n == 0) {
				GeneralSettings.tags = false;
			} else {
				GeneralSettings.tags = true;
				for (int i = 0; i < n; i++) {
					GeneralSettings.tagsArray[i] = (String) result.get(i)[0];
				}
			}
			getIdea();
		} else if (requestContext.equals("get idea")) {
			if (result.isEmpty()) {
				lightbulbAnimation.stop();
				lightbulbLayout.setVisibility(View.GONE);
				infoLayout.setVisibility(View.VISIBLE);
				buttonsLayout.setVisibility(View.GONE);
				creatorViewLayout.setVisibility(View.INVISIBLE);
				viewOldIdeasLayout.setVisibility(View.VISIBLE);

				textIdea.setText(R.string.no_new_ideas);
				btnClickable = true;
				swipable = false;
			} else {
				ideaId = (int) result.get(0)[0];
				creatorId = (String) result.get(0)[1];
				idea = (String) result.get(0)[2];
				info = (String) result.get(0)[3];
				contact = (String) result.get(0)[4];

				new ServerRequestTask(this).execute(
						"select name from users where id=?;", creatorId,
						"get name");
			}
		} else if (requestContext.equals("get name")) {
			creator = (String) result.get(0)[0];
			textIdea.setText(idea);
			textCreator.setText("by " + creator);

			lightbulbAnimation.stop();
			lightbulbLayout.setVisibility(View.GONE);
			infoLayout.setVisibility(View.VISIBLE);
			buttonsLayout.setVisibility(View.VISIBLE);
			creatorViewLayout.setVisibility(View.VISIBLE);
			viewOldIdeasLayout.setVisibility(View.GONE);
			btnClickable = true;
			swipable = true;
		} else if (requestContext.contains("get rating")) {
			int rating = (int) result.get(0)[0];

			if (requestContext.equals("get rating -1")) {
				updateRating(rating, -1, "reports=reports+1");
			} else if (requestContext.equals("get rating 0")) {
				updateRating(rating, 0, "dislikes=dislikes+1");
			} else {
				updateRating(rating, 1, "likes=likes+1");
			}
		}
	}
*/
	/*private void updateRating(int oldRating, int newRating, String rating) {
		switch (oldRating) {
		case -1:
			new ServerRequestTask(this).execute(
					"update ideas set reports=reports-1 where id=" + ideaId,
					"delete from rated_ideas where idea_id=" + ideaId
							+ " and user_id='" + GeneralSettings.userID + "'",
					"insert into rated_ideas(idea_id, user_id, rating) values("
							+ ideaId + ", '" + GeneralSettings.userID + "', "
							+ newRating + ")", "update ideas set " + rating
							+ " where id=" + ideaId, "update table");
			break;
		case 0:
			new ServerRequestTask(this).execute(
					"update ideas set dislikes=dislikes-1 where id=" + ideaId,
					"delete from rated_ideas where idea_id=" + ideaId
							+ " and user_id='" + GeneralSettings.userID + "'",
					"insert into rated_ideas(idea_id, user_id, rating) values("
							+ ideaId + ", '" + GeneralSettings.userID + "', "
							+ newRating + ")", "update ideas set " + rating
							+ " where id=" + ideaId, "update table");
			break;
		case 1:
			new ServerRequestTask(this).execute(
					"update ideas set likes=likes-1 where id=" + ideaId,
					"delete from rated_ideas where idea_id=" + ideaId
							+ " and user_id='" + GeneralSettings.userID + "'",
					"insert into rated_ideas(idea_id, user_id, rating) values("
							+ ideaId + ", '" + GeneralSettings.userID + "', "
							+ newRating + ")", "update ideas set " + rating
							+ " where id=" + ideaId, "update table");
			break;
		}

	}*/

	public void setNewIdeaParameter(boolean bool) {
		newIdea = bool;
	}

	public void setBtnClickable(boolean clickable) {
		btnClickable = clickable;
	}

}
