package com.example.tunhanmyae.DeliveeServer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tunhanmyae.DeliveeServer.Common.Common;
import com.example.tunhanmyae.DeliveeServer.Common.DirectionJSONParser;
import com.example.tunhanmyae.DeliveeServer.Remote.IGoCoordinates;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private GoogleMap mMap;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_PERMISSION_REQUEST = 1001;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_INTERVAL = 5000;
    private static int DISPLACEMT = 10;


    private IGoCoordinates mService;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);

        mService = Common.getGeoCodeService();


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestRuntimePermission();

        }
        else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClinet();
                createLocationRequest();
                
            }
        }
        displayLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestRuntimePermission();

        }
        else
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation!= null)
            {
                double latiude = mLastLocation.getLatitude();
                double lountitude = mLastLocation.getLongitude();
                LatLng yourLoaction = new LatLng(latiude,lountitude);
                mMap.addMarker(new MarkerOptions().position(yourLoaction).title("Your Loaciton"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLoaction));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                drawRoute(yourLoaction,Common.currentRequest.getAddress());



            }
            else
            {
//                Toast.makeText(this,"Couldn't get the location",Toast.LENGTH_SHORT).show();
                Log.d("DEBUG","Couldn't get the location");
            }
        }

    }

    private void drawRoute(final LatLng yourLocation, String address) {
        mService.getGeoCode(address).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try
                { JSONObject jsonObject = new JSONObject(response.body().toString());

                    String lat=((JSONArray)jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lat").toString();

                    String lng=((JSONArray)jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lng").toString();

                    LatLng orderLocation = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));

                    Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.box);
                    bitmap=Common.scaleBitmap(bitmap,70,70);

                    MarkerOptions marker = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title("Order of "+Common.currentRequest.getPhone())
                            .position(orderLocation);

                    mMap.addMarker(marker);

//                    mService.getDirections(yourLocation.latitude+","+yourLocation.longitude,orderLocation.latitude+","+orderLocation.longitude)
//                            .enqueue(new Callback<String>() {
//                                @Override
//                                public void onResponse(Call<String> call, Response<String> response) {
//                                    new ParserTask().execute(response.body().toString());
//                                }
//
//                                @Override
//                                public void onFailure(Call<String> call, Throwable t) {
//
//                                }
//                            });







                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode!= ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(this,"This device is not support ",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this,new String[]
                {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION

                },LOCATION_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    if(checkPlayServices())
                    {
                        buildGoogleApiClinet();
                        createLocationRequest();
                        displayLocation();


                    }

                }
                break;
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMT);
    }

    protected synchronized void buildGoogleApiClinet() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
        

    }

    private void startLocationUpdates() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient!= null)
        {
            mGoogleApiClient.connect();
        }
    }

//    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>{
//        ProgressDialog mDialog = new ProgressDialog(TrackingOrder.this);
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mDialog.setMessage("Please Waiting...");
//            mDialog.show();
//        }
//
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
//            JSONObject jsonObject;
//            List<List<HashMap<String,String>>> routes = null;
//                    try
//                    {
//                        jsonObject = new JSONObject(strings[0]);
//                        DirectionJSONParser parser = new DirectionJSONParser();
//                        routes = parser.parse(jsonObject);
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    return routes;
//        }
//
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
//           mDialog.dismiss();
//
//            ArrayList points;
//            PolylineOptions lineOptions = null;
//
//            for(int i=0;i<lists.size();i++)
//            {
//                points = new ArrayList();
//                lineOptions = new PolylineOptions();
//
//                List<HashMap<String,String>> path = lists.get(i);
//
//
//                for (int j=0;j< path.size();j++)
//                {
//                    HashMap<String,String> point = path.get(j);
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//
//                    LatLng position = new LatLng(lat,lng);
//                    points.add(position);
//                }
//
//
//                lineOptions.addAll(points);
//                lineOptions.width(12);
//                lineOptions.color(Color.RED);
//                lineOptions.geodesic(true);
//
//
//
//
//
//            }
//            mMap.addPolyline(lineOptions);
//
//
//
//        }
//    }
}
