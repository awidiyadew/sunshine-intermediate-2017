package cf.awidiyadew.sunshine_intermediate_class.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.awidiyadew.sunshine_intermediate_class.R;
import cf.awidiyadew.sunshine_intermediate_class.model.DummyForecast;
import cf.awidiyadew.sunshine_intermediate_class.model.ListForecast;
import cf.awidiyadew.sunshine_intermediate_class.model.WeatherItem;

/**
 * Created by awidiyadew on 5/7/17.
 */

public class ForecastItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_weatherIcon) ImageView ivWeatherIcon;
    @BindView(R.id.tv_day) TextView tvDay;
    @BindView(R.id.tv_forecast) TextView tvForecast;
    @BindView(R.id.tv_minTemp) TextView tvMinTemp;
    @BindView(R.id.tv_maxTemp) TextView tvMaxTemp;

    public ForecastItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(DummyForecast data){
        tvDay.setText(data.getDay());
        tvForecast.setText(data.getForecast());
        tvMinTemp.setText(data.getMixTempWithDerajat()); // konversi int ke string
        tvMaxTemp.setText(data.getMaxTempWithDerajat()); // konversi int ke string
    }

    public void bind(ListForecast data, int position){

        if (position == 0){
            tvDay.setText(data.getTodayReadableTime());
        } else {
            tvDay.setText(data.getReadableTime(position));
        }

        WeatherItem weather = data.getWeather().get(0);

        tvForecast.setText(weather.getDescription());
        tvMinTemp.setText(data.getTemp().getDerajatMinTemp());
        tvMaxTemp.setText(data.getTemp().getDerajatMaxTemp());

    }

}
