package com.sdesimeur.android.gpsfiction.activities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
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
import java.util.Iterator;
import java.util.LinkedList;

public class Adapter4TabZones extends RecyclerView.Adapter<Adapter4TabZones.ViewHolder> implements PlayerLocationListener, ZoneChangeListener {

    private LinkedList<Zone> zonesToOrder = null;
    private GpsFictionControler gpsFictionControler;

    public Adapter4TabZones() {
        super();
        //if (zonesToOrder == null)
            zonesToOrder = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zones_one_item_view, parent, false);
        v.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Zone selectedZone =  (Zone) view.findViewById(R.id.textNameOfZone).getTag();
                gpsFictionControler.setSelectedZone(selectedZone);
                //v.invalidate();
                return true;
            }
        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Zone zn = zonesToOrder.get(position);
        holder.miniCompassView.setTag(zn);
        holder.distanceToZoneView.setTag(zn);
        holder.zoneTitleView.setTag(zn);
        //holder.setAttachedZone(zn);
        holder.zoneTitleView.setText(zn.getName());
        holder.zoneTitleView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                gpsFictionControler.addZoneSelectListener(GpsFictionControler.REGISTER.VIEW, holder);
            }
            @Override
            public void onViewDetachedFromWindow(View view) {
                gpsFictionControler.removeZoneSelectListener(GpsFictionControler.REGISTER.VIEW, holder);
            }
        });
        holder.distanceToZoneView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                gpsFictionControler.addPlayerLocationListener(GpsFictionControler.REGISTER.VIEW, holder.distanceToZoneView);
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                gpsFictionControler.removePlayerLocationListener(GpsFictionControler.REGISTER.VIEW, holder.distanceToZoneView);
            }
        });
        holder.miniCompassView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                gpsFictionControler.addPlayerBearingListener(GpsFictionControler.REGISTER.VIEW, holder.miniCompassView);
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                gpsFictionControler.removePlayerBearingListener(GpsFictionControler.REGISTER.VIEW, holder.miniCompassView);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView (RecyclerView view) {
        GpsFictionControler gfc = (GpsFictionControler) view.getTag();
        gpsFictionControler = gfc;
        gfc.addPlayerLocationListener(GpsFictionControler.REGISTER.ADAPTERVIEW, this);
        gfc.addZoneChangeListener(this);
        Iterator<GpsFictionThing> it = gfc.getmGpsFictionData().getGpsFictionThing(Zone.class).iterator();
        while (it.hasNext()) {
            Zone zn = (Zone) it.next();
            if (zn.isVisible()) zonesToOrder.add(zn);
        }
        super.onAttachedToRecyclerView(view);
    }
    @Override
    public void onDetachedFromRecyclerView (RecyclerView view) {
        GpsFictionControler gfc = (GpsFictionControler) view.getTag();
        gfc.removePlayerLocationListener(GpsFictionControler.REGISTER.ADAPTERVIEW, this);
        gfc.removeZoneChangeListener(this);
        zonesToOrder.clear();
        gpsFictionControler = null;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements ZoneSelectListener {
        public TextView zoneTitleView = null;

        public ZoneDistance4ListView distanceToZoneView = null;
        //	private TextView directionOfZone;
        public MiniCompassView miniCompassView = null;
        //private Zone attachedZone = null;

        public ViewHolder(View itemView) {
            super(itemView);
            zoneTitleView = (TextView) itemView.findViewById(R.id.textNameOfZone);
            distanceToZoneView = (ZoneDistance4ListView) itemView.findViewById(R.id.textDistance);
            miniCompassView = (MiniCompassView) itemView.findViewById(R.id.miniCompassDirection);
        }

        //private void updateZoneTitleView() {
            //int titlebackgroundcolor = 0;
            //if (gpsFictionControler.getSelectedZone() == attachedZone) {
            //    zoneTitleView.setSelected(true);
                //titlebackgroundcolor = res.getColor(R.color.tabnameofzoneselected);
            //} else {
            //    zoneTitleView.setSelected(false);
                //titlebackgroundcolor = res.getColor(R.color.tabnameofzone);
            //}
            //zoneTitleView.setBackgroundColor(titlebackgroundcolor);
        //}
/*
        public void setAttachedZone (Zone aZone) {
            attachedZone = aZone;
            updateZoneTitleView();
        }
*/
        @Override
        public void onZoneSelectChanged(Zone selectedZone, Zone uSZ) {
            zoneTitleView.setSelected(selectedZone == ((Zone)zoneTitleView.getTag()));
            if (zoneTitleView.isShown())
                zoneTitleView.invalidate();
            //updateZoneTitleView();
        }
/*
        public Zone getAttachedZone() {
            return attachedZone;
        }
*/
    }
}
