package com.alex.floatindicatorlayout.adapter.fragmentpageradapter;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
public class TitleFragmentPagerAdapter extends FragmentPagerAdapter
{
	protected List<Fragment> list;
	protected List<String> listTitle;
	public TitleFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		list = new ArrayList<Fragment>();
		notifyDataSetChanged();
	}
	public TitleFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.list = list;
		notifyDataSetChanged();
	}
	public TitleFragmentPagerAdapter(FragmentManager fm, List<Fragment> list,List<String> listTitle) {
		super(fm);
		this.list = list;
		this.listTitle = listTitle;
		notifyDataSetChanged();
	}
	public void addTitle(List<String> listTitle){
		this.listTitle = listTitle;
		notifyDataSetChanged();
	}
	public void addItem(Fragment fragment)
	{
		list.add(fragment);
		notifyDataSetChanged();
	}
	public void addItem(List<Fragment> list)
	{
		this.list = list;
		notifyDataSetChanged();
	}
	@Override
	public Fragment getItem(int position){
		return ((list == null) ? null:list.get(position));
	}
	@Override
	public int getCount(){
		return ((list == null) ? 0:list.size());
	}
	@Override
	public CharSequence getPageTitle(int position){
		return (listTitle==null) ? "" : listTitle.get(position);
	}
}
