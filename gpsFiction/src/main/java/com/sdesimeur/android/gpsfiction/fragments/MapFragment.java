package com.sdesimeur.android.gpsfiction.fragments;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.sdesimeur.android.gpsfiction.activities.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionThing;
import com.sdesimeur.android.gpsfiction.classes.PlayerBearingListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.VehiculeSelectedIdListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneChangeListener;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.classes.ZoneViewHelper;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Color;
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
import java.util.Locale;

import static android.support.v4.content.ContextCompat.getColor;
import static android.support.v4.content.ContextCompat.getDrawable;
import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;
import static org.oscim.layers.marker.MarkerSymbol.HotspotPlace;


public class MapFragment
        extends MyTabFragment
        implements PlayerBearingListener, ZoneChangeListener, PlayerLocationListener, ZoneSelectListener, ItemizedLayer.OnItemGestureListener<MarkerItem> {
    private static final float MINMOVE = 20;
    MapView mapView;
    Map mMap;
    //MapPreferences mPrefs;
    //private MapPosition mapPosition = null;
    private static final int SELECTEDBUTTON = 255;
    private static final int UNSELECTEDBUTTON = 100;
    private ImageView viewForMapDirection;
    private ImageView viewForMapPosition;
    //int clickCount;
    //private int PositionTouchX;
    //private int PositionTouchY;
    //long startTouchTime = 0 ;
    private Bitmap playerBitmap;
    private float dX2;
    private float dY2;
    //private int lastAction;
    private float dX1;
    private float dY1;

    public GpsFictionData getmGpsFictionData() {
        return getmGpsFictionControler().getmGpsFictionData();
    }

    private static class MapDirection {
        public static final int PLAYER=0;
        public static final int FIX = 1;
        public static final int NORTH = 2;
    }
    private File mapsFolder;
    static final private String CURRENTAREA = "jeu";
    private MarkerItem playerMarkerItem;
    private ViewGroup viewGroupForVehiculesButtons = null;
    private HashMap<Zone,ZoneViewHelper> zoneViewHelperHashMap = null;
    private ItemizedLayer<MarkerItem> mMarkerLayer=null;
    private PathLayer routePathLayer = null;
    private Style mStyle4SelectedZone;
    private Style mStyle4UnSelectedZone;
    private Style mStyle4InvisibleZone;

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

    private float getBearingOfPlayer() {
            return getmGpsFictionControler().getBearingOfPlayer();
    }

    public MyGeoPoint getPlayerLocation() {
        return getmGpsFictionControler().getPlayerGeoPoint();
    }

    public MapFragment() {
        super();
    }



    public MapView getMapView() {
        return this.mapView;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRootView(inflater.inflate(R.layout.map_view, container, false));
        mStyle4SelectedZone = Style.builder()
                .strokeColor(getColor(getActivity(),R.color.colorOfZoneShapeSelected))
                .fillColor(getColor(getActivity(),R.color.colorOfZoneShapeSelected))
                .build();
        mStyle4UnSelectedZone = Style.builder()
                .strokeColor(getColor(getActivity(),R.color.colorOfZoneShapeNotSelected))
                .fillColor(getColor(getActivity(),R.color.colorOfZoneShapeNotSelected))
                .build();
        mStyle4InvisibleZone = Style.builder()
                .fillColor(getColor(getActivity(),R.color.colorOfZoneShapeInvisible))
                .fillAlpha(0f)
                .strokeWidth(0)
                .strokeColor(getColor(getActivity(),R.color.colorOfZoneShapeInvisible))
                .build();
        mapView=(MapView) this.getRootView().findViewById(R.id.mapView);
        mMap = mapView.map();
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, android.view.MotionEvent event) {
                int pointerCount = event.getPointerCount();
                switch (event.getActionMasked()) {
    	              case MotionEvent.ACTION_DOWN:
    	                  dX1 = event.getRawX();
    	                  dY1 = event.getRawY();
    	                  break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dX2 = event.getX();
                        dY2 = event.getY();
 //                       lastAction = MotionEvent.ACTION_POINTER_DOWN;
                        break;
    	            case MotionEvent.ACTION_POINTER_UP:
                        if ((Math.abs(dX2-event.getX())<MINMOVE) && (Math.abs(dY2 - event.getY())<MINMOVE)) break;
                    case MotionEvent.ACTION_UP:
                          if ((Math.abs(dX1-event.getRawX())<MINMOVE) && (Math.abs(dY1 - event.getRawY())<MINMOVE)) break;
                                  viewForMapPosition.setTag(R.drawable.mapwithoutfollow);
                                  fixViewForMapPosition();
                                  onLocationPlayerChanged(getPlayerLocation());
                                  viewForMapDirection.setTag(MapDirection.FIX);
                                  fixViewForMapDirection();
                                  onBearingPlayerChanged(getBearingOfPlayer());
    	                  break;
    	              default:
    	                  return false;
    	          }
                return false;
            }
        });
        zoneViewHelperHashMap = new HashMap<>();
        playerBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.player_marker);
        MarkerSymbol ms = new MarkerSymbol(drawableToBitmap(getResources(),R.drawable.transparent), HotspotPlace.CENTER);
        mMarkerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), ms , this);
        if (playerMarkerItem == null) {
            playerMarkerItem = new MarkerItem("Player", "Player", new MyGeoPoint(90,0));
            playerMarkerItem.setMarker(getMarkerSymbolWithBitmap(getRotatedBitmap(playerBitmap,0f,false),HotspotPlace.CENTER,false));
            mMarkerLayer.addItem(playerMarkerItem);
        }
        mMap.layers().add(mMarkerLayer);
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setPreferredLanguage(Locale.getDefault().getLanguage());
        File file = new File(mapsFolder, CURRENTAREA + ".map");
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
        int lineColor = getColor(getActivity(),R.color.colorOfRouteLine);
        LineStyle ls = new LineStyle(lineColor, lineWidth, org.oscim.backend.canvas.Paint.Cap.ROUND);
        routePathLayer.setStyle(ls);
        getmGpsFictionControler().setRoutePathLayer(routePathLayer);
        ViewGroup vg = (ViewGroup) getRootView();
        addViewGroupForVehiculesButtons(vg);
        View viewForDistanceToDest = vg.findViewById(R.id.forDistanceToDest);
        viewForDistanceToDest.setTag(R.id.gpsFictionControlerId,getmGpsFictionControler());
        addViewForMapDirection(vg);
        addViewForMapPosition(vg);
        addViewGroupForZoomButtons(vg);
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
        //mPrefs.load(mapView.map());
        mapView.onResume();
        registerAllZones();
        GpsFictionControler gfc = getmGpsFictionControler();
        gfc.addPlayerLocationListener(GpsFictionControler.REGISTER.FRAGMENT, this);
        gfc.addPlayerBearingListener(GpsFictionControler.REGISTER.FRAGMENT, this);
        gfc.addZoneSelectListener(GpsFictionControler.REGISTER.FRAGMENT, this);
        gfc.addZoneChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        GpsFictionControler gfc = getmGpsFictionControler();
        gfc.removePlayerLocationListener(GpsFictionControler.REGISTER.FRAGMENT, this);
        gfc.removePlayerBearingListener(GpsFictionControler.REGISTER.FRAGMENT, this);
        gfc.removeZoneSelectListener(GpsFictionControler.REGISTER.FRAGMENT, this);
        gfc.removeZoneChangeListener(this);
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
    private void addViewGroupForVehiculesButtons(ViewGroup vg) {
        TypedArray vehicules = getResources().obtainTypedArray(R.array.vehicules_array);
        if (vehicules.length() > 1) {
            viewGroupForVehiculesButtons = (ViewGroup) vg.findViewById(R.id.forVehiculesButtons);
            for (int index = 0; index < vehicules.length(); index++) {
                class MyImageView extends ImageView implements VehiculeSelectedIdListener {
                    public MyImageView(Context context) {
                        super(context);
                    }

                    @Override
                    public void onVehiculeSelectedId(int id) {
                        int res = (int) getTag();
                        getDrawable().setAlpha((res == id) ? MapFragment.SELECTEDBUTTON : MapFragment.UNSELECTEDBUTTON);
                        invalidate();
                    }
                }
                ImageView img = new MyImageView(getActivity());
                int pad = getResources().getDimensionPixelSize(R.dimen.buttonsVehiculesPadding);
                img.setPadding(pad, pad, pad, pad);
                img.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int res = (int) v.getTag();
                        if (getVehiculeSelectedId() != res) {
                            setVehiculeSelectedId(res);
                        }
                        //onVehiculeChange(v);
                    }
                });
                Integer res = vehicules.getResourceId(index, 0);
                img.setImageDrawable(getDrawable(getActivity(), res));
                int sZ = getResources().getDimensionPixelSize(R.dimen.buttonsVehiculesSize);
                ViewGroup.LayoutParams parms = new ViewGroup.LayoutParams(sZ, sZ);
                img.setLayoutParams(parms);
                img.setTag(res);
                int alpha = (res == getVehiculeSelectedId()) ? MapFragment.SELECTEDBUTTON : MapFragment.UNSELECTEDBUTTON;
                img.getDrawable().setAlpha(alpha);
                viewGroupForVehiculesButtons.addView(img);
                GpsFictionControler gfc = getmGpsFictionControler();
                gfc.addVehiculeSelectedIdListener((VehiculeSelectedIdListener) img);
            }
        } else {
            setVehiculeSelectedId(vehicules.getResourceId(0,R.drawable.compass));
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
                onBearingPlayerChanged(getBearingOfPlayer());
                //if (id == MapDirection.NORTH)
                    onLocationPlayerChanged(getPlayerLocation());
            }
        });
    }
    private void fixViewForMapDirection () {
        int id = (int) viewForMapDirection.getTag();
        if (id == MapDirection.PLAYER) {
            viewForMapDirection.setImageDrawable(getDrawable(getActivity(),R.drawable.bearing));
        } else if (id == MapDirection.FIX) {
            viewForMapDirection.setImageBitmap(getRotatedBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.nobearing),mMap.getMapPosition().getBearing(),true));
        } else if (id == MapDirection.NORTH) {
            viewForMapDirection.setImageDrawable(getDrawable(getActivity(),R.drawable.nobearing));
        }
    }

    public void onZoneSelectChanged(Zone sZn, Zone sZnO ) {
        if (sZnO != null) {
            onZoneChanged(sZnO);
        }
    }
    @Override
    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {
        if (playerLocation != null) {
            playerMarkerItem.geoPoint=playerLocation;
            mMarkerLayer.populate();
            if (((int)viewForMapPosition.getTag()) != R.drawable.mapwithoutfollow) {
                MapPosition pos = mMap.getMapPosition();
                pos.setPosition(playerLocation);
                mMap.setMapPosition(pos);
                mMap.updateMap(true);
            }
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

        playerMarkerItem.setMarker(getMarkerSymbolWithBitmap(getRotatedBitmap(playerBitmap,playerBearing,false),HotspotPlace.CENTER,false));
        mMarkerLayer.populate();
    }

    private Bitmap getRotatedBitmap (Bitmap bitmap , float angle, boolean keepSize){
        Matrix m = new Matrix();
        m.setRotate(angle, bitmap.getWidth()/2, bitmap.getHeight()/2);
        Bitmap b = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
        if (keepSize) {
            int dx = (b.getWidth() - bitmap.getWidth()) / 2;
            int dy = (b.getHeight() - bitmap.getHeight()) / 2;
            return Bitmap.createBitmap(b,dx,dy,bitmap.getWidth(),bitmap.getHeight());
        } else {
            return b;
        }
    }
    private MarkerSymbol getMarkerSymbolWithBitmap (Bitmap bitmap, HotspotPlace hp, boolean bill) {
        AndroidBitmap ab = new AndroidBitmap(bitmap);
        return new MarkerSymbol(ab, hp, bill);
    }
    private Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.DITHER_FLAG|Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
}
}