package com.sdesimeur.android.gpsfiction.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;

import java.util.ArrayList;
import java.util.Iterator;


public class MyDialogFragment extends DialogFragment {
    private GpsFictionActivity gpsFictionActivity = null;
    private ArrayList<Integer> buttonsListIds = new ArrayList<Integer>();
    private int titleId = 0;
    private int textId = 0;
    private LinearLayout buttonsLinearLayout = null;
    private TextView textView = null;
    private View dialogView = null;

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

    public void init(GpsFictionActivity gpsFictionActivity, int titleId, int textId) {
        this.gpsFictionActivity = gpsFictionActivity;
        this.titleId = titleId;
        this.textId = textId;
        this.buttonsListIds.clear();
        this.gpsFictionActivity.dialogFragments.add(this);
    }

    //public void setTexts (int textId) {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setCancelable(false);
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
            //this.buttonsLinearLayout.addView(button, layoutParams);
        }
        return this.dialogView;
    }

    public void show(FragmentManager fragmentManager) {
        String tag = Integer.toHexString(this.titleId);
        super.show(fragmentManager, tag);
    }

    private void returnButton(int buttonId) {
        this.gpsFictionActivity.getReponseFromMyDialogFragment(titleId, (Integer) (buttonId));
        this.dismissAllowingStateLoss();
    }

    @Override
    public void onDetach() {
        this.gpsFictionActivity.dialogFragments.remove(this);
        super.onDetach();
    }

}
