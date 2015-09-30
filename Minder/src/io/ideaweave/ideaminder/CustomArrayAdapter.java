package io.ideaweave.ideaminder;

import io.ideaweave.ideaminder.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String> {

	private Context context;
	private String[] values;
	private String[] creators;

	public CustomArrayAdapter(Context context, String[] values,
			String[] creators) {
		super(context, R.layout.view_ideas_list_view_layout, values);
		this.context = context;
		this.values = values;
		this.creators = creators;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View ideaView = inflater.inflate(R.layout.view_ideas_list_view_layout,
				parent, false);
		TextView idea = (TextView) ideaView
				.findViewById(R.id.view_ideas_list_view_idea);
		TextView creator = (TextView) ideaView
				.findViewById(R.id.view_ideas_list_view_creator);
		idea.setText(values[position]);
		if (creators != null) {
			creator.setText("by " + creators[position]);
		} else {
			creator.setText("by me");
		}
		if (position % 2 == 1) {
			ideaView.setBackgroundColor(ideaView.getResources().getColor(
					R.color.white));
		} else {
			ideaView.setBackgroundColor(ideaView.getResources().getColor(
					R.color.grey));
		}
		return ideaView;
	}
}
