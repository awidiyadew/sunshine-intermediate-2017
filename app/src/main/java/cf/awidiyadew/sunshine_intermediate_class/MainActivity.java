package cf.awidiyadew.sunshine_intermediate_class;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import cf.awidiyadew.sunshine_intermediate_class.model.ApiResponse;
import cf.awidiyadew.sunshine_intermediate_class.model.DummyForecast;
import cf.awidiyadew.sunshine_intermediate_class.model.ListForecast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    ListForecastAdapter adapter;
    private List<DummyForecast> listDataDummy = new ArrayList<>();
    private List<ListForecast> listWeather = new ArrayList<>();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setElevation(0); // menghilangkan shadow pada action bar

        setupRecyclerview();

        // TODO: 5/19/17 B. Setup click listener : step 4
        adapter.setForecastItemClickListener(new ListForecastAdapter.ForecastItemClickListener() {
            @Override
            public void onForecastItemClick(ListForecast data) {
                Toast.makeText(MainActivity.this, "Test click - max temp " + data.getTemp().getDerajatMaxTemp(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupRecyclerview(){
        //adapter = new ListForecastAdapter(listDataDummy);
        adapter = new ListForecastAdapter(listWeather);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        //getDummyData();
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

        final String url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=-8.650000&lon=115.216667&cnt=16&appid=3e40a7f5fd7287ce34741d4c5f82779a&units=metric";

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);

                ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

                for (ListForecast listItem : apiResponse.getList()){
                    listWeather.add(listItem);
                }

                adapter.notifyDataSetChanged();

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null){
                    Log.e(TAG, "onErrorResponse: " + error.getMessage());
                } else {
                    Log.e(TAG, "onErrorResponse: " + "Something wrong happened");
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

}
