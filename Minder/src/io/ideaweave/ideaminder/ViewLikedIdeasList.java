package io.ideaweave.ideaminder;

import io.ideaweave.models.Idea;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class ViewLikedIdeasList extends ViewIdeasList {

	private String info;
	private String contact;
	private int pos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GeneralSettings.activity = this;
	}

	/*@Override
	public void handleRequestResult(ArrayList<Object[]> result,
			String requestContext) {
		if (requestContext.equals("get ideas")) {
			super.handleRequestResult(result, requestContext);
			int n = result.size();
			if (n > 0) {
				creators = new String[n];
				for (int i = 0; i < n; i++) {
					creators[i] = (String) result.get(i)[2];
				}
				CustomArrayAdapter adapter = new CustomArrayAdapter(this,
						titles, creators);
				super.ideasList.setAdapter(adapter);

				super.lightbulbAnimation.stop();
				super.lightbulbLayout.setVisibility(View.GONE);
				super.contentLayout.setVisibility(View.VISIBLE);
			}
		} else {
			info = (String) result.get(0)[0];
			contact = (String) result.get(0)[1];

			Intent mIntent = new Intent(this, ViewSelectedIdea.class);
			mIntent.putExtra("context", "likedIdea");
			mIntent.putExtra("idea", super.titles[pos]);
			mIntent.putExtra("ideaId", super.ideaIds[pos]);
			mIntent.putExtra("creator", creators[pos]);
			mIntent.putExtra("info", info);
			mIntent.putExtra("contact", contact);
			startActivity(mIntent);
		}
	}*/

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		super.contentLayout.setVisibility(View.GONE);
		super.lightbulbLayout.setVisibility(View.VISIBLE);
		super.lightbulbAnimation.start();
		pos = position;
		
		Callback<Idea> cbIdea = new Callback<Idea>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				System.out.println("Problem retrieving the idea");
			}

			@Override
			public void success(Idea idea, Response arg1) {
				info = idea.brief;
				contact = "";

				ownNext();
			}
		};
		GeneralSettings.ideaweave.getIdea(super.ideaIds[pos], cbIdea);
		
		/*new ServerRequestTask(this).execute(
				"select info, contact from ideas where id=?;",
				Integer.toString(super.ideaIds[pos]), "get selected idea");
	*/}

	public void ownNext() {
		Intent mIntent = new Intent(this, ViewSelectedIdea.class);
		mIntent.putExtra("context", "likedIdea");
		mIntent.putExtra("idea", super.titles[pos]);
		mIntent.putExtra("ideaId", super.ideaIds[pos]);
		mIntent.putExtra("creator", super.creators[pos]);
		mIntent.putExtra("info", info);
		mIntent.putExtra("contact", contact);
		startActivity(mIntent);
	}
}
