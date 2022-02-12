package dev.morganv.ezbarber.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import dev.morganv.ezbarber.CallBacks.CallBack_Map;
import dev.morganv.ezbarber.Fragments.MapFragment;
import dev.morganv.ezbarber.R;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    MaterialButton btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        MapFragment mapFragment = new MapFragment();
        mapFragment.setCallBackMap(callBack_map);
        getSupportFragmentManager().beginTransaction().add(R.id.map_frame, mapFragment).commit();
        btnReturn = findViewById(R.id.details_BTN_return);
        btnReturn.setOnClickListener(v -> startActivity(new Intent(DetailsActivity.this, MonthActivity.class)));
    }

    CallBack_Map callBack_map = new CallBack_Map() {
        @Override
        public void mapClicked(double lat, double lon) {
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng mark = new LatLng(32.075444, 34.808313);
        googleMap.addMarker(new MarkerOptions().position(mark).title("Oren Vazana Hair Stylist"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mark));
    }
}
