package com.sdesimeur.android.gpsfiction.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.views.CompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4CompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneNameView;

@SuppressWarnings("unused")
public class CompassFragment extends MyTabFragment {
    private ZoneDistance4CompassView textviewDistance = null;
    private ZoneNameView textviewName = null;
    private CompassView compassView = null;

    public CompassFragment() {
        super();
        //	this.setNameId(R.string.tabCompassTitle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setRootView(inflater.inflate(R.layout.compass_view, container, false));
        this.textviewName = (ZoneNameView) (this.getRootView().findViewById(R.id.textNameOfZone));
        this.textviewName.init(this.getGpsFictionActivity());
        this.textviewDistance = (ZoneDistance4CompassView) (this.getRootView().findViewById(R.id.textDistance));
        this.textviewDistance.init(this.getGpsFictionActivity());
        this.compassView = (CompassView) (this.getRootView().findViewById(R.id.compassDirection));
        this.compassView.init(this.getGpsFictionActivity());
        //this.compassView.setTypeface(this.getGpsFictionActivity().getFontFromRes(R.integer.compassFont));
        this.compassView.setTypeface(this.getGpsFictionActivity().getFontFromRes(R.raw.font_dancing));
        //this.updateTextviewName();
        //this.updateTextviewDistance();
        //this.updateCompassView();
        return this.getRootView();
    }

/*
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.tab_compass);
        this.textviewName = ((TextView) this.findViewById(R.id.textNameOfZone));
		this.textviewDistance = ((TextView) this.findViewById(R.id.textDistance));
		this.setGpsFiction(((GpsFictionActivity)this.getParent()).getGpsFiction());
		this.onZoneSelectChanged();
		this.register();
		((CompassView)this.findViewById(R.id.compassDirection)).register(this.getGpsFiction());
	}
*/
/*	
	private void updateTextviewDistance () {
		if (this.textviewDistance != null) {
			String distanceText = ( this.selectedZone == null ) ?
					getResources().getString(R.string.noZoneDistance) :
					this.selectedZone.getStringDistance2Player();
			this.textviewDistance.setText(distanceText);
			this.textviewDistance.invalidate();
		}
	}
	private void updateTextviewName () {
		if (this.textviewName != null) {
			String nameText = ( this.selectedZone == null ) ?
				getResources().getString(R.string.noZoneTitle) :
				this.selectedZone.getName();
			this.textviewName.setText(nameText);
			this.textviewName.invalidate();
		}
	}
	private void updateCompassView () {
		if (this.compassView != null) {
			this.compassView.setSelectedZone(this.selectedZone);
			this.compassView.setBearingOfPlayer(this.bearingOfPlayer);
			this.compassView.invalidate();
		}
	}
	@Override
	public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
		// TODO Auto-generated method stub
		this.updateTextviewDistance();
		this.updateCompassView();
	}
	@Override
	public void onZoneSelectChanged(Zone selectedZone) {
		// TODO Auto-generated method stub
		this.selectedZone = selectedZone;
		this.updateTextviewName();
		this.updateTextviewDistance();
		this.updateCompassView();
	}
	@Override
	public void onBearingPlayerChanged(PlayerBearingEvent playerBearingEvent) {
		this.bearingOfPlayer = playerBearingEvent.getBearing();
		this.updateCompassView();
	}
*/
}
