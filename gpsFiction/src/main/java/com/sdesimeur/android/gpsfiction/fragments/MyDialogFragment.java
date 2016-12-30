package com.sdesimeur.android.gpsfiction.fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.activities.R;

import java.util.ArrayList;
import java.util.Iterator;


public class MyDialogFragment extends DialogFragment {
    private ArrayList<Integer> buttonsListIds = new ArrayList<Integer>();
    private int titleId = 0;
    private int textId = 0;
    private LinearLayout buttonsLinearLayout = null;
    private TextView textView = null;
    private View dialogView = null;
    private boolean toSave = false;
    public MyDialogFragment() {
        // TODO Auto-generated constructor stub
        super();
    }

    public ArrayList<Integer> getButtonsListIds() {
        return buttonsListIds;
    }

    public void setButtonsListIds(ArrayList<Integer> buttonsListIds) {
        this.buttonsListIds = buttonsListIds;
    }
    public GpsFictionActivity getmGpsFictionActivity () {
        return (GpsFictionActivity) getActivity();
    }
    public void init( int titleId, int textId) {
        toSave = true;
        this.titleId = titleId;
        this.textId = textId;
        this.buttonsListIds.clear();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (toSave) {
            savedInstanceState.putInt("savedState",1);
            savedInstanceState.putInt("titleId", titleId);
            savedInstanceState.putInt("textId", textId);
            savedInstanceState.putInt("nbButtons",buttonsListIds.size());
            int index=0;
            final Iterator<Integer> it = this.buttonsListIds.iterator();
            while (it.hasNext()) {
                final int buttonTextId = it.next();
                savedInstanceState.putInt("buttonTextId"+index, buttonTextId);
                index++;
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getmGpsFictionActivity().dialogFragments.add(this);
        this.setCancelable(false);
        if (savedInstanceState != null)
        if (savedInstanceState.getInt("savedState",0)==1) {
            titleId = savedInstanceState.getInt("titleId");
            textId = savedInstanceState.getInt("textId");
            int indexMax = savedInstanceState.getInt("nbButtons");
            for (int index=0;index<indexMax;index++) {
                buttonsListIds.add(savedInstanceState.getInt("buttonTextId"+index));
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.titleId == 0) {
            this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } else {
            String title = this.getResources().getString(this.titleId);
            this.getDialog().getWindow().setTitle(title);
        }
        this.dialogView = inflater.inflate(R.layout.dialog_view, container, true);
        this.textView = (TextView) (this.dialogView.findViewById(R.id.mydialog_text));
        this.buttonsLinearLayout = (LinearLayout) this.dialogView.findViewById(R.id.mydialog_buttons);
        this.textView.setText(this.textId);
//		LayoutInflater inflater = (LayoutInflater)((Context)(this.gpsFictionActivity)).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Iterator<Integer> it = this.buttonsListIds.iterator();
        while (it.hasNext()) {
            final int buttonTextId = it.next();
            Button button = (Button) inflater.inflate(R.layout.dialog_buttons, this.buttonsLinearLayout, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(layoutParams);
            button.setText(buttonTextId);
            button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    returnButton(buttonTextId);
                }
            });
            this.buttonsLinearLayout.addView(button);
        }
        return this.dialogView;
    }

    public void show(FragmentManager fragmentManager) {
        String tag = Integer.toHexString(this.titleId);
        super.show(fragmentManager, tag);
    }

    private void returnButton(int buttonId) {
        toSave = false;
        getmGpsFictionActivity().getReponseFromMyDialogFragment(titleId, (Integer) (buttonId));
        this.dismiss();
        //this.dismissAllowingStateLoss();
    }

    @Override
    public void onDetach() {
        getmGpsFictionActivity().dialogFragments.remove(this);
        super.onDetach();
    }

}
