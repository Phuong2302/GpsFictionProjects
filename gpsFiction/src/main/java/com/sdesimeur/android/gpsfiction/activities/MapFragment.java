package com.sdesimeur.android.gpsfiction.activities;


import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import com.graphhopper.util.StopWatch;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionThing;
import com.sdesimeur.android.gpsfiction.classes.InstructionRoutePath;
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
import com.sdesimeur.android.gpsfiction.views.ImageViewWithId;

import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
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

import static android.view.ViewGroup.LayoutParams;
import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;
import static org.oscim.layers.marker.MarkerSymbol.HotspotPlace;


public class MapFragment extends MyTabFragment implements PlayerBearingListener, ZoneChangeListener, PlayerLocationListener, ZoneSelectListener, ItemizedLayer.OnItemGestureListener<MarkerItem> {
    MapView mapView;
    Map mMap;
    MapPreferences mPrefs;
    //private MapPosition mapPosition = null;
    private static final int INITZOOMLEVEL = 14;
    private static final int SELECTEDBUTTON = 255;
    private static final int UNSELECTEDBUTTON = 100;

    public int getVehiculeSelectedId() {
        return getGpsFictionActivity().getGpsFictionData().getVehiculeSelectedId();
    }
    public void setVehiculeSelectedId(int id) {
        getGpsFictionActivity().getGpsFictionData().setVehiculeSelectedId(id);
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

    public Zone getSelectedZone() {
        return getGpsFictionActivity().getGpsFictionData().getSelectedZone();
    }

    public void setSelectedZone(Zone selectedZone) {
        getGpsFictionActivity().getGpsFictionData().setSelectedZone(selectedZone);
    }

    public MyGeoPoint getPlayerLocation() {
        return getGpsFictionActivity().getMyLocationListener().getPlayerGeoPoint();
    }

    public float getPlayerBearing() {
        return getGpsFictionActivity().getMyLocationListener().getBearingOfPlayer();
    }

    private HashMap<Integer,FlagEncoder> vehiculeGHEncoding = new HashMap <Integer, FlagEncoder> () {{
        put(R.drawable.compass, null);
        put(R.drawable.pieton, new FootFlagEncoder());
        put(R.drawable.cycle, new BikeFlagEncoder());
        put(R.drawable.auto, new CarFlagEncoder());
    }};
    private MarkerSymbol playerMarkerSymbol = null;
    private HashMap<Zone,ZoneViewHelper> zoneViewHelperHashMap = null;
    private ItemizedLayer<MarkerItem> mMarkerLayer=null;
    private Drawable playerDrawable = null;
    private PathLayer routePathLayer = null;
    private PathWrapper routePath = null;
    private Translation mTranslation = null;
    private InstructionRoutePath nextInstruction = null;
//    private Bitmap playerBitmap = null;
//    private RotateDrawable playerRotateDrawable = null;

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
    public void onStart() {
        super.onStart();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRootView(inflater.inflate(R.layout.map_view, container, false));
        TranslationMap trm = new TranslationMap();
        trm.doImport();
        mTranslation = trm.getWithFallBack(Locale.getDefault());
        //setVehiculeSelectedId(R.drawable.compass);
        mapView=(MapView) this.getRootView().findViewById(R.id.mapView);
        mMap = mapView.map();
        zoneViewHelperHashMap = new HashMap<>();
        playerDrawable = getResources().getDrawable(R.drawable.player_marker);
//        playerRotateDrawable = (RotateDrawable) getResources().getDrawable(R.drawable.playerrotatemarker);
//        playerBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.player_marker);
        MarkerSymbol ms = new MarkerSymbol(drawableToBitmap(getResources(),R.drawable.transparent), HotspotPlace.CENTER);
        mMarkerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), ms , this);
        mMap.layers().add(mMarkerLayer);
        mPrefs = new MapPreferences(MapFragment.class.getName(), this.getContext());
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setPreferredLanguage(Locale.getDefault().getLanguage());
        File file = new File(mapsFolder, currentArea + ".map");
        if (tileSource.setMapFile(file.toString())) {
            VectorTileLayer l = mMap.setBaseMap(tileSource);
            mMap.setTheme(VtmThemes.DEFAULT);
            mMap.layers().add(new BuildingLayer(mMap, l));
            mMap.layers().add(new LabelLayer(mMap, l));
            mPrefs.clear();
            MapPosition pos = mMap.getMapPosition();
            pos.setZoomLevel(INITZOOMLEVEL);
            mMap.setMapPosition(pos);
        }
        if (routePathLayer == null) {
            routePathLayer = new PathLayer(mMap,Color.TRANSPARENT);
            mMap.layers().add(routePathLayer);
        }
        int lineWidth=2;
        int lineColor = Color.BLUE;
        LineStyle ls = new LineStyle(lineColor, lineWidth, Paint.Cap.BUTT);
        routePathLayer.setStyle(ls);
        ViewGroup vg = (ViewGroup) getRootView();
        addViewGroupForVehiculesButtons(vg);
        addViewGroupMapSCaleBar(vg);
        addViewGroupForZoomButtons(vg);
        loadGraphStorage();
        return getRootView();
    }

    private void registerAllZones() {
        Zone zone=null;
        Iterator<GpsFictionThing> itZone = this.getGpsFictionActivity().getGpsFictionData().getGpsFictionThing(Zone.class).iterator();
        while (itZone.hasNext()) {
            zone = (Zone) itZone.next();
            onZoneChanged(zone);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        shortestPathRunning = false;
        mPrefs.load(mapView.map());
        mapView.onResume();
        registerAllZones();
        getGpsFictionActivity().getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.FRAGMENT, this);
        getGpsFictionActivity().getMyLocationListener().addPlayerBearingListener(MyLocationListener.REGISTER.FRAGMENT, this);
        getGpsFictionActivity().getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.FRAGMENT, this);
        getGpsFictionActivity().getGpsFictionData().addZoneChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getGpsFictionActivity().getMyLocationListener().removePlayerLocationListener(MyLocationListener.REGISTER.FRAGMENT, this);
        getGpsFictionActivity().getMyLocationListener().removePlayerBearingListener(MyLocationListener.REGISTER.FRAGMENT, this);
        getGpsFictionActivity().getGpsFictionData().removeZoneSelectListener(GpsFictionData.REGISTER.FRAGMENT, this);
        getGpsFictionActivity().getGpsFictionData().removeZoneChangeListener(this);
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
    //    hopper.clean();
        hopper.close();
        hopper = null;
        mMarkerLayer = null;
        zoneViewHelperHashMap = null;
        playerMarkerItem = null;
        routePathLayer = null;
        //System.gc();
        super.onDetach();
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
            int res = ((ImageViewWithId) v).getDrawableId();
            if (getVehiculeSelectedId() != res) {
                setVehiculeSelectedId(res);
                for (int position = 0; position < this.viewGroupForVehiculesButtons.getChildCount(); position++) {
                    View vi = this.viewGroupForVehiculesButtons.getChildAt(position);
                    int alpha = ((vi == v) ? MapFragment.SELECTEDBUTTON : MapFragment.UNSELECTEDBUTTON);
                    ((ImageView) vi).getDrawable().setAlpha(alpha);
                    vi.invalidate();
                }
            }
            calcPath();
    }

    private void addViewGroupForVehiculesButtons(ViewGroup vg) {
        viewGroupForVehiculesButtons = (ViewGroup) vg.findViewById(R.id.forVehiculesButtons);
        TypedArray vehicules = getResources().obtainTypedArray(R.array.vehicules_array);
        for (int index = 0; index < vehicules.length(); index++) {
            ImageViewWithId img = new ImageViewWithId(getActivity());
            int pad = getResources().getDimensionPixelSize(R.dimen.buttonsVehiculesPadding);
            img.setPadding(pad, pad, pad, pad);
            img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVehiculeChange(v);
                }
            });
            int res = vehicules.getResourceId(index, -1);
            img.setDrawableId(res);
            int alpha;
            //if ((playerLocation == null) || (selectedZone == null)) {
            //    alpha = (res==R.drawable.compass)?MapFragment.SELECTEDBUTTON :MapFragment.UNSELECTEDBUTTON;
            //} else {
                alpha = ((res == getVehiculeSelectedId()) ? MapFragment.SELECTEDBUTTON : MapFragment.UNSELECTEDBUTTON);
            //    calcPath();
            //}
            img.getDrawable().setAlpha(alpha);
            viewGroupForVehiculesButtons.addView(img);
        }
        vehicules.recycle();
    }

    private void addViewGroupMapSCaleBar(ViewGroup vg) {
        LinearLayout l = (LinearLayout) vg.findViewById(R.id.forScaleBar);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_LEFT | RelativeLayout.ALIGN_BOTTOM);
    }

    private InstructionRoutePath getNextInstruction () {
        Instruction nextInst = routePath.getInstructions().find(getPlayerLocation().getLatitude(), getPlayerLocation().getLongitude(), 2000);
        String nextInstructionString = nextInst.getTurnDescription(mTranslation);
        double distanceToNextInstruction = nextInst.getDistance();
        nextInstruction = new InstructionRoutePath(nextInstructionString,distanceToNextInstruction);
        return nextInstruction;
    }

    public PathWrapper getRoutePath() {
        return routePath;
    }

    private void createRouteGeoPointListAutoClean(GHResponse resp) {
        routePath = resp.getBest();
        RouteGeoPointListAutoClean mRouteGeoPointListAutoClean = new RouteGeoPointListAutoClean(this);
        // TODO Auto-generated method stub
    }

    public void calcRoutePath(final double fromLat, final double fromLon, final double toLat, final double toLon ) {
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
        if ((getPlayerLocation() != null) && (getSelectedZone() != null))
        if (getVehiculeSelectedId() == R.drawable.compass) {
            calcLinePath();
        } else {
            final double fromLat = getPlayerLocation().getLatitude();
            final double fromLon = getPlayerLocation().getLongitude();
            final double toLat = getSelectedZone().getCenterPoint().getLatitude();
            final double toLon = getSelectedZone().getCenterPoint().getLongitude();
            if (! (shortestPathRunning)) calcRoutePath(fromLat, fromLon, toLat, toLon);
        }
    }

    public PathLayer getRoutePathLayer() {
        return routePathLayer;
    }

    @Override
    public void onZoneSelectChanged(Zone sZn) {
        // TODO Auto-generated method stub
        calcPath();
    }
    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        // TODO Auto-generated method stub
        if (getPlayerLocation() != null) {
            if (playerMarkerItem != null) {
//                mMarkerLayer.removeItem(playerMarkerItem);
                playerMarkerItem.geoPoint=getPlayerLocation();
            } else {
                playerMarkerItem = new MarkerItem("Player", "", getPlayerLocation());
                playerMarkerSymbol = new MarkerSymbol(drawableToBitmap(playerDrawable), HotspotPlace.CENTER, false);
                playerMarkerItem.setMarker(playerMarkerSymbol);
                mMarkerLayer.addItem(playerMarkerItem);
            }
            mMarkerLayer.populate();
//            this.playerMarkerItem = new PlayerRotatingMarker(this.playerLocation, getResources(), R.drawable.player_marker);
                //this.playerMarker = new PlayerRotatingMarker  (playerPosition);
                //this.playerMarker.setResource(getResources(), R.drawable.player_marker);
                //this.playerMarker.register(this.getGpsFictionActivity());
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
            Toast.makeText(getContext(), getNextInstruction().nextInstructionString, Toast.LENGTH_LONG).show();
            //   TODO on s'ecarte du chemin...


            // On reste sur le chemin

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
        if (zvh.pathLayer == null) {
            zvh.pathLayer = new PathLayer(mMap,Color.TRANSPARENT);
            mMap.layers().add(zvh.pathLayer);
        }
        zvh.markerItem.setMarker(zone.isVisible()?zoneMarkerSymbol:null);
        mMarkerLayer.populate();

//        int lineWidth = (zone.isVisible()?2:0);
        int lineWidth=2;
        int lineColor = (zone.isVisible()?
                (zone.isSelectedZone()?Color.RED:Color.YELLOW):
                Color.TRANSPARENT);
        LineStyle ls = new LineStyle(lineColor, lineWidth, Paint.Cap.BUTT);
        zvh.pathLayer.setStyle(ls);
        if (zone.isVisible()) {
            List<GeoPoint> temp = zone.getShape().getAllGeoPoints();
            temp.add(temp.get(0));
            zvh.pathLayer.setPoints(temp);
        } else {
            zvh.pathLayer.clearPath();
        }
//        mMap.updateMap(true);
    }
    private Drawable getRotateDrawable(final Drawable d, final float angle) {
        final Drawable[] arD = { d };
        return new LayerDrawable(arD) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, d.getBounds().width() / 2, d.getBounds().height() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
    }

    public void onBearingPlayerChanged(PlayerBearingEvent playerBearingEvent) {
//        float playerBearingOld = playerBearing;
        float playerBearing = (180 + playerBearingEvent.getBearing()) % 360 - 180;
        MapPosition pos = mMap.getMapPosition();
        pos.setBearing(-playerBearing);
        mMap.setMapPosition(pos);

     /*   playerRotateDrawable.setFromDegrees(playerBearingOld);
        playerRotateDrawable.setToDegrees(playerBearing);
        playerRotateDrawable.setLevel(5);
        playerMarkerSymbol = new MarkerSymbol(drawableToBitmap(playerRotateDrawable), HotspotPlace.CENTER, false);
        */
     /*   Matrix matrix = new Matrix();
        matrix.postRotate(playerBearing);
        Bitmap bm = Bitmap.createBitmap(playerBitmap, 0, 0, playerBitmap.getWidth(), playerBitmap.getHeight(), matrix, true);
        Drawable d = new BitmapDrawable(bm);
        playerMarkerSymbol = new MarkerSymbol(drawableToBitmap(d), HotspotPlace.CENTER, false);
        */
        playerMarkerSymbol = new MarkerSymbol(drawableToBitmap(getRotateDrawable(playerDrawable,playerBearing)), HotspotPlace.CENTER, false);

        playerMarkerItem.setMarker(playerMarkerSymbol);
        mMarkerLayer.populate();
    }
}
