package com.sdesimeur.android.gpsfiction.forall.player;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.activities.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link GameItem} and makes a call to the
 * specified {@link GameFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyGameRecyclerViewAdapter extends RecyclerView.Adapter<MyGameRecyclerViewAdapter.ViewHolder> {

    private final List<GameFragment.GameItem> mValues;
    private final GameFragment.OnListFragmentInteractionListener mListener;

    public MyGameRecyclerViewAdapter(List<GameFragment.GameItem> items, GameFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mGameNameView.setText(mValues.get(position).name);
        holder.mGameDescriptionView.setText(mValues.get(position).desc);

        holder.mGameNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final Button mGameNameView;
        public final TextView mGameDescriptionView;
        public GameFragment.GameItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mGameNameView = (Button) view.findViewById(R.id.gameNameView);
            mGameDescriptionView = (TextView) view.findViewById(R.id.gameDescriptionView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mGameNameView.getText() + "'";
        }
    }
}
