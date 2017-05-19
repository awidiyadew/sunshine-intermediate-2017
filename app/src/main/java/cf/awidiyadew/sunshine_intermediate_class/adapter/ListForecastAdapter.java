package cf.awidiyadew.sunshine_intermediate_class.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cf.awidiyadew.sunshine_intermediate_class.R;
import cf.awidiyadew.sunshine_intermediate_class.model.ListForecast;

/**
 * Created by awidiyadew on 5/7/17.
 */

public class ListForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ListForecastAdapter.class.getSimpleName();

    private List<ListForecast> listData = new ArrayList<>();
    private static final int TODAY_VIEW = 0;
    private static final int NEXT_DAY_VIEW = 1;

    // TODO: 5/19/17 B. Setup click listener : step 2
    private ForecastItemClickListener mForecastClickCallback;

    public ListForecastAdapter(List<ListForecast> listData) {
        this.listData = listData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO: 5/19/17 A. Setup today vholder : step 2
        // Jika view typenya adalah TODAY_VIEW maka return ViewHolder TodayForecastItemViewHolder dengan layout row_today...
        if (viewType == TODAY_VIEW){
            View todayItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_today_forecast_item, parent, false);
            return new TodayForecastItemViewHolder(todayItemView);
        } else {
            // Jika view typenya bukan TODAY_VIEW maka return ViewHolder biasa yaitu ForecastItemViewHolder dengan layout row_forecast_item...
            View nextDayItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_forecast_item, parent, false);
            return new ForecastItemViewHolder(nextDayItemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // TODO: 5/19/17 A. Setup today vholder : step 3

        final int viewType = getItemViewType(position);
        final ListForecast data = listData.get(position);

        if (viewType == TODAY_VIEW){
            TodayForecastItemViewHolder todayForecastItemViewHolder = (TodayForecastItemViewHolder) holder; // casting view holder menjadi TodayForecastItemViewHolder
            todayForecastItemViewHolder.bind(data, position);
        } else {
            ForecastItemViewHolder forecastItemViewHolder = (ForecastItemViewHolder) holder; // casting view holder menjadi ForecastItemViewHolder
            forecastItemViewHolder.bind(data, position);
        }

        // TODO: 5/19/17 B. Setup click listener : step 3
        // holder.itemView adalah item view pada list
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mForecastClickCallback != null)
                    mForecastClickCallback.onForecastItemClick(data);
                else
                    Log.e(TAG, "onClick: Forecase click listener is null, " +
                            "don't forget to call adapter.setForecastItemClickListener(this)");
            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public int getItemViewType(int position) {
        // TODO: 5/19/17 A. Setup today vholder : step 1
        // posisi 0 (item pertama), jika posisi nol maka set view type menjadi TODAY_VIEW
        if (position == 0)
            return TODAY_VIEW;
        else
            return NEXT_DAY_VIEW;
    }

    public void setForecastItemClickListener(ForecastItemClickListener clickListener){
        if (clickListener != null)
            mForecastClickCallback = clickListener;
    }


    // TODO: 5/19/17 B. Setup click listener : step 1
    // Inner interface class utk click listener pada recyclerview
    public interface ForecastItemClickListener{
        void onForecastItemClick(ListForecast data);
    }
}
