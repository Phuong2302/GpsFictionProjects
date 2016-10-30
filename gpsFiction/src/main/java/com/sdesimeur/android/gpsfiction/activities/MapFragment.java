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
import android.widget.Toast;
import android.widget.ZoomControls;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.util.BikeFlagEncoder;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.FootFlagEncoder;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionThing;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerBearingEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerBearingListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.RouteGeoPointListAutoClean;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneChangeListener;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.classes.ZoneViewHelper;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.helpers.DistanceToTextHelper;
import com.sdesimeur.android.gpsfiction.utils.MyDrawable;

import org.oscim.android.MapView;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
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


public class MapFragment extends MyTabFragment implements PlayerBearingListener, ZoneChangeListener, PlayerLocationListener, ZoneSelectListener, ItemizedLayer.OnItemGestureListener<MarkerItem> {
    MapView mapView;
    Map mMap;
    //MapPreferences mPrefs;
    //private MapPosition mapPosition = null;
    private static final int INITZOOMLEVEL = 14;
    private static final int SELECTEDBUTTON = 255;
    private static final int UNSELECTEDBUTTON = 100;
    private String nextInstructionString ="";

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
    private VectorLayer mVectorLayer = null;
    private PathWrapper routePath = null;
    private Translation mTranslation = null;
    private HashMap<Integer, ImageView> hashMapVehiculesButtonsIdView = null;
    private RouteGeoPointListAutoClean mRouteGeoPointListAutoClean = null;
    private Style mStyle4SelectedZone;
    private Style mStyle4UnSelectedZone;
    private Style mStyle4InvisibleZone;
//    private RotateDrawable playerRotateDrawable = null;
//    private Bitmap playerBitmap = null;

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

    public float getPlayerBearing() {
        return getmMyLocationListener().getBearingOfPlayer();
    }

    public MapFragment() {
        super();
        //	this.setNameId(R.string.tabMapTitle);
    }

    public MapView getMapView() {
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
                .fillColor(getResources().getColor(R.color.colorOfZoneShapeSelected))
                .build();
        mStyle4UnSelectedZone = Style.builder()
                .fillColor(getResources().getColor(R.color.colorOfZoneShapeNotSelected))
                .build();
        mStyle4InvisibleZone = Style.builder()
                .fillColor(getResources().getColor(R.color.colorOfZoneShapeInvisible))
                .fillAlpha(0f)
                .strokeWidth(0)
                .strokeColor(getResources().getColor(R.color.colorOfZoneShapeInvisible))
                .build();
        TranslationMap trm = new TranslationMap();
        trm.doImport();
        mTranslation = trm.getWithFallBack(Locale.getDefault());
        //setVehiculeSelectedId(R.drawable.compass);
        mapView=(MapView) this.getRootView().findViewById(R.id.mapView);
        mMap = mapView.map();
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
            pos.setZoomLevel(INITZOOMLEVEL);
            mMap.setMapPosition(pos);
        }
        if (routePathLayer == null) {
            routePathLayer = new PathLayer(mMap, Color.TRANSPARENT);
            mMap.layers().add(routePathLayer);
        }
        if (mVectorLayer == null) {
            mVectorLayer = new VectorLayer(mMap);
            mMap.layers().add(mVectorLayer);
        }
        int lineWidth=getResources().getDimensionPixelSize(R.dimen.widthOfRouteLine);
        //int lineWidth=2;
        int lineColor = getResources().getColor(R.color.colorOfRouteLine);
        //int lineColor = Color.BLUE;
        LineStyle ls = new LineStyle(lineColor, lineWidth, Paint.Cap.BUTT);
        routePathLayer.setStyle(ls);
        ViewGroup vg = (ViewGroup) getRootView();
        addViewGroupForVehiculesButtons(vg);
        addViewGroupForMapDirection(vg);
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
        int alpha = MapFragment.SELECTEDBUTTON;
        hashMapVehiculesButtonsIdView.get(getVehiculeSelectedId()).getDrawable().setAlpha(alpha);
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
        if (mRouteGeoPointListAutoClean != null) mRouteGeoPointListAutoClean.destroy();
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
                pos.setZoomLevel(pos.getZoomLevel()+1);
                mMap.setMapPosition(pos);
            }
        });
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPosition pos = mMap.getMapPosition();
                pos.setZoomLevel(pos.getZoomLevel()-1);
                mMap.setMapPosition(pos);
            }
        });
    }

    private void onVehiculeChange(View v) {
            ImageView v1 = (ImageView) v;
            if (hashMapVehiculesButtonsIdView.get(getVehiculeSelectedId()) != v1) {
                for (int idx : hashMapVehiculesButtonsIdView.keySet()){
                    ImageView v2 = hashMapVehiculesButtonsIdView.get(idx);
                    if (v2 == v1) {
                        setVehiculeSelectedId(idx);
                    }
                    int alpha = ((v2 == v1) ? MapFragment.SELECTEDBUTTON : MapFragment.UNSELECTEDBUTTON);
                    v2.getDrawable().setAlpha(alpha);
                    v2.invalidate();
                }
            }
            calcPath();
    }

    private void addViewGroupForVehiculesButtons(ViewGroup vg) {
        viewGroupForVehiculesButtons = (ViewGroup) vg.findViewById(R.id.forVehiculesButtons);
        TypedArray vehicules = getResources().obtainTypedArray(R.array.vehicules_array);
        hashMapVehiculesButtonsIdView = new HashMap<>();
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
            hashMapVehiculesButtonsIdView.put(res,img);
            img.setImageDrawable(getDrawable(getActivity(),res));
            int alpha = MapFragment.UNSELECTEDBUTTON;
            img.getDrawable().setAlpha(alpha);
            viewGroupForVehiculesButtons.addView(img);
        }
    }

    private void addViewGroupForMapDirection(ViewGroup vg) {
        ImageView v = (ImageView) vg.findViewById(R.id.forMapDirectionButtons);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //lp.addRule(RelativeLayout.ALIGN_LEFT | RelativeLayout.ALIGN_BOTTOM);
    }

    private void setNextInstruction () {
        if (routePath != null && !shortestPathRunning) {
            Instruction nextInst = routePath.getInstructions().find(getPlayerLocation().getLatitude(), getPlayerLocation().getLongitude(), 2000);
            PointList pl = nextInst.getPoints();
            int idx = pl.getSize() - 1;
            MyGeoPoint nxtMyGeoPoint = new MyGeoPoint(pl.getLatitude(idx), pl.getLongitude(idx));
            DistanceToTextHelper dst = new DistanceToTextHelper(nxtMyGeoPoint.distanceTo(getPlayerLocation()));
            nextInstructionString = mTranslation.tr("web.to_hint", new Object[0]) + " " + dst.getDistanceInText() + ", " + nextInst.getTurnDescription(mTranslation);
        }
    }

    public PathWrapper getRoutePath() {
        return routePath;
    }

    private void createRouteGeoPointListAutoClean(GHResponse resp) {
        routePath = resp.getBest();
        mRouteGeoPointListAutoClean = new RouteGeoPointListAutoClean(this);
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
                    createRouteGeoPointListAutoClean(resp);
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
                calcLinePath();
            } else {
                if (! (shortestPathRunning)) calcRoutePath();
            }
        } else {
            if (mRouteGeoPointListAutoClean!=null) mRouteGeoPointListAutoClean.destroy();
            mRouteGeoPointListAutoClean=null;
            routePath=null;
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
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        if (getPlayerLocation() != null) {
            if (playerMarkerItem != null) {
                playerMarkerItem.geoPoint=getPlayerLocation();
            } else {
                playerMarkerItem = new MarkerItem("Player", "", getPlayerLocation());
                playerMarkerSymbol = new MarkerSymbol(drawableToBitmap(playerDrawable.getmDrawable()), HotspotPlace.CENTER, false);
                playerMarkerItem.setMarker(playerMarkerSymbol);
                mMarkerLayer.addItem(playerMarkerItem);
            }
            mMarkerLayer.populate();
            MapPosition pos = mMap.getMapPosition();
            pos.setPosition(getPlayerLocation());
            mMap.setMapPosition(pos);
            if (getSelectedZone() != null) updateCalcPath();
        }
    }

    private void updateCalcPath() {
        if (getVehiculeSelectedId()==R.drawable.compass) {
            calcLinePath();
        } else {
            setNextInstruction();
            Toast.makeText(getmGpsFictionActivity(), nextInstructionString, Toast.LENGTH_LONG).show();
            getmGpsFictionActivity().speak(nextInstructionString);
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
    //    if (zvh.pathLayer == null) {
    //        zvh.pathLayer = new PathLayer(mMap,Color.TRANSPARENT);
    //    }
        if (zvh.polygon == null) {
            zvh.polygon = new PolygonDrawable(zone.getShape().getAllGeoPoints());
            zvh.polygon.setStyle(mStyle4InvisibleZone);
            mVectorLayer.add(zvh.polygon);
        }
        zvh.markerItem.setMarker(zone.isVisible()?zoneMarkerSymbol:null);
        mMarkerLayer.populate();
/*
        if (zone.isVisible()) {
            zvh.polygon.setStyle(zone.isSelectedZone() ? mStyle4SelectedZone : mStyle4UnSelectedZone);
            if (!zvh.isPolygonVisible) {
                mVectorLayer.add(zvh.polygon);
                zvh.isPolygonVisible = true;
            }
        } else {
            if (zvh.isPolygonVisible) {
                mVectorLayer.remove(zvh.polygon);
                zvh.isPolygonVisible = false;
            }
        }
*/

        zvh.polygon.setStyle(zone.isVisible()?
                (zone.isSelectedZone() ? mStyle4SelectedZone : mStyle4UnSelectedZone):
                mStyle4InvisibleZone);
        mVectorLayer.update();
/*
        if (zone.isVisible()) {
            if (mMap.layers().contains(zvh.pathLayer)) mMap.layers().remove(zvh.pathLayer);
        } else {
            if (! mMap.layers().contains(zvh.pathLayer)) mMap.layers().add(zvh.pathLayer);
        }
        */
            /*
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


    public void onBearingPlayerChanged(PlayerBearingEvent playerBearingEvent) {
        float playerBearing = (180 + playerBearingEvent.getBearing()) % 360 - 180;
        MapPosition pos = mMap.getMapPosition();
        pos.setBearing(-playerBearing);
        mMap.setMapPosition(pos);

        playerDrawable.setAngle(playerBearing);
        playerMarkerSymbol = new MarkerSymbol(drawableToBitmap(playerDrawable.getRotated()), HotspotPlace.CENTER, false);

        playerMarkerItem.setMarker(playerMarkerSymbol);
        mMarkerLayer.populate();
    }
}
