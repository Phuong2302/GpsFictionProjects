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
import com.sdesimeur.android.gpsfiction.views.MiniCompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4ListView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

@SuppressWarnings({"unused"})
public class Adapter4TabZones extends BaseAdapter implements PlayerLocationListener {
    //	private LayoutInflater mLayoutInflater = null;
    private HashMap<Zone, View> zone2View = new HashMap<Zone, View>();
    private LinkedList<Zone> zonesToOrder = new LinkedList<Zone>();
    private GpsFictionActivity gpsFictionActivity = null;

    public Adapter4TabZones() {
        super();
    }

    public void init(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
        this.getGpsFictionActivity().getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.ADAPTERVIEW, this);
        this.reOrderZones();
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
        this.zonesToOrder.clear();

        Zone zone;
        Float newDistance;
        //String[] tabOfTitles=getResources().getStringArray(R.array.names_classes);
        Iterator<GpsFictionThing> itZone = this.getGpsFictionActivity().getGpsFictionData().getGpsFictionThing(Zone.class).iterator();
        int i;
        while (itZone.hasNext()) {
            zone = (Zone) itZone.next();
            if (zone.isVisible()) {
                if (zone.isPlayerInThisZone()) {
                    this.zonesToOrder.addFirst(zone);
                } else {
                    newDistance = zone.getDistance2Player();
                    i = this.zonesToOrder.size();
                    do {
                        i--;
                    }
                    while ((i > -1) && (newDistance <= (this.zonesToOrder.get(i).getDistance2Player())));
                    if (i == -1) {
                        this.zonesToOrder.addFirst(zone);
                    } else {
                        this.zonesToOrder.add(i + 1, zone);
                    }
                }
            }
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
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
}
