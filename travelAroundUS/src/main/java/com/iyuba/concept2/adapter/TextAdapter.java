package com.iyuba.concept2.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.configation.ConfigManager;

/**
 * 
 * 主题界面gridview的adapter
 */
public class TextAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Map<String, Object>> mArrayList = new ArrayList<Map<String, Object>>();
	private LayoutInflater layoutInflater;
	ViewHolder viewHolder;
	Map<String, Object> Item;
	private int selectColor;

	public TextAdapter(Context context) {
		mContext = context;
		layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		selectColor = ConfigManager.Instance().loadInt("textColor");
		XmlResourceParser xml = mContext.getResources()
				.getXml(R.xml.text_color);
		try {
			// 遍历xml，构建arraylist
			xml.next();
			int eventType = xml.getEventType();
			Map<String, Object> map = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xml.getName();
				switch (eventType) {
				// 文档开始
				case XmlPullParser.START_DOCUMENT:
					break;
				// 开始节点
				case XmlPullParser.START_TAG:
					if ("item".equals(nodeName)) {
						map = new HashMap<String, Object>();
					} else if ("name".equals(nodeName)) {
						map.put("text", xml.nextText());

					} else if ("colorid".equals(nodeName)) {
						map.put("color", xml.nextText());
					}
					break;
				// 结束节点
				case XmlPullParser.END_TAG:
					if ("item".equals(nodeName)) {
						mArrayList.add(map);
						map = null;
					}
					break;
				default:
					break;
				}
				eventType = xml.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return mArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		Item = mArrayList.get(pos);
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.boundary_item,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.bkg = (ImageView) convertView.findViewById(R.id.back);
			viewHolder.text = (TextView) convertView.findViewById(R.id.text);
			viewHolder.checked = (ImageView) convertView.findViewById(R.id.ok);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.bkg.setBackgroundColor(Color.parseColor((String) (Item
				.get("color"))));
		viewHolder.text.setText((String) Item.get("text"));
		if (Color.parseColor((String) Item.get("color")) == selectColor) {
			viewHolder.checked.setVisibility(View.VISIBLE);
		} else {
			viewHolder.checked.setVisibility(View.GONE);
		}
		return convertView;
	}

	public void setOK(int color) {
		selectColor = color;
	}

	static class ViewHolder {

		ImageView bkg;
		TextView text;
		ImageView checked;
	}
}
