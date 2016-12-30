package com.sdesimeur.android.gpsfiction.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.sdesimeur.android.gpsfiction.activities.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionThing;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneChangeListener;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.views.MiniCompass4ListView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4ListView;
import com.sdesimeur.android.gpsfiction.views.ZoneName4ListView;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Adapter4TabZones extends RecyclerView.Adapter<Adapter4TabZones.ViewHolder> implements PlayerLocationListener, ZoneChangeListener {

    private LinkedList<Zone> zonesToOrder = null;

    public Adapter4TabZones() {
        super();
        zonesToOrder = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zones_one_item_view, parent, false);
        int id = R.id.gpsFictionControlerId;
        GpsFictionControler gfc = (GpsFictionControler) parent.getTag(id);
        v.setTag(id,gfc);
        v.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View vn = view.findViewById(R.id.textNameOfZone);
                Zone selectedZone =  (Zone) vn.getTag(R.id.attachedZoneId);
                GpsFictionControler gfc = (GpsFictionControler) vn.getTag(R.id.gpsFictionControlerId);
                gfc.setSelectedZone(selectedZone);
                //v.invalidate();
                return true;
            }
        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Zone zn = zonesToOrder.get(position);
        int id = R.id.attachedZoneId;
        holder.miniCompassView.setTag(id,zn);
        holder.distanceToZoneView.setTag(id,zn);
        holder.zoneTitleView.setTag(id,zn);
        holder.zoneTitleView.setText(zn.getName());
    }

    @Override
    public void onAttachedToRecyclerView (RecyclerView view) {
        int id = R.id.gpsFictionControlerId;
        GpsFictionControler gfc = (GpsFictionControler) view.getTag(id);
        Iterator<GpsFictionThing> it = gfc.getmGpsFictionData().getGpsFictionThing(Zone.class).iterator();
        while (it.hasNext()) {
            Zone zn = (Zone) it.next();
            if (zn.isVisible()) zonesToOrder.add(zn);
        }
        reOrderZones();
        gfc.addPlayerLocationListener(GpsFictionControler.REGISTER.ADAPTERVIEW, this);
        gfc.addZoneChangeListener(this);
        super.onAttachedToRecyclerView(view);
    }
    @Override
    public void onDetachedFromRecyclerView (RecyclerView view) {
        GpsFictionControler gfc = (GpsFictionControler) view.getTag();
        gfc.removePlayerLocationListener(GpsFictionControler.REGISTER.ADAPTERVIEW, this);
        gfc.removeZoneChangeListener(this);
        zonesToOrder.clear();
        super.onDetachedFromRecyclerView(view);
    }
    @Override
    public int getItemCount() {
        return zonesToOrder.size();
    }


    private void reOrderZones() {
        if (zonesToOrder != null) {
            Collections.sort(zonesToOrder, Zone.DISTANCE2PLAYERINCREASING);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {
        reOrderZones();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ZoneName4ListView zoneTitleView = null;
        public ZoneDistance4ListView distanceToZoneView = null;
        public MiniCompass4ListView miniCompassView = null;

        public ViewHolder(View itemView) {
            super(itemView);
            int id = R.id.gpsFictionControlerId;
            GpsFictionControler gfc = (GpsFictionControler) itemView.getTag(id);
            zoneTitleView = (ZoneName4ListView) itemView.findViewById(R.id.textNameOfZone);
            zoneTitleView.setTag(id,gfc);
            distanceToZoneView = (ZoneDistance4ListView) itemView.findViewById(R.id.textDistance);
            distanceToZoneView.setTag(id,gfc);
            miniCompassView = (MiniCompass4ListView) itemView.findViewById(R.id.miniCompassDirection);
            miniCompassView.setTag(id,gfc);
        }
    }
}
