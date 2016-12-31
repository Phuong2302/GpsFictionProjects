package com.sdesimeur.android.gpsfiction.polygon;

import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.math.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.oscim.core.GeoPoint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MyPolygon extends LinkedList<MyGeoPoint> {

    public MyPolygon() {
        super();
    }
/*
    public Bundle getByBundle() {
        Bundle dest = new Bundle();
        Iterator<MyGeoPoint> it = this.iterator();
        MyGeoPoint gp = null;
        dest.putInt("shapeNbPoints", this.size());
        int index = 0;
        while (it.hasNext()) {
            gp = it.next();
            dest.putBundle("shapePoint" + index, gp.getByBundle());
            index++;
        }
        return dest;
    }

    public void setByBundle(Bundle in) {
        MyGeoPoint gp = null;
        int nbGeoGointInShape = in.getInt("shapeNbPoints");
        for (int index = 0; index < nbGeoGointInShape; index++) {
            gp = MyGeoPoint.setByBundle(in.getBundle("shapePoint" + index));
            this.add(gp);
        }
    }
*/
    public JSONArray getJsonArray() throws JSONException {
        JSONArray obj  = new JSONArray();
        Iterator<MyGeoPoint> it = this.iterator();
        MyGeoPoint gp = null;
        while (it.hasNext()) {
            gp = it.next();
            obj.put(gp.getJsonArray());
        }
        return  obj;
    }

    public void setJsonArray (JSONArray obj) throws JSONException {
        MyGeoPoint gp = null;
        for (int index=0; index < obj.length(); index ++) {
            gp = MyGeoPoint.newFromJsonArray(obj.getJSONArray(index));
            this.add(gp);
        }
    }

    public void addMyGeoPoint(MyGeoPoint gp) {
        this.add(gp);
    }

    public boolean contains(MyGeoPoint gp) {
        return this.contains(gp.getX(), gp.getY());
    }

    public List<GeoPoint> getAllGeoPoints () {
        Iterator<MyGeoPoint> it = this.iterator();
        List <GeoPoint> res = new LinkedList<>();
        while (it.hasNext())
            res.add(it.next());
        return res;
    }
//       /**
//        * Test if a high-precision rectangle intersects the shape. This is true
//        * if any point in the rectangle is in the shape. This implementation is
//        * precise.
//        *
//        * @param x the x coordinate of the rectangle
//        * @param y the y coordinate of the rectangle
//        * @param w the width of the rectangle, treated as point if negative
//        * @param h the height of the rectangle, treated as point if negative
//        * @return true if the rectangle intersects this shape
//        * @since 1.2
//        */
//       public boolean intersects(double x, double y, double w, double h)
//       {
//         /* Does any edge intersect? */
//         if (evaluateCrossings(x, y, false, w) != 0 /* top */
//             || evaluateCrossings(x, y + h, false, w) != 0 /* bottom */
//             || evaluateCrossings(x + w, y, true, h) != 0 /* right */
//             || evaluateCrossings(x, y, true, h) != 0) /* left */
//           return true;
//     
//         /* No intersections, is any point inside? */
//         if ((evaluateCrossings(x, y, false, BIG_VALUE) & 1) != 0)
//           return true;
//     
//         return false;
//       }


    /**
     * Tests whether or not the specified point is inside this polygon.
     *
     * @param x the X coordinate of the point to test
     * @param y the Y coordinate of the point to test
     * @return true if the point is inside this polygon
     * @since 1.2
     */

    public boolean contains(int x, int y) {
//	      return (((this.evaluateCrossings(x, y, false, BIG_VALUE))&1) != 0);
        int y1, x1, y0, x0;
        float y1f, y0f, x1f, x0f;
        int nb_crossing = 0;
        int last = this.size() - 1;
        x0 = this.get(last).getX() - x;
        y0 = this.get(last).getY() - y;
        for (int i = 0; i <= last; i++) {
            x1 = this.get(i).getX() - x;
            y1 = this.get(i).getY() - y;
            if (((y0 == 0) && (x0 >= 0)) || ((y1 == 0) && (x1 >= 0))) {
                nb_crossing++;
            } else if ((y0 != y1) && ((y0 * y1) < 0)) {
                x0f = (float) x0;
                x1f = (float) x1;
                y0f = (float) y0;
                y1f = (float) y1;
                if ((x0f - y0f * (x1f - x0f) / (y1f - y0f)) >= 0) {
                    nb_crossing++;
                }
            }
            x0 = x1;
            y0 = y1;
        }
        return ((nb_crossing % 2) == 1);
    }

//       /**
//        * Test if a high-precision rectangle lies completely in the shape. This is
//        * true if all points in the rectangle are in the shape. This implementation
//        * is precise.
//        *
//        * @param x the x coordinate of the rectangle
//        * @param y the y coordinate of the rectangle
//        * @param w the width of the rectangle, treated as point if negative
//        * @param h the height of the rectangle, treated as point if negative
//        * @return true if the rectangle is contained in this shape
//        * @since 1.2
//        */	 
//	 public boolean contains(int x, int y, int w, int h)
//	    {
//	      if (! getBounds2D().intersects(x, y, w, h))
//	        return false;
//	  
//	      /* Does any edge intersect? */
//	      if (evaluateCrossings(x, y, false, w) != 0 /* top */
//	          || evaluateCrossings(x, y + h, false, w) != 0 /* bottom */
//	          || evaluateCrossings(x + w, y, true, h) != 0 /* right */
//	          || evaluateCrossings(x, y, true, h) != 0) /* left */
//	        return false;
//	  
//	      /* No intersections, is any point inside? */
//	      if ((evaluateCrossings(x, y, false, BIG_VALUE) & 1) != 0)
//	        return true;
//	  
//	      return false;
//	    }

    /**
     * Helper for contains, intersects, calculates the number of intersections
     * between the polygon and a line extending from the point (x, y) along
     * the positive X, or Y axis, within a given interval.
     *
     * @return the winding number.
     * @see #contains(double, double)
     */
//	private int evaluateCrossings(int x, int y, boolean useYaxis, int distance) {
//        int x0;
//        int x1;
//        int y0;
//        int y1;
//        int epsilon = 0;
//        int crossings = 0;
//        int xp;
//        int yp;
//        /* Get a value which is small but not insignificant relative the path. */
//        epsilon = 10;
//        int last = this.size()-1;
//        if (useYaxis) {
//        	xp=this.get(last).getY();
//        	yp=this.get(last).getX();
//        } else {
//        	xp=this.get(last).getX();
//        	yp=this.get(last).getY();
//        }
//        x0 = xp - x;
//        y0 = yp - y;
//        for (int i = 0; i < this.size(); i++) {
//        	if (useYaxis) {
//        		xp=this.get(i).getY();
//        		yp=this.get(i).getX();
//        	} else {
//        		xp=this.get(i).getX();
//        		yp=this.get(i).getY();
//        	}
//        	x1 = xp - x;
//        	y1 = yp - y;
////        	if (y0 == 0)
////        		y0 -= epsilon;
////        	if (y1 == 0)
////        		y1 -= epsilon;
//        	if (y0 * y1 < 0)
//        		if (MyPolygon.linesIntersect(x0, y0, x1, y1, epsilon, 0, distance, 0))
//        			++crossings;
//        	x0 = x1;
//        	y0 = y1;
//        }
//        return crossings;
//    }
    /**
     * Returns <code>true</code> if (x3, y3) lies between (x1, y1) and (x2, y2),
     * and false otherwise,  This test assumes that the three points are
     * collinear, and is used for intersection testing.
     *
     * @param x1  the x-coordinate of the first point.
     * @param y1  the y-coordinate of the first point.
     * @param x2  the x-coordinate of the second point.
     * @param y2  the y-coordinate of the second point.
     * @param x3  the x-coordinate of the third point.
     * @param y3  the y-coordinate of the third point.
     *
     * @return A boolean.
     */
//       private static boolean between(int x1, int y1, int x2, int y2, int x3, int y3) {
//    	   if (x1 != x2) {
//    		   return ((x1 <= x3) && (x3 <= x2)) || ((x1 >= x3) && (x3 >= x2));
//    	   } else {
//    		   return ((y1 <= y3) && (y3 <= y2)) || ((y1 >= y3) && (y3 >= y2));
//    	   }
//       }

    /**
     * Computes twice the (signed) area of the triangle defined by the three
     * points.  This method is used for intersection testing.
     *
     * @param x1  the x-coordinate of the first point.
     * @param y1  the y-coordinate of the first point.
     * @param x2  the x-coordinate of the second point.
     * @param y2  the y-coordinate of the second point.
     * @param x3  the x-coordinate of the third point.
     * @param y3  the y-coordinate of the third point.
     *
     * @return Twice the area.
     */
    //   private static int area2(int x1, int y1, int x2, int y2, int x3, int y3) {
    //	   return (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
    //   }

    /**
     * Test if the line segment (x1,y1)-&gt;(x2,y2) intersects the line segment
     * (x3,y3)-&gt;(x4,y4).
     *
     * @param x1 the first x coordinate of the first segment
     * @param y1 the first y coordinate of the first segment
     * @param x2 the second x coordinate of the first segment
     * @param y2 the second y coordinate of the first segment
     * @param x3 the first x coordinate of the second segment
     * @param y3 the first y coordinate of the second segment
     * @param x4 the second x coordinate of the second segment
     * @param y4 the second y coordinate of the second segment
     * @return true if the segments intersect
     */
//       public static boolean linesIntersect(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
//    	   int a1, a2, a3, a4;
//
//    	   // deal with special cases
//    	   if ((a1 = area2(x1, y1, x2, y2, x3, y3)) == 0) {
//    		   // check if p3 is between p1 and p2 OR
//    		   // p4 is collinear also AND either between p1 and p2 OR at opposite ends
//    		   if (between(x1, y1, x2, y2, x3, y3)) {
//    			   return true;
//    		   } else {
//    			   if (area2(x1, y1, x2, y2, x4, y4) == 0) {
//    				   return between(x3, y3, x4, y4, x1, y1) || between (x3, y3, x4, y4, x2, y2);
//    			   } else {
//    				   return false;
//    			   }
//    		   }
//    	   } else if ((a2 = area2(x1, y1, x2, y2, x4, y4)) == 0) {
//    		   // check if p4 is between p1 and p2 (we already know p3 is not
//    		   // collinear)
//    		   return between(x1, y1, x2, y2, x4, y4);
//    	   }
//
//    	   if ((a3 = area2(x3, y3, x4, y4, x1, y1)) == 0) {
//    		   // check if p1 is between p3 and p4 OR
//    		   // p2 is collinear also AND either between p1 and p2 OR at opposite ends
//    		   if (between(x3, y3, x4, y4, x1, y1)) {
//    			   return true;
//    		   } else {
//    			   if (area2(x3, y3, x4, y4, x2, y2) == 0) {
//        			 	return between(x1, y1, x2, y2, x3, y3) || between (x1, y1, x2, y2, x4, y4);
//    			   } else {
//    				   return false;
//    			   }
//    		   }
//    	   } else if ((a4 = area2(x3, y3, x4, y4, x2, y2)) == 0) {
//    		   // check if p2 is between p3 and p4 (we already know p1 is not
//    		   // collinear)
//    		   return between(x3, y3, x4, y4, x2, y2);
//    	   } else {  // test for regular intersection
//    		   return ((a1 > 0) ^ (a2 > 0)) && ((a3 > 0) ^ (a4 > 0));
//    	   }
//       }

//       public static boolean linesIntersect(MyGeoPoint pA, MyGeoPoint pB, MyGeoPoint pC, MyGeoPoint pD) {
//    	   return MyPolygon.linesIntersect(pA.getX(),pA.getY(),pB.getX(),pB.getY(),pC.getX(),pC.getY(),pD.getX(),pD.getY());
//       }
    public MyGeoPoint pointDistanceMin(MyGeoPoint pM) {
        MyGeoPoint pA, pB, pTemp;
        float dAM, dBM, dAB, dTemp;
        pA = this.get(0);
        pB = this.get(1);
        dAM = pA.distanceTo(pM);
        dBM = pB.distanceTo(pM);
        if (dAM > dBM) {
            dTemp = dBM;
            dBM = dAM;
            dAM = dTemp;
            pTemp = pB;
            pB = pA;
            pA = pTemp;
        }
        for (int i = 2; i < this.size(); i++) {
            pTemp = this.get(i);
            dTemp = pTemp.distanceTo(pM);
            if (dTemp < dAM) {
                dBM = dAM;
                pB = pA;
                dAM = dTemp;
                pA = pTemp;
            } else if (dTemp < dBM) {
                dBM = dTemp;
                pB = pTemp;
            }
        }
        dAB = pA.distanceTo(pB);
        if ((dBM * dBM) > (dAB * dAB + dAM * dAM)) {
            pTemp = pA;
        } else {
            Vector vector1 = new Vector(pA, pB);
            Vector vector2 = new Vector(pA, pM);
            int d = vector1.prodVector(vector2);
            if (vector1.norm()==0)
                pTemp = pA;
            else
                pTemp = vector1.prod(d).div(vector1.norm()).translate(pA);
        }
        return pTemp;
    }

} // class Polygon
