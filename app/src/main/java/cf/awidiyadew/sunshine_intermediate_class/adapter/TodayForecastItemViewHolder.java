package cf.awidiyadew.sunshine_intermediate_class.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.awidiyadew.sunshine_intermediate_class.R;
import cf.awidiyadew.sunshine_intermediate_class.model.ListForecast;
import cf.awidiyadew.sunshine_intermediate_class.model.WeatherItem;
import cf.awidiyadew.sunshine_intermediate_class.util.SunshineWeatherUtils;

/**
 * Created by awidiyadew on 5/19/17.
 */

public class TodayForecastItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_today_date) TextView tvTodayDate;
    @BindView(R.id.tv_today_weather_description) TextView tvTodayWeatherDesc;
    @BindView(R.id.tv_today_high_temperature) TextView tvTodayHighTemp;
    @BindView(R.id.tv_today_low_temperature) TextView tvTodayLowTemp;
    @BindView(R.id.iv_today_weather_icon) ImageView ivTodayIcon;

    public TodayForecastItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(ListForecast data, int position){
        if (position == 0){
            tvTodayDate.setText(data.getTodayReadableTime());
        } else {
            tvTodayDate.setText(data.getReadableTime(position));
        }

        ivTodayIcon.setImageResource(
                SunshineWeatherUtils
                        .getSmallArtResourceIdForWeatherCondition(
                                data.getWeather().get(0).getId()
                        )
        );

        WeatherItem weather = data.getWeather().get(0);

        tvTodayWeatherDesc.setText(weather.getDescription());
        tvTodayLowTemp.setText(data.getTemp().getDerajatMinTemp());
        tvTodayHighTemp.setText(data.getTemp().getDerajatMaxTemp());

    }
}
