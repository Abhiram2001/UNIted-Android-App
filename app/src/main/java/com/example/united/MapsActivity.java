package com.example.united;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.united.Utils.location;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  ArrayList<LatLng> arrayList = new ArrayList<LatLng>();

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;
    List<String[]> list = new ArrayList<String[]>();
    location distance = new location();







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String[] permissions = {Manifest.permission.INTERNET,Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this,permissions, PackageManager.PERMISSION_GRANTED);

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference coursesRef = rootRef.child("Friends").child(mUser.getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("hello",String.valueOf(ds.child("UserID")));

                    mUserRef.child((ds.child("UserID").getValue(String.class))).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                String latitude = snapshot.child("latitude").getValue(String.class);
                                String longitude = snapshot.child("longitude").getValue(String.class);
                                String name = snapshot.child("Name").getValue(String.class);
                                list.add(new String[]{latitude, longitude, name});
                                arrayList.add(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
                //Do what you need to do with the list of string
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        coursesRef.addListenerForSingleValueEvent(valueEventListener);

        for (String[] strArr : list) {
            LatLng latLng = new LatLng(Double.parseDouble(strArr[0]), Double.parseDouble(strArr[1]));
            Log.d("hello","hi");

            arrayList.add(new LatLng(Double.parseDouble(strArr[0]), Double.parseDouble(strArr[1])));
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//
//        mMap = googleMap;
//
//        for (int i=0;i<arrayList.size();i++) {
//            mMap.addMarker(new MarkerOptions().position(arrayList.get(i)).title(list.get(i)[2]));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));
//
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(15.8281, 78.0373);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                MarkerOptions markerOptions  = new MarkerOptions();

                Log.d("hello",arrayList.size()+"");
                Log.d("hello",list.size()+"");


                double lat = location.getLatitude();
                double lon = location.getLongitude();
                LatLng latLng = new LatLng(lat,lon);
                String countryname = "";
                Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(lat,lon,1);
                    Address address = addressList.get(0);
                    countryname=address.getCountryName();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (int i=0;i<arrayList.size();i++) {
                    Log.d("hello",distance.distance(Double.parseDouble(list.get(i)[0]), Double.parseDouble(list.get(i)[1]),lat,lon,"K"));
                    mMap.addMarker(markerOptions.position(arrayList.get(i)).title(list.get(i)[2]+"--"+distance.distance(Double.parseDouble(list.get(i)[0]), Double.parseDouble(list.get(i)[1]),lat,lon,"K") + "KM"));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));

                }

                mMap.addMarker(markerOptions.position(latLng).title("mylocation"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Log.d("hello","out");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override

            public void onProviderDisabled(String provider) {

            }
        };
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,3000,3,locationListener);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }
}