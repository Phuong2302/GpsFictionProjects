package com.sdesimeur.android.gpsfiction.debug;

import android.util.Log;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionThing;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerBearingListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.geopoint.GeoPoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;

public abstract class WithDebug {
    private static final String[] clientIPs = new String[]{"192.168.3.150", "192.168.3.151", "192.168.4.150", "192.168.4.151", "192.168.44.150", "192.168.44.151", "192.168.42.150", "192.168.42.151", "192.168.43.150", "192.168.43.151", "192.168.5.150", "192.168.5.151"};
    public static float COEF = 5.0f;
    private static Socket socket = null;
    //private static boolean tousIPs=false;
    private static PrintWriter out = null;
    private static int lastClientIPok = 0;

    public final static void connectToServer() {
        System.out.println("connectToServer");
        //Debug.tousIPs = false;
        //Debug.lastClientIPok =0;

//       	while ((Debug.socket==null) || (Debug.socket.isClosed()) || (!(Debug.tousIPs))) {
        while (((socket == null) || (socket.isClosed())) && (lastClientIPok < clientIPs.length)) {
            //Debug.socket=new Socket("localhost",64800);
            //Debug.socket=new Socket("10.0.2.2",64800);
            try {
                //Debug.myLog("IP" , clientIPs[lastClientIPok]);
                socket = new Socket(clientIPs[lastClientIPok], 64800);
                //Debug.myLog("IP OK" , clientIPs[lastClientIPok]);

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                lastClientIPok++;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                lastClientIPok++;
            }
            //Debug.socket=new Socket("127.0.0.1",64800);
            //lastClientIPok = (lastClientIPok %  clientIPs.length );
        }
        if (socket != null) {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("erreur creation out");
                e.printStackTrace();
            }
            System.out.println("Socket ok : " + clientIPs[lastClientIPok]);
        } else {
            System.out.println("Pas de Socket");
        }

    }

    public final static void methodName(int depth) {
        final StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        StackTraceElement ste = stes[depth + 3];
        //System. out.println(ste[ste.length-depth].getClassName()+"#"+ste[ste.length-depth].getMethodName());
        System.out.println(ste.getClassName() + " : " + ste.getMethodName() + " (line " + ste.getLineNumber() + ")");
    }

    public final static void sendToMapTest(Zone zone) {
        connectToServer();
        if (out != null) {
            //FileWriter fw = new FileWriter(file);
            GeoPoint geoPoint;
            //bw.newLine();
            //Create object of Socket
            String command;
            String allcommand = "";
//        		        command = "ZoneName#"+MyLocationListener.getContext().getResources().getString(zone.getUuid())+"#"+Integer.toHexString(zone.getUuid());
            //command = "ZoneName#"+Integer.toHexString(zone.getUuid());
            command = "ZoneName#" + zone.getName();
            out.println(command);//sends command to server
            allcommand += (";" + command);
            //Debug.myLog("zone transmise",command);
            command = "ZoneActive#";
            if (zone.isActive()) command += "1";
            else command += "0";
            out.println(command);//sends command to server
            allcommand += (";" + command);
            command = "ZoneVisible#";
            if (zone.isVisible()) command += "1";
            else command += "0";
            out.println(command);//sends command to server
            allcommand += (";" + command);
            for (int i = 0; i < zone.getShape().size(); i++) {
                geoPoint = zone.getShape().get(i);
                command = "ZonePoint#" + String.valueOf(geoPoint.getLatitude()) + "," + String.valueOf(geoPoint.getLongitude());
                out.println(command);//sends command to server
                allcommand += (";" + command);
            }
            geoPoint = zone.getShape().get(0);
            command = "ZonePoint#" + String.valueOf(geoPoint.getLatitude()) + "," + String.valueOf(geoPoint.getLongitude());
            out.println(command);//sends command to server
            allcommand += (";" + command);
            command = "end#";
            out.println(command);//sends command to server
            closeSocket();
            allcommand += (";" + command);
            System.out.println(command);
            //Log.e("zone info",allcommand);
        } else {
            //Debug.myLog("zone transmise","non, pb de socket");
        }
    }

    @SuppressWarnings("unchecked")
    public final static void noStrictMode() {
        System.out.println("noStrictMode");
        try {
            @SuppressWarnings("rawtypes")
            Class strictModeClass = Class.forName("android.os.StrictMode");
            @SuppressWarnings("rawtypes")
            Class strictModeThreadPolicyClass = Class.forName("android.os.StrictMode$ThreadPolicy");
            Object laxPolicy = strictModeThreadPolicyClass.getField("LAX").get(null);
            Method method_setThreadPolicy = strictModeClass.getMethod(
                    "setThreadPolicy", strictModeThreadPolicyClass);
            method_setThreadPolicy.invoke(null, laxPolicy);
        } catch (Exception e) {

        }
        stopDebug();
    }

    private final static void closeSocket() {
        out.close(); //close output stream
        out = null;
        try {
            socket.close();
            socket = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  //close port
    }

    public final static void stopDebug() {
        System.out.println("stopDebug");
        connectToServer();
        out.println("exit");
        closeSocket();
    }

    public final static void myLog(String title, String text) {
        Log.e(title, text);
    }

    public final static void afficheGpsFictionThings(HashSet<GpsFictionThing> gpsFictionThings) {
        Iterator<GpsFictionThing> it = gpsFictionThings.iterator();
        while (it.hasNext()) {
            GpsFictionThing gft = it.next();
            //Debug.myLog("gpsFictionThing", gft.getName());
        }
    }

    public final static void afficheObjectPlayerLocationListener(GpsFictionActivity gpsFictionActivity, MyLocationListener.REGISTER register) {
        // TODO Auto-generated method stub
        if (gpsFictionActivity.getMyLocationListener() == null) {
            //Debug.myLog("afficheObjectPlayerLocationListener", "pas de myLocationListener");
        } else {
            if (gpsFictionActivity.getMyLocationListener().getPlayerLocationListener(register) == null) {
                //Debug.myLog("afficheObjectPlayerLocationListener", "pas de getPlayerLocationListener");
            } else {
                HashSet<PlayerLocationListener> playerLocationListener = gpsFictionActivity.getMyLocationListener().getPlayerLocationListener(register);
                Iterator<PlayerLocationListener> it = playerLocationListener.iterator();
                while (it.hasNext()) {
                    PlayerLocationListener gft = it.next();
                    Debug.myLog("ObjectPlayerLocationListener", ((GpsFictionThing) gft).getName());
                }
            }
        }
    }

    public final static void afficheObjectPlayerBearingListener(HashSet<PlayerBearingListener> playerBearingListener) {
        // TODO Auto-generated method stub
        Iterator<PlayerBearingListener> it = playerBearingListener.iterator();
        while (it.hasNext()) {
            PlayerBearingListener gft = it.next();
            Debug.myLog("ObjectPlayerBearingListener", ((GpsFictionThing) gft).getName());
        }

    }
}
