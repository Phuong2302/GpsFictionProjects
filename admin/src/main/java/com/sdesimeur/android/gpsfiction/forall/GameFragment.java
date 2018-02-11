package com.sdesimeur.android.gpsfiction.forall;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdesimeur.android.gpsfiction.intent.GpsFictionIntent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GameFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GameFragment newInstance(int columnCount) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            final List <GameItem> contentList = new ArrayList<>();
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String localeString = settings.getString(GpsFictionIntent.LOCALE, GpsFictionIntent.DEFAULTPLAYERLOCALE);
            Locale locale = new Locale(localeString);
            final Configuration cfg = new Configuration();
            cfg.setLocale(locale);
            GamesActivity.AllGpsFictionActivityHelper temp = ((GamesActivity)getActivity()).new AllGpsFictionActivityHelper() {
                @Override
                public void action(ActivityInfo ai) {
                    GameFragment.GameItem gi = new GameFragment.GameItem();
                    try {
                        //Context thisapp = getActivity().getApplicationContext();
                        //Context newapp = thisapp.createPackageContext(ai.packageName,thisapp.CONTEXT_IGNORE_SECURITY);
                        //newapp.getResources().updateConfiguration(cfg,getActivity().getResources().getDisplayMetrics());
                        Resources res = pm.getResourcesForApplication(ai.applicationInfo);
                        res.updateConfiguration(cfg,getActivity().getResources().getDisplayMetrics());
                        //gi.name = ai.applicationInfo.loadLabel(pm).toString();
                        gi.name = res.getString(ai.applicationInfo.labelRes);
                        //gi.desc = ai.applicationInfo.loadDescription(pm).toString();
                        gi.desc = res.getString(ai.applicationInfo.descriptionRes);
                        gi.activityInfo = ai;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    //gi.name = (String) pm.getApplicationLabel(ai.applicationInfo);
                    contentList.add(gi);
                }
            };
            temp.doForAllGpsFictionActivity();
            mListener = (OnListFragmentInteractionListener) getActivity();
            recyclerView.setAdapter(new MyGameRecyclerViewAdapter(contentList, mListener));
        }
        return view;
    }
    public class GameItem {
        public String name;
        public String desc;
        public ActivityInfo activityInfo;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(GameItem item);
    }
}