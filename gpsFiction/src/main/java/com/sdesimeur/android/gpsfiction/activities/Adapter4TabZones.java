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
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneChangeListener;
import com.sdesimeur.android.gpsfiction.views.MiniCompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4ListView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Adapter4TabZones extends BaseAdapter implements PlayerLocationListener, ZoneChangeListener {
    private HashMap<Zone, View> zone2View = null;
    private LinkedList<Zone> zonesToOrder = null;
    private GpsFictionActivity gpsFictionActivity = null;

    public Adapter4TabZones() {
        super();
        if (zonesToOrder == null) zonesToOrder = new LinkedList<>();
    }

    public void register(GpsFictionActivity gfa) {
        this.gpsFictionActivity = gfa;
        Iterator<GpsFictionThing> it = this.getGpsFictionActivity().getGpsFictionData().getGpsFictionThing(Zone.class).iterator();
        while (it.hasNext()) {
            Zone zn = (Zone) it.next();
            if (zn.isVisible()) zonesToOrder.add(zn);
        }
        getGpsFictionActivity().getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.ADAPTERVIEW, this);
        getGpsFictionActivity().getGpsFictionData().addZoneChangeListener(this);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return zonesToOrder.size();
    }

    public GpsFictionActivity getGpsFictionActivity() {
        return this.gpsFictionActivity;
    }

    private void reOrderZones() {
        //TODO add zonesToOrder by tmpZonesToOrder
        if (zonesToOrder != null) {
            Collections.sort(zonesToOrder, Zone.DISTANCE2PLAYERINCREASING);
            notifyDataSetChanged();
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return zonesToOrder.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return ((Zone)getItem(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (zone2View == null ) zone2View = new HashMap<>();
        LinearLayout layoutItem;
        LayoutInflater mLayoutInflater = LayoutInflater.from(this.getGpsFictionActivity());
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
//			holder.setDirectionOfZone((TextView) layoutItem.findViewById(R.id.textDirection));
            holder.init(this.getGpsFictionActivity(), attachedZone);
            layoutItem.setTag(holder);
            layoutItem.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Zone selectedZone = ((ViewHolder4Zones) (view.getTag())).getAttachedZone();
                    getGpsFictionActivity().getGpsFictionData().setSelectedZone(selectedZone);
                    //zonesFragment.getListZones().invalidateViews();
                    return true;
                }
            });
            zone2View.put(attachedZone, layoutItem);
        }
//		this.zonesFragment.getGpsFictionActivity().getMyLocationListener().firePlayerBearingListener();
//		this.zonesFragment.getGpsFictionActivity().getMyLocationListener().firePlayerLocationListener();
        // TODO calculer la direction
//		holder.getDirectionOfZone().setText(this.zoneActivity.getZonesOrdered().get(position).getStringBearingFromPlayer());
        return layoutItem;
    }

    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
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
        getGpsFictionActivity().getMyLocationListener().removePlayerLocationListener(MyLocationListener.REGISTER.ADAPTERVIEW, this);
        getGpsFictionActivity().getGpsFictionData().removeZoneChangeListener(this);
    }
}
