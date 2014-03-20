package it.unict.dieei.semm.trackmyself.util;

import it.unict.dieei.semm.trackmyself.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Favorite> {

	public ListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private List<Favorite> items;

	public ListAdapter(Context context, int resource, List<Favorite> items) {

		super(context, resource, items);

		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		TextView tt = null;
		TextView tt1 = null;
		TextView tt2 = null;
		LayoutInflater vi;
		vi = LayoutInflater.from(getContext());
		v = vi.inflate(R.layout.itemlistrow, null);

		tt = (TextView) v.findViewById(R.id.routeNameItem);
		tt1 = (TextView) v.findViewById(R.id.fromTimeItem);
		tt2 = (TextView) v.findViewById(R.id.toTimeItem);

		Favorite p = items.get(position);

		GregorianCalendar from = new GregorianCalendar();
		GregorianCalendar to = new GregorianCalendar();
		from.setTimeInMillis(p.getFromTime());
		to.setTimeInMillis(p.getToTime());

		if (p != null) {

			if (tt != null) {
				tt.setText("" + p.getFavoriteName());
			}
			if (tt1 != null) {

				tt1.setText(Utils.getFormattedDate(from));
			}
			if (tt2 != null) {

				tt2.setText(Utils.getFormattedDate(to));
			}

		}

		return v;

	}

}