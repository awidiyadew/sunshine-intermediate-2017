package cf.awidiyadew.sunshine_intermediate_class;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.awidiyadew.sunshine_intermediate_class.model.ListForecast;
import cf.awidiyadew.sunshine_intermediate_class.util.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_date) TextView tvDate;
    @BindView(R.id.detail_weather_icon) ImageView ivWeatherIcon;
    @BindView(R.id.detail_weather_description) TextView tvWeatherDesc;
    @BindView(R.id.detail_high_temperature) TextView tvHighTemp;
    @BindView(R.id.detail_low_temperature) TextView tvLowTemp;
    @BindView(R.id.detail_humidity) TextView tvHumidity;
    @BindView(R.id.detail_pressure) TextView tvPressure;
    @BindView(R.id.detail_wind) TextView tvWind;

    private ListForecast forecastData;
    private int dataPosition;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        String extraJsonData = getIntent().getStringExtra("data"); // mengambil data dari intent
        if (extraJsonData != null) {
            forecastData = gson.fromJson(extraJsonData, ListForecast.class);
            dataPosition = getIntent().getIntExtra("position", 0); // mengambil data position dr intent
            bindData();
        }
    }

    private void bindData() {
        if (dataPosition == 0) {
            //today
            tvDate.setText(forecastData.getTodayReadableTime());
        } else {
            tvDate.setText(forecastData.getReadableTime(dataPosition));
        }

        ivWeatherIcon.setImageResource(
                SunshineWeatherUtils
                        .getSmallArtResourceIdForWeatherCondition(
                                forecastData.getWeather().get(0).getId()
                        )
        );

        tvHighTemp.setText(forecastData.getTemp().getDerajatMaxTemp());
        tvLowTemp.setText(forecastData.getTemp().getDerajatMinTemp());

        tvWeatherDesc.setText(forecastData.getWeather().get(0).getDescription());
        tvHumidity.setText(forecastData.getReadableHumidity());
        tvWind.setText(forecastData.getReadableWindSpeed());
        tvPressure.setText(forecastData.getReadablePressure());

    }
}
