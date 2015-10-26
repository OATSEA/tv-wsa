package adapter;

import java.util.ArrayList;


import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.teachervirus.R;

public class StringViewAdapter extends BaseAdapter {

	static class ViewHolder {
		AppCompatTextView txvTitle;
	}

	private Context context;
	private ArrayList<String> items = null;
	private final LayoutInflater mLayoutInflater;
	private Integer animation;



	private Animation getAnimation() {
		return AnimationUtils.loadAnimation(this.context, this.animation);
	}

	public StringViewAdapter(Context context, ArrayList<String> items) {
		this.context = context;
		this.items = items;
		this.mLayoutInflater = LayoutInflater.from(context);


	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {

		final ViewHolder vh;
		try {
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.view_string_item, parent, false);
				vh = new ViewHolder();
				vh.txvTitle = (AppCompatTextView) convertView
						.findViewById(R.id.txvTitle);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			vh.txvTitle.setText(getItem(position).toString());
			convertView.startAnimation(getAnimation());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.items.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}	
}