package net.sourcewalker.garanbot;

import net.sourcewalker.garanbot.api.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author T. Gumprecht
 *
 */
public class ItemsAdapter extends BaseAdapter {
	
	private LayoutInflater layoutInflater;
	private Item[] entries;
	
	public ItemsAdapter(Context context, Item[] entries){
		// Cache the LayoutInflate to avoid asking for a new one each time.
		layoutInflater = LayoutInflater.from(context);
		this.entries = entries;
	}

	public int getCount() {
		return entries.length;
	}

	public Object getItem(int position) {
		return entries[position];
	}

	public long getItemId(int position) {
		return entries[position].getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item, null);
		}
		TextView name = (TextView) convertView.findViewById(R.id.firstLine);
		name.setText(entries[position].getName());
		TextView manufacturer = (TextView) convertView
				.findViewById(R.id.secondLine);
		manufacturer.setText(entries[position].getManufacturer());
		return convertView;
	}
}
