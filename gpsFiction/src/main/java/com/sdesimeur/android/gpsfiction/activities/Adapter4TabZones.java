package com.sdesimeur.android.gpsfiction.activities;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionThing;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneChangeListener;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.views.MiniCompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4ListView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Adapter4TabZones extends RecyclerView.Adapter<Adapter4TabZones.ViewHolder> implements PlayerLocationListener, ZoneChangeListener {
    public HashMap<Zone, View> getZone2View() {
        return zone2View;
    }

    private HashMap<Zone, View> zone2View = new HashMap<>();
    private LinkedList<Zone> zonesToOrder = null;
    private MyTabFragment mMyTabFragment = null;

    public Adapter4TabZones() {
        super();
        if (zonesToOrder == null) zonesToOrder = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zones_one_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Zone zn = zonesToOrder.get(position);
        holder.miniCompassView.setTag(zn);
        holder.distanceToZoneView.setTag(zn);
        holder.setAttachedZone(zn);
    }
    @Override
    public int getItemCount() {
        return zonesToOrder.size();
    }

    public void register(MyTabFragment mtfi) {
        mMyTabFragment = mtfi;
        GpsFictionControler gfc = mMyTabFragment.getmGpsFictionControler();
        gfc.addPlayerLocationListener(GpsFictionControler.REGISTER.ADAPTERVIEW, this);
        /*
        for ( View v : getZone2View().values()) {
            ViewHolder4Zones v1 = (ViewHolder4Zones)v.getTag();
            gfc.addPlayerLocationListener(GpsFictionControler.REGISTER.VIEW,v1.getDistanceToZoneView());
            gfc.addPlayerBearingListener(GpsFictionControler.REGISTER.VIEW,v1.getMiniCompassView());
        }
        */
        gfc.addZoneChangeListener(this);
        Iterator<GpsFictionThing> it = mMyTabFragment.getmGpsFictionControler().getmGpsFictionData().getGpsFictionThing(Zone.class).iterator();
        while (it.hasNext()) {
            Zone zn = (Zone) it.next();
            if (zn.isVisible()) zonesToOrder.add(zn);
        }
    }

    private void reOrderZones() {
        if (zonesToOrder != null) {
            Collections.sort(zonesToOrder, Zone.DISTANCE2PLAYERINCREASING);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layoutItem;
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
            holder.init( mMyTabFragment, attachedZone);
            layoutItem.setTag(holder);
            layoutItem.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Zone selectedZone = ((ViewHolder4Zones) (view.getTag())).getAttachedZone();
                    mMyTabFragment.getmGpsFictionControler().setSelectedZone(selectedZone);
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

    public void unregister() {
        GpsFictionControler gfc = mMyTabFragment.getmGpsFictionControler();
        gfc.removePlayerLocationListener(GpsFictionControler.REGISTER.ADAPTERVIEW, this);
        /*
        for ( View v : getZone2View().values()) {
            ViewHolder4Zones v1 = (ViewHolder4Zones)v.getTag();
            gfc.removePlayerLocationListener(GpsFictionControler.REGISTER.VIEW,v1.getDistanceToZoneView());
            gfc.removePlayerBearingListener(GpsFictionControler.REGISTER.VIEW,v1.getMiniCompassView());
        }
        */
        gfc.removeZoneChangeListener(this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ZoneSelectListener {
        public TextView zoneTitleView = null;

        public ZoneDistance4ListView distanceToZoneView = null;
        //	private TextView directionOfZone;
        public MiniCompassView miniCompassView = null;
        private Zone attachedZone = null;

        public ViewHolder(View itemView) {
            super(itemView);
            zoneTitleView = (TextView) itemView.findViewById(R.id.textNameOfZone);
            distanceToZoneView = (ZoneDistance4ListView) itemView.findViewById(R.id.textDistance);
            miniCompassView = (MiniCompassView) itemView.findViewById(R.id.miniCompassDirection);
            zoneTitleView.setText(attachedZone.getName());
            zoneTitleView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                    mMyTabFragment.getmGpsFictionControler().addZoneSelectListener(GpsFictionControler.REGISTER.HOLDERVIEW, ViewHolder4Zones.this);
                }
                @Override
                public void onViewDetachedFromWindow(View view) {
                    mMyTabFragment.getmGpsFictionControler().removeZoneSelectListener(GpsFictionControler.REGISTER.HOLDERVIEW, ViewHolder4Zones.this);
                }
            });
            distanceToZoneView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                    mMyTabFragment.getmGpsFictionControler().addPlayerLocationListener(GpsFictionControler.REGISTER.VIEW, this);
                    super.onViewAttachedToWindow(view);
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    mMyTabFragment.getmGpsFictionControler().removePlayerLocationListener(GpsFictionControler.REGISTER.VIEW, this);
                    super.onViewDetachedFromWindow(view);
                }
            });
            miniCompassView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                    mMyTabFragment.getmGpsFictionControler().addPlayerBearingListener(GpsFictionControler.REGISTER.VIEW, this);
                    super.onViewAttachedToWindow(view);
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    mMyTabFragment.getmGpsFictionControler().removePlayerBearingListener(GpsFictionControler.REGISTER.VIEW, this);
                    super.onViewDetachedFromWindow(view);
                }
            });
        }

        public Zone getAttachedZone() {
            return attachedZone;
        }

        private void updateZoneTitleView() {
            Resources res = mMyTabFragment.getResources();
            int titlebackgroundcolor = 0;
            if (mMyTabFragment.getmGpsFictionControler().getSelectedZone() == attachedZone) {
                titlebackgroundcolor = res.getColor(R.color.tabnameofzoneselected);
            } else {
                titlebackgroundcolor = res.getColor(R.color.tabnameofzone);
            }
            zoneTitleView.setBackgroundColor(titlebackgroundcolor);
            if (zoneTitleView.isShown())
                zoneTitleView.invalidate();
        }

        public void setAttachedZone (Zone aZone) {
            attachedZone = aZone;
            updateZoneTitleView();
        }

        @Override
        public void onZoneSelectChanged(Zone selectedZone, Zone uSZ) {
            // TODO Auto-generated method stub
            updateZoneTitleView();
        }

    }
}
