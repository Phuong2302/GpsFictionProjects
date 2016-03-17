package com.sdesimeur.android.gpsfiction.activities;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;


public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<MyTabFragment> tabsFragments = new ArrayList<MyTabFragment>();
    private HashMap<String, Integer> string2position = new HashMap<String, Integer>();
    private GpsFictionActivity gpsFictionActivity = null;

    public AppSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private void defineTabsFragments() {
        this.tabsFragments.clear();
        for (int position = 0; position < this.getCount(); position++) {
            String s = this.getStringArray()[position];
            try {
                Class myclass = Class.forName(this.getClassName(position));
                MyTabFragment myTabFragment = (MyTabFragment) (myclass.newInstance());
                this.tabsFragments.add(position, myTabFragment);
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            this.tabsFragments.get(position).register(this.gpsFictionActivity);
            this.string2position.put(s, position);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        FragmentManager manager = ((Fragment) object).getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove((Fragment) object);
        trans.commit();
        super.destroyItem(container, position, object);
    }

    private Resources getResources() {
        return this.getGpsFictionActivity().getResources();
    }

    public GpsFictionActivity getGpsFictionActivity() {
        return this.gpsFictionActivity;
    }

    public void setGpsFictionActivity(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
        this.defineTabsFragments();
    }

    public String getClassName(int position) {
        return ("com.sdesimeur.android.gpsfiction.activities." + this.getStringArray()[position] + "Fragment");
    }

    @Override
    public Fragment getItem(int i) {
        return this.tabsFragments.get(i);
    }

    @Override
    public int getCount() {
        return this.getStringArray().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //    int id = (this.tabsFragments.get(position)).getNameId();
        //    return getResources().getString(id);
        return null;
    }

    private String[] getStringArray() {
        //return getResources().getStringArray(R.array.tabsOrderedNamesFragments);
        return null;
    }

    /*
        public View getPageTitleView(int position) {
            XmlPullParser parser = this.getResources().getXml(R.layout.tab_title);
            AttributeSet attributes = Xml.asAttributeSet(parser);
            View view = new View (this.gpsFictionActivity,attributes);
            ((TextView)view.findViewById(R.id.tabTitle)).setText(this.getPageTitle(position));
            // TODO Auto-generated method
            return view;
        }
    */
    public MyTabFragment getFragment(String s) {
        return this.tabsFragments.get(this.string2position.get(s));
    }
}

