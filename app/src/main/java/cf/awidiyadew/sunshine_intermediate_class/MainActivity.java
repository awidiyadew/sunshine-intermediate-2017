package cf.awidiyadew.sunshine_intermediate_class;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.awidiyadew.sunshine_intermediate_class.adapter.ListForecastAdapter;
import cf.awidiyadew.sunshine_intermediate_class.database.ForecastDBHelper;
import cf.awidiyadew.sunshine_intermediate_class.model.ApiResponse;
import cf.awidiyadew.sunshine_intermediate_class.model.DummyForecast;
import cf.awidiyadew.sunshine_intermediate_class.model.ListForecast;

public class MainActivity extends AppCompatActivity implements ListForecastAdapter.ForecastItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.pb_forecast) ProgressBar pb;
    @BindView(R.id.tv_error) TextView errorView;
    ListForecastAdapter adapter;
    private List<DummyForecast> listDataDummy = new ArrayList<>();
    private List<ListForecast> listWeather = new ArrayList<>();
    private Gson gson = new Gson();
    private ForecastDBHelper dbHelper;

    private String cityTarget;
    private String units;
    private SharedPreferences sharedPreferences;
    private boolean isNeedRefresh = false;
    private ApiResponse apiResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setElevation(0); // menghilangkan shadow pada action bar

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityTarget = sharedPreferences.getString(
                this.getString(R.string.pref_location_key),
                this.getString(R.string.pref_location_default)
        );
        units = sharedPreferences.getString(
                this.getString(R.string.pref_units_key),
                this.getString(R.string.pref_units_metric)
        );

        setupRecyclerview();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }

        Log.d(TAG, "sp ->" + sharedPreferences.getString(
                this.getString(R.string.pref_location_key),
                this.getString(R.string.pref_location_default)
        ));
        Log.d(TAG, "citytarget-> " + cityTarget);
        if (preferencesChecker()) {
            getDataFromApi();
        }
    }

    private boolean preferencesChecker() {
        if (!(sharedPreferences.getString(
                this.getString(R.string.pref_location_key),
                this.getString(R.string.pref_location_default)
        ).equals(cityTarget))) {
            cityTarget = sharedPreferences.getString(
                    this.getString(R.string.pref_location_key),
                    this.getString(R.string.pref_location_default)
            );
            Log.d(TAG, "cityTarget -> " + cityTarget);

            //getData();
            isNeedRefresh = true;
        }

        if (!(sharedPreferences.getString(
                this.getString(R.string.pref_units_key),
                this.getString(R.string.pref_units_metric)
        ).equals(units))) {
            units = sharedPreferences.getString(
                    this.getString(R.string.pref_units_key),
                    this.getString(R.string.pref_units_metric)
            );

            //getData();
            isNeedRefresh = true;
        }
        return isNeedRefresh;
    }

    private void setupRecyclerview(){
        //adapter = new ListForecastAdapter(listDataDummy);
        adapter = new ListForecastAdapter(listWeather);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        //getDummyData();

        dbHelper = new ForecastDBHelper(this);
        getDataFromApi();
    }

    public void getDummyData(){
        for (int i = 0 ; i < 20 ; i++){
            DummyForecast dummyForecast = new DummyForecast("Sunday", "Sunny", 20 + i, 25 + i);
            listDataDummy.add(dummyForecast);
        }

        adapter.notifyDataSetChanged();
    }

    public void getDataFromApi(){

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final String url = "http://api.openweathermap.org/data/2.5/forecast/daily?cnt=16&appid=3e40a7f5fd7287ce34741d4c5f82779a&units=" + units + "&q=" + cityTarget;

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);

                apiResponse = gson.fromJson(response, ApiResponse.class);

                for (ListForecast listItem : apiResponse.getList()){
                    listWeather.add(listItem);
                }

                adapter.notifyDataSetChanged();

                // TODO: 5/19/17 B. Setup click listener : step 4
                adapter.setForecastItemClickListener(MainActivity.this);
                saveForecastToDB(apiResponse);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dbHelper.isDataAlreadyExist(cityTarget)) {
                    //data denpasar is exist on sqlite, show it
                    apiResponse = dbHelper.getSavedForecast(cityTarget);
                    showDataFromDB(apiResponse);
                }else{
                    //data denpasar is not available on sqlite
                    updateView("error");
                    if (error != null) {
                        Log.e(TAG, error.getMessage());
                    } else {
                        Log.e(TAG, "Something wrong happened");
                    }
                }
            }
        };

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                responseListener,
                errorListener
        );

        requestQueue.add(request);

    }

    private void saveForecastToDB(ApiResponse data) {
        if (!dbHelper.isDataAlreadyExist(cityTarget)) {
            //data forecast not available on db, insert new
            for (ListForecast item : data.getList()) {
                dbHelper.saveForecast(data.getCity(), item);
            }
        } else {
            //data forecast already exist on db, update it with brand new data
            dbHelper.deleteForUpdate(cityTarget);
            for (ListForecast item : data.getList()) {
                dbHelper.saveForecast(data.getCity(), item);
            }
        }
    }

    private void updateView(String state) {
        if (state.equals("loading")) {
            pb.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
        } else if (state.equals("error")) {
            pb.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
        }
    }

    private void showDataFromDB(ApiResponse data) {
        listWeather.clear();
        for (ListForecast item : data.getList()) {
            listWeather.add(item);
        }
        adapter.notifyDataSetChanged();
        adapter.setForecastItemClickListener(this);
        updateView("complete");
    }

    @Override
    public void onForecastItemClick(ListForecast data, int position) {
        Intent intentDetail = new Intent(MainActivity.this, DetailActivity.class);
        intentDetail.putExtra("data", gson.toJson(data)); // mengirim data ke detail activity
        intentDetail.putExtra("position", position);
        startActivity(intentDetail);
    }
}
