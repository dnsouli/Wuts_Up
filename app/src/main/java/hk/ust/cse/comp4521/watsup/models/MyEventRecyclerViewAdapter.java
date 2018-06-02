package hk.ust.cse.comp4521.watsup.models;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hk.ust.cse.comp4521.watsup.EventDetailActivity;
import hk.ust.cse.comp4521.watsup.EventDetailFragment;
import hk.ust.cse.comp4521.watsup.EventListActivity;
import hk.ust.cse.comp4521.watsup.R;

//# COMP 4521    #  YOUR FULL NAME        STUDENT ID          EMAIL ADDRESS
//         1.       Ivan Bardarov         20501426            iebardarov@connect.ust.hk
//         2.       Danny Nsouli          20531407            dmansouli@connect.ust.hk




public class MyEventRecyclerViewAdapter
        extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {

        private final EventListActivity mParentActivity;
        private final List<Event> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event item = (Event) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(EventDetailFragment.ARG_ITEM_ID, EventListActivity.eventsToBeShown.indexOf(item));
                    EventDetailFragment fragment = new EventDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.event_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra(EventDetailFragment.ARG_ITEM_ID, EventListActivity.eventsToBeShown.indexOf(item));

                    context.startActivity(intent);
                }
            }
        };

    public MyEventRecyclerViewAdapter(EventListActivity parent,
                                      List<Event> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if(mValues.get(position) != null) {
                holder.mIdView.setText(mValues.get(position).getName());
                holder.mContentView.setText(mValues.get(position).getType());

                holder.itemView.setTag(mValues.get(position));
                holder.itemView.setOnClickListener(mOnClickListener);
            }
        }

        @Override
        public int getItemCount() {

            return mValues == null ? 0 : mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
}
