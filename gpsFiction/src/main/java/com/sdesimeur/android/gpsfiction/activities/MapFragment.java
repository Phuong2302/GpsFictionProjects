package com.sdesimeur.android.gpsfiction.activities;


import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.util.BikeFlagEncoder;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.FootFlagEncoder;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;
import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionThing;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerRotatingMarker;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.geopoint.GeoPoint;
import com.sdesimeur.android.gpsfiction.views.ImageViewWithId;
import com.sdesimeur.android.gpsfiction.views.MyMapScaleBarView;
import com.sdesimeur.android.gpsfiction.views.MyMapView;
import com.sdesimeur.android.gpsfiction.views.RotateView;

import org.oscim.android.cache.TileCache;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static android.view.ViewGroup.LayoutParams;
import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;


public class MapFragment extends MyTabFragment implements PlayerLocationListener, ZoneSelectListener {
    //private MapPosition mapPosition = null;
    private static final int SELECTEDBUTTON = 255;
    private static final int UNSELECTEDBUTTON = 100;
    //private Drawable vehiculeSelectedDrawable = null;
    private int vehiculeSelectedId = R.drawable.pieton;
//    private TileRendererLayer tileRendererLayer = null;
//    private MyMapView mapView = null;
//    private HashSet<Layer> saveLayers = new HashSet<Layer>();
//    private Polyline route = null;
//    private MyMapScaleBarView mapScaleBarView;
    private File mapsFolder;
    private File ghFolder;
//    private TileCache tileCache;
    private GraphHopper hopper;
    //private GraphHopperAPI hopper;
    private String currentArea = "jeu";
    private volatile boolean shortestPathRunning = false;
    private volatile boolean shortestPathRunningFirst = false;
    private volatile boolean prepareInProgress = false;
    //private RotatingMarker playerMarker;
    private MarkerItem playerMarkerItem;
    //private boolean layoutMapViewRotateInitialized = false;
    private RotateView rotateView = null;
    private ViewGroup viewGroupForVehiculesButtons = null;
    private Zone selectedZone = null;
    private GeoPoint playerLocation = null;
    private HashMap<Integer,FlagEncoder> vehiculeGHEncoding = new HashMap <Integer, FlagEncoder> () {{
        put(R.drawable.compass, null);
        put(R.drawable.pieton, new FootFlagEncoder());
        put(R.drawable.cycle, new BikeFlagEncoder());
        put(R.drawable.auto, new CarFlagEncoder());
    }};
    private MarkerSymbol playerMarkerSymbol;

    public MapFragment() {
        super();
        //	this.setNameId(R.string.tabMapTitle);
    }

    public MyMapView getMapView() {
        return this.mapView;
    }

    private void finishPrepare() {
        prepareInProgress = false;
    }
    boolean isReady()
    {
        // only return true if already loaded
        if (hopper != null)
            return true;

        if (prepareInProgress)
        {
            return false;
        }
        return false;
    }
    public void register(GpsFictionActivity gpsFictionActivity) {
        super.register(gpsFictionActivity);
        this.getGpsFictionActivity().getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.FRAGMENT, this);
        this.getGpsFictionActivity().getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.FRAGMENT, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //this.vehiculeSelectedDrawable = this.getResources().getDrawable(R.drawable.compass);
        prepareInProgress = true;
        if (this.mapView == null) {
            this.mapView = new MyMapView(activity);
        }
        this.mapView.init(this.getGpsFictionActivity());

/*		if ( this.rotateView == null ) {
            this.rotateView = new RotateView(activity);
		}
		this.rotateView.init(this.getGpsFictionActivity());
		this.rotateView.addView(this.mapView);
*/
        this.playerLocation = this.getGpsFictionActivity().getMyLocationListener().getPlayerGeoPoint();
        if (this.playerMarkerItem == null) {
            this.playerMarkerItem = new MarkerItem("Player","",playerLocation);
            playerMarkerSymbol = new MarkerSymbol(drawableToBitmap(getResources(), R.drawable.player_marker), MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
            playerMarkerItem.setMarker(playerMarkerSymbol);
//            this.playerMarkerItem = new PlayerRotatingMarker(this.playerLocation, getResources(), R.drawable.player_marker);
            //this.playerMarker = new PlayerRotatingMarker  (playerPosition);
            //this.playerMarker.setResource(getResources(), R.drawable.player_marker);
            //this.playerMarker.register(this.getGpsFictionActivity());
            this.mapView.getLayers().add(this.playerMarker);
        } else {
            this.playerMarkerItem.setLatLong(this.playerLocation);
        }
            boolean greaterOrEqKitkat = Build.VERSION.SDK_INT >= 19;
            File dir = null;
            if (greaterOrEqKitkat) {
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            } else {
                dir = Environment.getExternalStorageDirectory();
            }
            dir = new File (dir, "/sdesimeur/");
            this.mapsFolder = new File (dir , "/mapsforge/");
            this.ghFolder = new File (dir , "/graphhopper/");
            MapDataStore mapDataStore = new MapFile(new File(mapsFolder, currentArea + ".map"));
        if (this.tileCache == null)
            this.tileCache = AndroidUtil.createTileCache(this.getActivity(), getClass().getSimpleName(),
                    this.mapView.getModel().displayModel.getTileSize(), 1f,
                    this.mapView.getModel().frameBufferModel.getOverdrawFactor());
        if (this.tileRendererLayer == null) {
            this.tileRendererLayer = new TileRendererLayer(this.tileCache, mapDataStore,
                    this.mapView.getModel().mapViewPosition, true, true, AndroidGraphicFactory.INSTANCE);
            this.tileRendererLayer.setTextScale(1f);
            this.tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
            this.mapView.getLayers().add(0, this.tileRendererLayer);
        }
        this.mapView.getLayers().addAll(this.saveLayers);
        this.saveLayers.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Zone zone;
        Iterator<GpsFictionThing> itZone = this.getGpsFictionActivity().getGpsFictionData().getGpsFictionThing(Zone.class).iterator();
        while (itZone.hasNext()) {
            zone = (Zone) itZone.next();
            this.registerZone(zone);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setRootView(inflater.inflate(R.layout.map_view, container, false));
        ViewGroup vg = (ViewGroup) this.getRootView();
        this.addRotateView(vg);
        this.addViewGroupForVehiculesButtons(vg);
        this.addViewGroupMapSCaleBar(vg);
        this.addViewGroupForZoomButtons(vg);
        this.loadGraphStorage();
        return this.getRootView();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.rotateView.removeAllViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //((ViewGroup) this.getRootView()).removeView(this.mapView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        this.tileCache.destroy();
        //if (this.hopper != null) this.hopper.close();
        this.hopper = null;
        this.mapView.getLayers().remove(this.tileRendererLayer);
        this.tileCache.destroy();
        this.tileRendererLayer = null;
        // necessary?
        System.gc();
        super.onDetach();
    }

    void loadGraphStorage() {
        new AsyncTask<Void, Void, Path>() {
            String error = "Pas d'erreur";
            protected Path saveDoInBackground(Void... v) {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                FlagEncoder encoder = null;
                hopper = tmpHopp;
                //if (vehiculeSelectedId == R.drawable.pieton) encoder = new FootFlagEncoder();
                //if (vehiculeSelectedId == R.drawable.cycle) encoder = new BikeFlagEncoder();
                //if (vehiculeSelectedId == R.drawable.auto) encoder = new CarFlagEncoder();
                //hopper.setEncodingManager(new EncodingManager(encoder));
                //hopper.setCHEnable(false);
                //hopper.setOSMFile(ghFolder + "/" + currentArea + "-gh/" + currentArea + ".pbf");
                hopper.setOSMFile(ghFolder + "/" + currentArea + ".pbf");
                hopper.setEncodingManager(new EncodingManager("FOOT,BIKE,CAR"));
                hopper.setCHWeighting("fastest");
                //hopper.load(new File(mapsFolder, currentArea).getAbsolutePath());
                hopper.importOrLoad();
                return null;
            }
            @Override
            protected Path doInBackground(Void... params) {
                try {
                    return saveDoInBackground(params);
                } catch (Throwable t) {
                    error = t.getMessage();
                    Log.e("AsynTask GraphHopper", error);
                    return null;
                }
            }
            protected void onPostExecute(Path o) {
                finishPrepare();
            }
        }.execute();
    }

    public void registerZone(Zone zn) {
        if (this.mapView == null) {
            this.saveLayers.add(zn.getZoneMarker());
            this.saveLayers.add(zn.getZonePolyline());
        } else {
            if (!this.mapView.getLayers().contains(zn.getZoneMarker()))
                this.mapView.getLayers().add(zn.getZoneMarker());
            if (!this.mapView.getLayers().contains(zn.getZonePolyline()))
                this.mapView.getLayers().add(zn.getZonePolyline());
        }
    }

    private void addViewGroupForZoomButtons(ViewGroup vg) {
        //	inflater.inflate(R.layout.tab_map_zoom_buttons, vg, true);
        //View v = inflater.inflate(R.layout.tab_map_vehicules_buttons, vg, true);
        //vg.addView(v);
        ZoomControls zoom = (ZoomControls) vg.findViewById(R.id.zoomControls);
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getModel().mapViewPosition.zoomIn();
            }
        });
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getModel().mapViewPosition.zoomOut();
            }
        });
    }

    private void onVehiculeChange(View v) {
        if ((this.playerLocation != null) && (this.selectedZone != null)) {
            for (int position = 0; position < this.viewGroupForVehiculesButtons.getChildCount(); position++) {
                View vi = this.viewGroupForVehiculesButtons.getChildAt(position);
                int alpha = ((vi == v) ? MapFragment.SELECTEDBUTTON : MapFragment.UNSELECTEDBUTTON);
                ((ImageView) vi).getDrawable().setAlpha(alpha);
                vi.invalidate();
            }
            int res = ((ImageViewWithId) v).getDrawableId();
            vehiculeSelectedId = res;
            //loadGraphStorage();
            this.calcPath();
        }
    }

    private void addViewGroupForVehiculesButtons(ViewGroup vg) {
        //	inflater.inflate(R.layout.tab_map_vehicules_buttons, vg, true);
        //View v = inflater.inflate(R.layout.tab_map_vehicules_buttons, vg, true);
        //vg.addView(v);
        viewGroupForVehiculesButtons = (ViewGroup) vg.findViewById(R.id.forVehiculesButtons);
        TypedArray vehicules = getResources().obtainTypedArray(R.array.vehicules_array);
        for (int index = 0; index < vehicules.length(); index++) {
            //int res = vehicules.getResourceId(index, -1);
            //Drawable d = this.getResources().getDrawable(res);
            int res = vehicules.getResourceId(index, -1);
            //int alpha = ((d == this.vehiculeSelectedDrawable)?MapFragment.SELECTEDBUTTON:MapFragment.UNSELECTEDBUTTON);
            ImageViewWithId img = new ImageViewWithId(getActivity());
            int pad = getResources().getDimensionPixelSize(R.dimen.buttonsVehiculesPadding);
            img.setPadding(pad, pad, pad, pad);
            img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVehiculeChange(v);
                }
            });
            img.setDrawableId(res);
            int alpha;
            if ((playerLocation == null) || (selectedZone == null)) {
                alpha = MapFragment.UNSELECTEDBUTTON;
            } else {
                alpha = ((res == vehiculeSelectedId) ? MapFragment.SELECTEDBUTTON : MapFragment.UNSELECTEDBUTTON);
                calcPath();
            }
            img.getDrawable().setAlpha(alpha);
            viewGroupForVehiculesButtons.addView(img);
        }
        vehicules.recycle();
    }

    private void addViewGroupMapSCaleBar(ViewGroup vg) {
        //	inflater.inflate(R.layout.tab_map_scalebar, vg, true);
        LinearLayout l = (LinearLayout) vg.findViewById(R.id.forScaleBar);
        mapScaleBarView = new MyMapScaleBarView(getActivity());
        mapScaleBarView.init(mapView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_LEFT | RelativeLayout.ALIGN_BOTTOM);
        mapScaleBarView.setLayoutParams(lp);
        l.addView(mapScaleBarView);
        //vg.addView(this.mapScaleBarView);
    }

    private void addRotateView(ViewGroup vg) {
        rotateView = (RotateView) vg.findViewById(R.id.mapRotateView);
        rotateView.init(this.getGpsFictionActivity());
        rotateView.addView(mapView);
    }

    private Marker createMarker(LatLong p, int resource) {
        Drawable drawable = getResources().getDrawable(resource);
        Bitmap bitmapRotated = AndroidGraphicFactory.convertToBitmap(drawable);
        Marker marker = new Marker(p, bitmapRotated, -bitmapRotated.getHeight() / 2 + 1, bitmapRotated.getWidth() / 2 + 1);
        return marker;
    }

    private Polyline createPolyline(List<LatLong> listOfPoints) {
        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(Color.BLUE);
        paintStroke.setDashPathEffect(new float[]{25, 15});
        paintStroke.setStrokeWidth(4);
        // TODO: new mapsforge version wants an mapsforge-paint, not an android paint.
        // This doesn't seem to support transparceny
        //paintStroke.setAlpha(128);
        Polyline line = new Polyline( paintStroke, AndroidGraphicFactory.INSTANCE);
        line.getLatLongs().addAll(listOfPoints);
        return line;
    }

    private Polyline createPolyline(GHResponse resp) {
        ArrayList<LatLong> listOfPoints = new ArrayList<LatLong>();
        PointList tmp = resp.getPoints();
        for (int i = 0; i < tmp.getSize(); i++) {
            listOfPoints.add(new LatLong(tmp.toGHPoint(i).getLat(), tmp.toGHPoint(i).getLon()));
        }
        // TODO Auto-generated method stub
        return this.createPolyline(listOfPoints);
    }

    public void calcRoutePath(final double fromLat, final double fromLon, final double toLat, final double toLon ) {
        shortestPathRunning = true;
        new AsyncTask<Void, Void, GHResponse>() {
            float time;

            protected GHResponse doInBackground( Void... v ) {
                while ( ! (isReady()))  {}
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon)
                        .setAlgorithm(AlgorithmOptions.DIJKSTRA_BI);
                req.setVehicle(vehiculeGHEncoding.get(vehiculeSelectedId));
                req.getHints().put("instructions", "false");
                hopper.getGraphHopperStorage();
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp;
            }

            protected void onPostExecute( GHResponse resp ) {
                if (!resp.hasErrors()) {
                    addRoute(createPolyline(resp));
                    shortestPathRunningFirst = false;
                    //mapView.redraw();
                } else { }
                shortestPathRunning = false;
            }
        }.execute();
    }
    private void calcLinePath (){
        ArrayList<LatLong> listOfPoints = new ArrayList<LatLong>();
        listOfPoints.add((LatLong) this.playerLocation);
        listOfPoints.add((LatLong) this.selectedZone.getCenterPoint());
        addRoute(createPolyline(listOfPoints));
    }
    private void calcPath() {
        if (vehiculeSelectedId == R.drawable.compass) {
            shortestPathRunningFirst = true;
            calcLinePath();
        } else {
            if (shortestPathRunningFirst) calcLinePath();
            final double fromLat = playerLocation.getLatitude();
            final double fromLon = playerLocation.getLongitude();
            final double toLat = selectedZone.getCenterPoint().getLatitude();
            final double toLon = selectedZone.getCenterPoint().getLongitude();
            if (! (shortestPathRunning)) calcRoutePath(fromLat, fromLon, toLat, toLon);
        }
    }

    private void addRoute(Polyline newroute) {
        if (mapView != null) {
            if (route != null) mapView.getLayers().remove(route);
            route=newroute;
            mapView.getLayers().add(route);
            //mapView.invalidate();
        }
    }

    @Override
    public void onZoneSelectChanged(Zone sZn) {
        // TODO Auto-generated method stub
        selectedZone = sZn;
        if ((playerLocation != null) && (selectedZone != null)) calcPath();
    }

    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        // TODO Auto-generated method stub
        playerLocation = playerLocationEvent.getLocationOfPlayer();
        if ((playerLocation != null) && (selectedZone != null)) calcPath();
    }
}
