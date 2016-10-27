package com.sdesimeur.android.gpsfiction.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sdesimeur.android.gpsfiction.R;

import java.util.ArrayList;


public class InventoryFragment extends MyTabFragment {
    protected ListView listTitles = null;

    public InventoryFragment() {
        super();
        //	this.setNameId(R.string.tabInventoryTitle);
    }

    protected ArrayList<String> createMainList() {
        int resId;
        String resString;
        ArrayList<String> elements = new ArrayList<String>();
        //String[] tabOfTitles=getResources().getStringArray(R.array.names_classes);
        int[] tabOfResClasses = {
                R.string.titleZones,
                R.string.titleItems,
                R.string.titleCharacters,
                R.string.titleInventory,
                R.string.titleTasks
        };
        for (int i = 0; i < tabOfResClasses.length; i++) {
            resId = tabOfResClasses[i];
            resString = getResources().getString(resId);
            elements.add(resString);
        }
        return elements;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.setRootView(inflater.inflate(R.layout.inventory_view, container, false));
        listTitles = (ListView) this.getRootView().findViewById(R.id.listInventory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getmGpsFictionActivity(), android.R.layout.simple_list_item_1, createMainList());
        listTitles.setAdapter(adapter);
        return this.getRootView();
    }

/*
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_inventory);
        listTitles = (ListView) findViewById(R.id.listInventory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, createMainList());
        listTitles.setAdapter(adapter);
        
        }
*/

}
