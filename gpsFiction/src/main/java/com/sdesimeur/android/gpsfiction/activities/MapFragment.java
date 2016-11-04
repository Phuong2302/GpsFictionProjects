package com.sdesimeur.android.gpsfiction.activities;


import android.content.res.TypedArray;
import android.graphics.Path;
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
import android.widget.ZoomControls;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.util.BikeFlagEncoder;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.FootFlagEncoder;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.StopWatch;
import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionThing;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerBearingListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.RouteGeoPointListHelper;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneChangeListener;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.classes.ZoneViewHelper;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.utils.MyDrawable;
import com.sdesimeur.android.gpsfiction.utils.TextDrawable;

import org.oscim.android.MapView;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.event.MotionEvent;
import org.oscim.layers.MapEventLayer;
import org.oscim.layers.PathLayer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.PolygonDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;
import org.oscim.theme.VtmThemes;
import org.oscim.theme.styles.LineStyle;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.support.v4.content.ContextCompat.getDrawable;
import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;
import static org.oscim.layers.marker.MarkerSymbol.HotspotPlace;


public class MapFragment
        extends MyTabFragment
        implements PlayerBearingListener, ZoneChangeListener, PlayerLocationListener,
                    ZoneSelectListener, ItemizedLayer.OnItemGestureListener<MarkerItem> {
    MapView mapView;
    Map mMap;
    //MapPreferences mPrefs;
    //private MapPosition mapPosition = null;
    private static final int SELECTEDBUTTON = 255;
    private static final int UNSELECTEDBUTTON = 100;
    private ImageView viewForMapDirection;
    private ImageView viewForMapPosition;
    int clickCount;
    private int PositionTouchX;
    private int PositionTouchY;
    long startTouchTime = 0 ;

/*
    public void onMapEvent(Event e, MapPosition mapPosition) {
        if ((e == Map.MOVE_EVENT) || (e==Map.POSITION_EVENT)) {
            case MotionEvent.ACTION_MOVE:
                if ((pointerCount == 1)||(pointerCount == 2)) {
            viewForMapPosition.setTag(R.drawable.mapwithoutfollow);
            fixViewForMapPosition();
            onLocationPlayerChanged(getPlayerLocation());
            viewForMapDirection.setTag(MapDirection.FIX);
            fixViewForMapDirection();
            onBearingPlayerChanged(getmMyLocationListener().getBearingOfPlayer());
        }

    }
*/

    private static class MapDirection {
        public static final int PLAYER=0;
        public static final int FIX = 1;
        public static final int NORTH = 2;
    }
    //private Drawable vehiculeSelectedDrawable = null;
    private File mapsFolder;

    private File ghFolder;
    private GraphHopper hopper;
    //private GraphHopperAPI hopper;
    private String currentArea = "jeu";
    private volatile boolean shortestPathRunning = false;
    private volatile boolean prepareInProgress = false;
    private MarkerItem playerMarkerItem;
    private ViewGroup viewGroupForVehiculesButtons = null;
    private HashMap<Integer,FlagEncoder> vehiculeGHEncoding = new HashMap <Integer, FlagEncoder> () {{
        put(R.drawable.compass, null);
        put(R.drawable.pieton, new FootFlagEncoder());
        put(R.drawable.cycle, new BikeFlagEncoder());
        put(R.drawable.auto, new CarFlagEncoder());
    }};

    private MarkerSymbol playerMarkerSymbol = null;
    private HashMap<Zone,ZoneViewHelper> zoneViewHelperHashMap = null;
    private ItemizedLayer<MarkerItem> mMarkerLayer=null;
    private MyDrawable playerDrawable = null;
    private PathLayer routePathLayer = null;
//    private VectorLayer mVectorLayer = null;
    private PathWrapper routePath = null;
//    private HashMap<Integer, ImageView> hashMapVehiculesButtonsIdView = null;
    private RouteGeoPointListHelper mRouteGeoPointListHelper = null;
    private Style mStyle4SelectedZone;
    private Style mStyle4UnSelectedZone;
    private Style mStyle4InvisibleZone;
//    private RotateDrawable playerRotateDrawable = null;
//    private Bitmap playerBitmap = null;

    public int getZoomLevel() {
        return getmGpsFictionData().getZoomLevel();
    }
    public void setZoomLevel(int id) {
        getmGpsFictionData().setZoomLevel(id);
    }
    public int getZoomLevelIncr() {
        return getmGpsFictionData().getZoomLevelIncr();
    }
    public int getZoomLevelDecr() {
        return getmGpsFictionData().getZoomLevelDecr();
    }
    public int getVehiculeSelectedId() {
        return getmGpsFictionData().getVehiculeSelectedId();
    }
    public void setVehiculeSelectedId(int id) {
        getmGpsFictionData().setVehiculeSelectedId(id);
    }
    public Zone getSelectedZone() {
        return getmGpsFictionData().getSelectedZone();
    }
    public void setSelectedZone(Zone selectedZone) {
        getmGpsFictionData().setSelectedZone(selectedZone);
    }

    public MyGeoPoint getPlayerLocation() {
        return getmMyLocationListener().getPlayerGeoPoint();
    }

    public MapFragment() {
        super();
    }

    public MapView getMapView() {
        return this.mapView;
    }

    private void finishPrepare() {
        prepareInProgress = false;
    }
    boolean isReady() {
        if (hopper != null) return true;
        if (prepareInProgress) return false;
        return false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.vehiculeSelectedDrawable = this.getResources().getDrawable(R.drawable.compass);
        prepareInProgress = true;
            boolean greaterOrEqKitkat = Build.VERSION.SDK_INT >= 19;
            File dir = null;
            if (greaterOrEqKitkat) {
                //dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                dir = Environment.getExternalStorageDirectory();
            } else {
                dir = Environment.getExternalStorageDirectory();
            }
            dir = new File (dir, "/sdesimeur/");
            this.mapsFolder = new File (dir , "/mapsforge/");
            this.ghFolder = new File (dir , "/graphhopper/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRootView(inflater.inflate(R.layout.map_view, container, false));
        mStyle4SelectedZone = Style.builder()
                .strokeColor(getResources().getColor(R.color.colorOfZoneShapeSelected))
                .fillColor(getResources().getColor(R.color.colorOfZoneShapeSelected))
                .build();
        mStyle4UnSelectedZone = Style.builder()
                .strokeColor(getResources().getColor(R.color.colorOfZoneShapeNotSelected))
                .fillColor(getResources().getColor(R.color.colorOfZoneShapeNotSelected))
                .build();
        mStyle4InvisibleZone = Style.builder()
                .fillColor(getResources().getColor(R.color.colorOfZoneShapeInvisible))
                .fillAlpha(0f)
                .strokeWidth(0)
                .strokeColor(getResources().getColor(R.color.colorOfZoneShapeInvisible))
                .build();
        //setVehiculeSelectedId(R.drawable.compass);
        mapView=(MapView) this.getRootView().findViewById(R.id.mapView);
        mMap = mapView.map();
        mMap.layers().add(new MapEventLayer(mMap){
            public boolean onTouchEvent(MotionEvent e) {
                int pointerCount = e.getPointerCount();
                if ((e.getAction() & MotionEvent.ACTION_MASK)==MotionEvent.ACTION_MOVE ) {
                        if ((pointerCount == 1)||(pointerCount == 2)) {
                            viewForMapPosition.setTag(R.drawable.mapwithoutfollow);
                            fixViewForMapPosition();
                            onLocationPlayerChanged(getPlayerLocation());
                            viewForMapDirection.setTag(MapDirection.FIX);
                            fixViewForMapDirection();
                            onBearingPlayerChanged(getmMyLocationListener().getBearingOfPlayer());
                        }
                }
                return super.onTouchEvent(e);
            }
        });
        zoneViewHelperHashMap = new HashMap<>();
        playerDrawable = new MyDrawable(getDrawable(getActivity(),R.drawable.player_marker));
//        playerRotateDrawable = (RotateDrawable) getResources().getDrawable(R.drawable.playerrotatemarker);
//        playerBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.player_marker);
        MarkerSymbol ms = new MarkerSymbol(drawableToBitmap(getResources(),R.drawable.transparent), HotspotPlace.CENTER);
        mMarkerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), ms , this);
        mMap.layers().add(mMarkerLayer);
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setPreferredLanguage(Locale.getDefault().getLanguage());
        File file = new File(mapsFolder, currentArea + ".map");
        if (tileSource.setMapFile(file.toString())) {
            VectorTileLayer l = mMap.setBaseMap(tileSource);
            mMap.setTheme(VtmThemes.DEFAULT);
            mMap.layers().add(new BuildingLayer(mMap, l));
            mMap.layers().add(new LabelLayer(mMap, l));
            //   mPrefs.clear();
            MapPosition pos = mMap.getMapPosition();
            pos.setZoomLevel(getZoomLevel());
            mMap.setMapPosition(pos);
        }
        if (routePathLayer == null) {
            routePathLayer = new PathLayer(mMap, Color.TRANSPARENT);
            mMap.layers().add(routePathLayer);
        }
        int lineWidth=getResources().getDimensionPixelSize(R.dimen.widthOfRouteLine);
        int lineColor = getResources().getColor(R.color.colorOfRouteLine);
        LineStyle ls = new LineStyle(lineColor, lineWidth, Paint.Cap.ROUND);
        routePathLayer.setStyle(ls);
        ViewGroup vg = (ViewGroup) getRootView();
        addViewGroupForVehiculesButtons(vg);
        addViewForMapDirection(vg);
        addViewForMapPosition(vg);
        addViewGroupForZoomButtons(vg);
        loadGraphStorage();
        return getRootView();
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
//        int alpha = MapFragment.SELECTEDBUTTON;
//        hashMapVehiculesButtonsIdView.get(getVehiculeSelectedId()).getDrawable().setAlpha(alpha);
        shortestPathRunning = false;
        //mPrefs.load(mapView.map());
        mapView.onResume();
        registerAllZones();
        getmMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.FRAGMENT, this);
        getmMyLocationListener().addPlayerBearingListener(MyLocationListener.REGISTER.FRAGMENT, this);
        getmGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.FRAGMENT, this);
        getmGpsFictionData().addZoneChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRouteGeoPointListHelper != null) mRouteGeoPointListHelper.stopListenning();
        getmMyLocationListener().removePlayerLocationListener(MyLocationListener.REGISTER.FRAGMENT, this);
        getmMyLocationListener().removePlayerBearingListener(MyLocationListener.REGISTER.FRAGMENT, this);
        getmGpsFictionData().removeZoneSelectListener(GpsFictionData.REGISTER.FRAGMENT, this);
        getmGpsFictionData().removeZoneChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        hopper = null;
        mMarkerLayer = null;
        zoneViewHelperHashMap = null;
        playerMarkerItem = null;
        routePathLayer = null;
        super.onDetach();
    }

    private void registerAllZones() {
        Zone zone=null;
        Iterator<GpsFictionThing> itZone = getmGpsFictionData().getGpsFictionThing(Zone.class).iterator();
        while (itZone.hasNext()) {
            zone = (Zone) itZone.next();
            onZoneChanged(zone);
        }
    }

    void loadGraphStorage() {
        new AsyncTask<Void, Void, Path>() {
            String error = "Pas d'erreur";
            protected Path saveDoInBackground(Void... v) {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                hopper = tmpHopp;
//                hopper.getCHFactoryDecorator().addWeighting("fastest");
//                hopper.getCHFactoryDecorator().addWeighting("shortest");
                hopper.setCHEnabled(false);
                hopper.setAllowWrites(false);
                hopper.setEnableInstructions(true);
                hopper.setEnableCalcPoints(true);
                hopper.load(new File(ghFolder, currentArea).getAbsolutePath());
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

    private void addViewGroupForZoomButtons(ViewGroup vg) {
        ZoomControls zoom = (ZoomControls) vg.findViewById(R.id.zoomControls);
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPosition pos = mMap.getMapPosition();
                pos.setZoomLevel(getZoomLevelIncr());
                mMap.setMapPosition(pos);
            }
        });
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPosition pos = mMap.getMapPosition();
                pos.setZoomLevel(getZoomLevelDecr());
                mMap.setMapPosition(pos);
            }
        });
    }

    private void onVehiculeChange(View v) {
            ImageView v1 = (ImageView) v;
            int res = (int) v1.getTag();
//            if (hashMapVehiculesButtonsIdView.get(getVehiculeSelectedId()) != v1) {
            if (getVehiculeSelectedId() != res) {
                setVehiculeSelectedId(res);
                v1.getDrawable().setAlpha(MapFragment.SELECTEDBUTTON);
                v1.invalidate();
                for (int idx=0; idx < viewGroupForVehiculesButtons.getChildCount(); idx++) {
//                for (int idx : hashMapVehiculesButtonsIdView.keySet()){
                    ImageView v2 = (ImageView) viewGroupForVehiculesButtons.getChildAt(idx);
//                    if (((int)v2.getTag()) == ((int)v1.getTag())) {
//                        setVehiculeSelectedId(idx);
//                    }
                    if (((int) v2.getTag()) != res) {
                        v2.getDrawable().setAlpha(MapFragment.UNSELECTEDBUTTON);
                        v2.invalidate();
                    }
                }
                calcPath();
            }
    }

    private void addViewGroupForVehiculesButtons(ViewGroup vg) {
        viewGroupForVehiculesButtons = (ViewGroup) vg.findViewById(R.id.forVehiculesButtons);
        TypedArray vehicules = getResources().obtainTypedArray(R.array.vehicules_array);
        //hashMapVehiculesButtonsIdView = new HashMap<>();
        for (int index = 0; index < vehicules.length(); index++) {
            ImageView img = new ImageView(getActivity());
            int pad = getResources().getDimensionPixelSize(R.dimen.buttonsVehiculesPadding);
            img.setPadding(pad, pad, pad, pad);
            img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVehiculeChange(v);
                }
            });
            Integer res = vehicules.getResourceId(index,0);
            //hashMapVehiculesButtonsIdView.put(res,img);
            img.setImageDrawable(getDrawable(getActivity(),res));
            img.setTag(res);
            int alpha = (res==getVehiculeSelectedId())? MapFragment.SELECTEDBUTTON : MapFragment.UNSELECTEDBUTTON;
            img.getDrawable().setAlpha(alpha);
            viewGroupForVehiculesButtons.addView(img);
        }
    }

    private void addViewForMapPosition(ViewGroup vg) {
        viewForMapPosition = (ImageView) vg.findViewById(R.id.forMapPositionButtons);
        viewForMapPosition.setImageDrawable(getDrawable(getActivity(),R.drawable.mapwithfollow));
        viewForMapPosition.setTag(R.drawable.mapwithfollow);
        viewForMapPosition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) viewForMapPosition.getTag();
                if (id == R.drawable.mapwithfollow) {
                    viewForMapPosition.setTag(R.drawable.mapwithoutfollow);
                    viewForMapDirection.setTag(MapDirection.FIX);
                } else if (id == R.drawable.mapwithoutfollow) {
                    viewForMapPosition.setTag(R.drawable.mapwithfollow);
                }
                fixViewForMapPosition();
                fixViewForMapDirection();
                onLocationPlayerChanged(getPlayerLocation());
            }
        });
    }
    private void fixViewForMapPosition () {
        int id = (int) viewForMapPosition.getTag();
        viewForMapPosition.setImageDrawable(getDrawable(getActivity(),id));
    }
    private void addViewForMapDirection(ViewGroup vg) {
        viewForMapDirection = (ImageView) vg.findViewById(R.id.forMapDirectionButtons);
        viewForMapDirection.setImageDrawable(getDrawable(getActivity(),R.drawable.bearing));
        viewForMapDirection.setTag(MapDirection.PLAYER);
        viewForMapDirection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) viewForMapDirection.getTag();
                if (id == MapDirection.PLAYER) {
                    viewForMapDirection.setTag(MapDirection.FIX);
                } else if (id == MapDirection.FIX) {
                    viewForMapDirection.setTag(MapDirection.NORTH);
                } else if (id == MapDirection.NORTH) {
                    viewForMapDirection.setTag(MapDirection.PLAYER);
                    viewForMapPosition.setTag(R.drawable.mapwithfollow);
                }
                fixViewForMapDirection();
                fixViewForMapPosition();
                onBearingPlayerChanged(getmMyLocationListener().getBearingOfPlayer());
            }
        });
    }
    private void fixViewForMapDirection () {
        int id = (int) viewForMapDirection.getTag();
        if (id == MapDirection.PLAYER) {
            viewForMapDirection.setImageDrawable(getDrawable(getActivity(),R.drawable.bearing));
        } else if (id == MapDirection.FIX) {
            MyDrawable md = new MyDrawable(getDrawable(getActivity(),R.drawable.nobearing));
            md.setAngle(mMap.getMapPosition().getBearing());
            viewForMapDirection.setImageDrawable(md.getRotated());
        } else if (id == MapDirection.NORTH) {
            viewForMapDirection.setImageDrawable(getDrawable(getActivity(),R.drawable.nobearing));
        }
    }

    public PathWrapper getRoutePath() {
        return routePath;
    }

    private void createRouteGeoPointListHelper(GHResponse resp) {
        routePath = resp.getBest();
        if (mRouteGeoPointListHelper == null) mRouteGeoPointListHelper = new RouteGeoPointListHelper(this);
        mRouteGeoPointListHelper.startListenning();
    }

    public boolean isShortestPathRunning() {
        return shortestPathRunning;
    }

    public void calcRoutePath() {
        routePath = null;
        final double fromLat = getPlayerLocation().getLatitude();
        final double fromLon = getPlayerLocation().getLongitude();
        final double toLat = getSelectedZone().getCenterPoint().getLatitude();
        final double toLon = getSelectedZone().getCenterPoint().getLongitude();
        shortestPathRunning = true;
        new AsyncTask<Void, Void, GHResponse>() {
            float time;

            protected GHResponse doInBackground( Void... v ) {
                while ( ! (isReady()))  {}
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon);
                req.setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
                req.getHints().put("instructions", true);
                req.getHints().put("calc_points", true);
                req.setLocale(Locale.getDefault());
                req.setVehicle(vehiculeGHEncoding.get(getVehiculeSelectedId()).toString());
                //req.setWeighting("fastest");
                //hopper.getGraphHopperStorage();
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp;
            }

            protected void onPostExecute( GHResponse resp ) {
                if (!resp.hasErrors()) {
                    createRouteGeoPointListHelper(resp);
                }
                shortestPathRunning = false;
            }
        }.execute();
    }
    private void calcLinePath (){
        List<GeoPoint> listOfPoints = new ArrayList<>();
        listOfPoints.add(getPlayerLocation());
        listOfPoints.add(getSelectedZone().getCenterPoint());
        routePathLayer.setPoints(listOfPoints);
    }
    private void calcPath() {
        if ((getPlayerLocation() != null) && (getSelectedZone() != null)) {
            if (getVehiculeSelectedId() == R.drawable.compass) {
                if (mRouteGeoPointListHelper!=null) mRouteGeoPointListHelper.stopListenning();
                calcLinePath();
            } else {
                if (! (shortestPathRunning)) calcRoutePath();
            }
        } else {
            if (mRouteGeoPointListHelper!=null) mRouteGeoPointListHelper.stopListenning();
            //mRouteGeoPointListHelper=null;
            //routePath=null;
            if (routePathLayer!=null) routePathLayer.clearPath();
        }
    }

    public PathLayer getRoutePathLayer() {
        return routePathLayer;
    }

    @Override
    public void onZoneSelectChanged(Zone sZn, Zone sZnO ) {
        if (sZnO != null) onZoneChanged(sZnO);
        if (sZn != null ) {
            onZoneChanged(sZn);
            calcPath();
        }
    }
    @Override
    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {
        if (playerLocation != null) {
            if (playerMarkerItem != null) {
                playerMarkerItem.geoPoint=playerLocation;
            } else {
                MarkerItem ti = new MarkerItem("Test", "Test", playerLocation);
                TextDrawable d = new TextDrawable(getResources(),"test");
                MarkerSymbol ms = new MarkerSymbol(drawableToBitmap(d),HotspotPlace.LOWER_LEFT_CORNER,false);
                ti.setMarker(ms);
                mMarkerLayer.addItem(ti);

                playerMarkerItem = new MarkerItem("Player", "Player", playerLocation);
                playerMarkerSymbol = new MarkerSymbol(drawableToBitmap(playerDrawable.getmDrawable()), HotspotPlace.CENTER, false);
                playerMarkerItem.setMarker(playerMarkerSymbol);
                mMarkerLayer.addItem(playerMarkerItem);
            }
            mMarkerLayer.populate();
            if (((int)viewForMapPosition.getTag()) != R.drawable.mapwithoutfollow) {
                MapPosition pos = mMap.getMapPosition();
                pos.setPosition(playerLocation);
                mMap.setMapPosition(pos);
            }
            if (getSelectedZone() != null) updateCalcPath();
        }
    }

    private void updateCalcPath() {
        if (getVehiculeSelectedId()==R.drawable.compass) {
            calcLinePath();
        } else {
        }
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        if (item != playerMarkerItem) {
            Zone zn = (Zone) (item.getUid());
            zn.setVisible(!zn.isVisible());
        }
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        if (item != playerMarkerItem) {
            Zone zn = (Zone) (item.getUid());
            setSelectedZone(zn);
        }
        return true;
    }

    @Override
    public void onZoneChanged(Zone zone) {
        MarkerSymbol zoneMarkerSymbol = new MarkerSymbol(drawableToBitmap(getResources(),zone.getIconId()), HotspotPlace.CENTER);
        ZoneViewHelper zvh = zoneViewHelperHashMap.get(zone);
        if (zvh == null) {
            zvh = new ZoneViewHelper(zone);
            zoneViewHelperHashMap.put(zone,zvh);
        }
        if (zvh.markerItem == null) {
            zvh.markerItem = new MarkerItem(zone, zone.getName(), "", zone.getCenterPoint());
            mMarkerLayer.addItem(zvh.markerItem);
        }
        if (zvh.vectorLayer == null) {
            zvh.vectorLayer = new VectorLayer(mMap);
            mMap.layers().add(zvh.vectorLayer);
        }
        if (zvh.polygon == null) {
            zvh.polygon = new PolygonDrawable(zone.getShape().getAllGeoPoints());
            zvh.polygon.setStyle(mStyle4InvisibleZone);
            zvh.vectorLayer.add(zvh.polygon);
        }
        zvh.markerItem.setMarker(zone.isVisible()?zoneMarkerSymbol:null);
        mMarkerLayer.populate();


        Style st = (zone.isVisible()?
                (zone.isSelectedZone() ? mStyle4SelectedZone : mStyle4UnSelectedZone):
                mStyle4InvisibleZone);
        zvh.polygon.setStyle(st);
        zvh.vectorLayer.update();
    /*
        if (zvh.pathLayer == null) {
            zvh.pathLayer = new PathLayer(mMap,Color.TRANSPARENT);
        }
        if (zone.isVisible()) {
            if (mMap.layers().contains(zvh.pathLayer)) mMap.layers().remove(zvh.pathLayer);
        } else {
            if (! mMap.layers().contains(zvh.pathLayer)) mMap.layers().add(zvh.pathLayer);
        }
        int lineWidth=getResources().getDimensionPixelSize(R.dimen.widthOfZoneShape);
        int lineColor = ((zone.isVisible()?
                getResources().getColor(zone.isSelectedZone() ? R.color.colorOfZoneShapeSelected : R.color.colorOfZoneShapeNotSelected):
                Color.TRANSPARENT));
        LineStyle ls = new LineStyle(lineColor, lineWidth, Paint.Cap.BUTT);
        zvh.pathLayer.setStyle(ls);

        if (zone.isVisible()) {
            List<GeoPoint> temp = zone.getShape().getAllGeoPoints();
            temp.add(temp.get(0));
            zvh.pathLayer.setPoints(temp);
        } else {
            zvh.pathLayer.clearPath();
        }
    */
    }

    public void onBearingPlayerChanged(float angle) {
        int id = (int)viewForMapDirection.getTag();
        MapPosition pos = mMap.getMapPosition();
        float playerBearing = (180 + angle) % 360 - 180;
        if (id == MapDirection.NORTH)  {
            pos.setBearing(0);
        } else if (id == MapDirection.FIX) {
            fixViewForMapDirection();
        } else if (id == MapDirection.PLAYER) {
            pos.setBearing(-playerBearing);
        }
        mMap.setMapPosition(pos);

        playerDrawable.setAngle(playerBearing);
        playerMarkerSymbol = new MarkerSymbol(drawableToBitmap(playerDrawable.getRotated()), HotspotPlace.CENTER, false);
        playerMarkerItem.setMarker(playerMarkerSymbol);
        mMarkerLayer.populate();
    }
}
