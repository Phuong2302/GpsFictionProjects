package com.sdesimeur.android.gpsfiction.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionThing;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneChangeListener;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.views.MiniCompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4ListView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Adapter4TabZones extends BaseAdapter implements PlayerLocationListener, ZoneChangeListener {
    private HashMap<Zone, View> zone2View = null;
    private LinkedList<Zone> zonesToOrder = null;
    private MyTabFragmentImpl mMyTabFragmentImpl = null;

    public Adapter4TabZones() {
        super();
        if (zonesToOrder == null) zonesToOrder = new LinkedList<>();
    }

    public void register(MyTabFragmentImpl mtfi) {
        mMyTabFragmentImpl = mtfi;
        Iterator<GpsFictionThing> it = mMyTabFragmentImpl.getmGpsFictionData().getGpsFictionThing(Zone.class).iterator();
        while (it.hasNext()) {
            Zone zn = (Zone) it.next();
            if (zn.isVisible()) zonesToOrder.add(zn);
        }
        mMyTabFragmentImpl.getmMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.ADAPTERVIEW, this);
        mMyTabFragmentImpl.getmGpsFictionData().addZoneChangeListener(this);
    }

    @Override
    public int getCount() {
        return zonesToOrder.size();
    }

    private void reOrderZones() {
        if (zonesToOrder != null) {
            Collections.sort(zonesToOrder, Zone.DISTANCE2PLAYERINCREASING);
            notifyDataSetChanged();
        }
    }

    @Override
    public Object getItem(int position) {
        return zonesToOrder.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Zone)getItem(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (zone2View == null ) zone2View = new HashMap<>();
        LinearLayout layoutItem;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mMyTabFragmentImpl.getmGpsFictionActivity());
        ViewHolder4Zones holder;
        final Zone attachedZone = this.zonesToOrder.get(position);
        if (zone2View.get(attachedZone) != null) {
            layoutItem = (LinearLayout) zone2View.get(attachedZone);
        } else {
            layoutItem = (LinearLayout) mLayoutInflater.inflate(R.layout.zones_one_item_view, parent, false);
            holder = new ViewHolder4Zones();
            holder.setZoneTitleView((TextView) layoutItem.findViewById(R.id.textNameOfZone));
            holder.setDistanceToZoneView((ZoneDistance4ListView) layoutItem.findViewById(R.id.textDistance));
            holder.setMiniCompassView((MiniCompassView) layoutItem.findViewById(R.id.miniCompassDirection));
            holder.init(mMyTabFragmentImpl.getmGpsFictionActivity(), attachedZone);
            layoutItem.setTag(holder);
            layoutItem.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Zone selectedZone = ((ViewHolder4Zones) (view.getTag())).getAttachedZone();
                    mMyTabFragmentImpl.getmGpsFictionData().setSelectedZone(selectedZone);
                    //zonesFragment.getListZones().invalidateViews();
                    return true;
                }
            });
            zone2View.put(attachedZone, layoutItem);
        }
        return layoutItem;
    }

    @Override
    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {
        this.reOrderZones();
    }

    @Override
    public void onZoneChanged(Zone zone) {
        if ((!zonesToOrder.contains(zone)) && zone.isVisible()) {
            zonesToOrder.add(zone);
        } else if (zonesToOrder.contains(zone) && (!zone.isVisible())) {
            zonesToOrder.remove(zone);
        }
        reOrderZones();
    }
    public void destroy () {
        mMyTabFragmentImpl.getmMyLocationListener().removePlayerLocationListener(MyLocationListener.REGISTER.ADAPTERVIEW, this);
        mMyTabFragmentImpl.getmGpsFictionData().removeZoneChangeListener(this);
    }
}
