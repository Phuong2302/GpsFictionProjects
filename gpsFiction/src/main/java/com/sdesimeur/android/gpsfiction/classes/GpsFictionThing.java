package com.sdesimeur.android.gpsfiction.classes;

import android.content.res.Resources;
import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;

public class GpsFictionThing {
    private GpsFictionActivity gpsFictionActivity = null;
    private int[] valint = new int[]{
            0,
            0,
            -1,
            0,
            R.drawable.flag_green
    };
    //private int id=0;
    //private int definitionId=0;
    //private int replacementNameInLongId=0;
    //private int numInTable4LongId=-1;
    //private int iconId = R.drawable.flag_green;
    //private boolean replacementIsATableInLongId=false;
    private boolean[] valbool = {
            true,
            true
    };

    public GpsFictionThing() {
    }

    public Bundle getByBundle() {
        Bundle dest = new Bundle();
        dest.putBooleanArray("ValBool", this.valbool);
        dest.putIntArray("ValInt", this.valint);
        return dest;
    }
    //private boolean visible=true; // Parcel
    //private boolean active=true; // Parcel

    public void setByBundle(Bundle in) {
        this.valbool = in.getBooleanArray("ValBool");
        this.valint = in.getIntArray("ValInt");
    }

    public void init(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
        //GpsFictionThing.activity.getResources().getClass().getDeclaredFields()
        this.getGpsFictionActivity().getGpsFictionData().addGpsFictionThing(this);
    }
    //protected int getLongId() {
    //	return this.longId;
    //}
    //protected void setLongId(int longId) {
    //	this.longId = longId;
    //}
    //protected int getReplacementNameInLongId() {
    //	return this.replacementNameInLongId;
    //}
    //protected void setReplacementNameInLongId(int replacementNameInLongId) {
    //	this.replacementNameInLongId = replacementNameInLongId;
    //}
    //protected boolean isReplacementIsATableInLongId() {
    //	return this.replacementIsATableInLongId;
    //}
    //protected void setReplacementIsATableInLongId(boolean replacementIsATableInLongId) {
    //	this.replacementIsATableInLongId = replacementIsATableInLongId;
    //}
    //protected int getNumInTable4LongId() {
    //	return numInTable4LongId;
    //}
    //protected void setNumInTable4LongId(int numInTable4LongId) {
    //	this.numInTable4LongId = numInTable4LongId;
    //}


//	public static void setDisplayNameIdMax() {
//		int max=0;
//		Field [] fields = R.string.class.getDeclaredFields();
//		for (int i=0, m = fields.length ; i<m ; i++) {
//			try {
//				max=Math.max(max,fields[i].getInt(null));
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		GpsFictionThing.displayNameIdMax = max+1;
//	}

    public void setAttrs(boolean visible, boolean active) {
        this.valbool[VALBOOL.visible] = visible;
        this.valbool[VALBOOL.active] = active;
    }

    public int[] getUUID() {
        int[] ret = new int[3];
        ret[0] = this.getId();
        //ret[1] = this.longId;
        ret[1] = this.getReplacementNameInLongId();
        ret[2] = this.getNumInTable4LongId();
        return ret;

    }

    public boolean compareUUID(int[] uuidToCompare) {
        boolean egal = true;
        for (int i = 0; i < this.getUUID().length; i++)
            egal = (egal && ((this.getUUID()[i]) == (uuidToCompare[i])));
        return egal;
    }

    public void setId(int longId, int replacementNameInLongId) {
        this.valint[VALINT.id] = longId;
        //this.longId = longId;
        this.valint[VALINT.replacementNameInLongId] = replacementNameInLongId;
        this.valint[VALINT.numInTable4LongId] = -1;
    }

    public void setId(int longId, int replacementNameInLongId, int numInTable4LongId) {
        this.setId(longId, replacementNameInLongId);
        //this.replacementIsATableInLongId=true;
        this.valint[VALINT.numInTable4LongId] = numInTable4LongId;
    }

    /**
     * @return the definition
     */
    public int getDefinitionId() {
        return this.valint[VALINT.definitionId];
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinitionId(int definitionId) {
        this.valint[VALINT.definitionId] = definitionId;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return this.valbool[VALBOOL.visible];
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.valbool[VALBOOL.visible] = visible;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return this.valbool[VALBOOL.active];
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.valbool[VALBOOL.active] = active;
    }

    /**
     * @return the name
     */
    public int getNameId() {
        return this.valint[VALINT.id];
    }

    public int getId() {
        return this.valint[VALINT.id];
    }

    public void setId(int id) {
        this.valint[VALINT.id] = id;
    }

    public int getReplacementNameInLongId() {
        return this.valint[VALINT.replacementNameInLongId];
    }

    public int getNumInTable4LongId() {
        return this.valint[VALINT.numInTable4LongId];
    }

    public String getName() {
        String name;
        if (this.getReplacementNameInLongId() == 0) {
            name = this.getResources().getString(this.getId());
        } else {
            if (this.getNumInTable4LongId() == -1) {
                name = this.getResources().getString(this.getReplacementNameInLongId());
            } else {
                name = this.getResources().getStringArray(this.getReplacementNameInLongId())[this.getNumInTable4LongId()];
            }
            name = this.getResources().getString(this.getId()).replaceFirst("%%%%", name);
        }
        return name;
    }

    public Resources getResources() {
        return this.getGpsFictionActivity().getResources();
    }

    public GpsFictionActivity getGpsFictionActivity() {
        return this.gpsFictionActivity;
    }

    public void setGpsFictionActivity(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
    }

    public int getIconId() {
        return this.valint[VALINT.iconId];
    }

    public void setIconId(int iconId) {
        this.valint[VALINT.iconId] = iconId;
    }

    public static class VALINT {
        static int id = 0;
        static int replacementNameInLongId = 1;
        static int numInTable4LongId = 2;
        static int definitionId = 3;
        static int iconId = 4;
    }

    public static class VALBOOL {
        static int visible = 0;
        static int active = 1;
    }
}
