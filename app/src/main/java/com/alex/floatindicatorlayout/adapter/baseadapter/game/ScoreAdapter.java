package com.alex.floatindicatorlayout.adapter.baseadapter.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alex.floatindicatorlayout.R;


import org.alex.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class ScoreAdapter extends BaseAdapter
{
	protected List<String> list;
	protected Context context;
	public ScoreAdapter(Context context) {
		this.list = new ArrayList<String>();
		this.context = context;
	}
	/**更新适配器*/
	public void updateItem(String obj,int position){
		list.set(position, obj);
		notifyDataSetChanged();
	}
	public void updateItem(List<String> list){
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void addItem(String obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<String> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void removeItem(int position){
		if(list == null || list.size()<=0 || position >= list.size() || position<0){
			return;
		}
		this.list.remove(position);
		notifyDataSetChanged();
	}
	@Override
	public int getCount(){
		return (list==null) ? 0:list.size();
	}
	@Override
	public Object getItem(int position){
		return (list==null) ? null:list.get(position);
	}
	@Override
	public long getItemId(int position){
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(list == null || list.size()<=0){
			return convertView;
		}
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_fragment_game_before_score, parent, false);
			holder.tv =(TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tv.setText(list.get(position));
		holder.tv.setOnClickListener(new MyOnClickListener(position));
		return convertView;
	}
	private final class MyOnClickListener implements View.OnClickListener
	{
		private int position;
		public MyOnClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			LogUtil.e("position = "+position);
		}
	}
	protected final class ViewHolder{
		public TextView tv;
	}
}
