package com.kbcovingtonjr.wuddup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

// import statements for Google Map API usage
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

// Other things to import
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.LogRecord;


public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    String url = "http://138.68.31.136/appdata/";
    String user = "THE DUDE";
    double initLat = 40.001575;
    double initLong = -105.262845;
    double radius = 5;


    private static final LatLngBounds ADELAIDE = new LatLngBounds(
            new LatLng(-35.0, 138.58), new LatLng(-34.9, 138.61));
    private static final CameraPosition ADELAIDE_CAMERA = new CameraPosition.Builder()
            .target(new LatLng(-34.92873, 138.59995)).zoom(20.0f).bearing(0).tilt(0).build();

    private static final LatLngBounds PACIFIC = new LatLngBounds(
            new LatLng(-15.0, 165.0), new LatLng(15.0, -165.0));
    private static final CameraPosition PACIFIC_CAMERA = new CameraPosition.Builder()
            .target(new LatLng(0, -180)).zoom(4.0f).bearing(0).tilt(0).build();


    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;


    /** HashMap containing user preference data */
    private HashMap<String,String> dataIn = new HashMap<>();

    /** HashMap containing user GPS info e.g. lat/long coordinates */
    private HashMap<String,Integer> locationData = new HashMap<>();

    /** HashMap containing place info e.g. type of event, lat/long coordinates */
    private HashMap<String,HashMap<String,Double>> places = new HashMap<String,HashMap<String,Double>>();

    HashMap<String, String> hackyHeaders = new HashMap<String, String>();


    // For making server calls
    // Instantiate the RequestQueue.
    //RequestQueue requestQueue = Volley.newRequestQueue(this);






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        // Create user event in new activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add an event", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, UserAddEvent.class);
                startActivity(intent);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle changing radius
            // change zoom

            // populate new area

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final Location location = new Location("");
        location.setLatitude(initLat);
        location.setLongitude(initLong);

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        mMap.setOnCameraIdleListener(this);

        //zoomToLocation(location);


        // Set initial viewing area
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
//        Location location = service.getLastKnownLocation(provider);
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

//        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
//
//            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
//                    LOCATION_SERVICE.MY_PERMISSION_ACCESS_COURSE_LOCATION );
//        }

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, lm);
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//        LocationListener locationListener = new MyLocationListener();
//        LocationListener locationListener = new MyLocationListener();
//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
//        LocationListener locationListener = new MyLocationListener();
//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);


// Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
//                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        mMap.setOnCameraIdleListener(this);

        //zoomToLocation(location);

        // Set initial viewing area
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

        // TODO: totally not working...FIX!!!!
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
//                String lattt = Double.toString(location.getLatitude());
//                Toast.makeText(MainActivity.this, lattt, Toast.LENGTH_LONG).show();
            }
        }, 5000);

        String latitude = Double.toString(location.getLatitude());
        String longitude = Double.toString(location.getLongitude());
//        Toast.makeText(MainActivity.this, latitude, Toast.LENGTH_LONG).show();
////        try {
////            outObject.put("User","lknsdfnsdflksdf");
////            Toast.makeText(this, "Maybe?", Toast.LENGTH_SHORT).show();
////            jsObjRequest.getHeaders() throws AuthFailureError {
////                HashMap<String, String> headers = new HashMap<String, String>();
////                //headers.put("Content-Type", "application/json");
////                headers.put("MICHAEL", "FUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCK");
////                return headers;
////            };
//
////            outObject.put("Latitude",latitude);
////            outObject.put("Longitude",longitude);
////        }
////        catch (JSONException e) {
////            // I like really hope this does not happen
////            Toast.makeText(this, "Fuckballs", Toast.LENGTH_SHORT).show();
////        }

        JSONObject outObject = new JSONObject();

        //String lattt = Double.toString(location.getLatitude());
        //Toast.makeText(MainActivity.this, lattt, Toast.LENGTH_LONG).show();

        hackyHeaders.put("USER", user);
        hackyHeaders.put("LATITUDE", latitude);
        hackyHeaders.put("LONGITUDE", longitude);
        hackyHeaders.put("RADIUS", Double.toString(radius));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, outObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //mTxtDisplay.setText("Response: " + response.toString());
                    Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Damnit Michael", Toast.LENGTH_SHORT).show();

                    }
                })
//                {
//                @Override
//                protected Map<String, String> getParams()
//                {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("name", "Old Greg");
//                    params.put("domain", "http://creamy");
//
//                    return params;
//                }
//            }
        {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //headers.put("Content-Type", "application/json");
//            lat = getLatCoords();
//            long = getLongCoords();
                //hackyHeaders.put("MICHAEL", "FUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCKFUCK");
                return hackyHeaders;
            }
        };

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();


//        HashMap<String, Double[]> map = new HashMap<String, Double[]>();
//        places.put("Bar", new Double[2] = {40.000089, -105.2575162});
//        double[] = {40.000089, -105.2575162};
//        double [ ] coords = new double [2];
//        latitude = 40.000089;
//        longitude = -105.2575162;
//
//        dropPin(latitude, longitude);

        // Default coordinates when opening app...should be facebook or location data by default
//        places.put("Bar",<"Latitude",40.000089>);
//        places.put("Bar",<"Longitude",-105.2575162>);
//        places.put("Restaurant",39.9991184,-105.2635106>);
//        places.put("Restaurant",<"Longitude",-105.2635106>);

        // Add initial pins
//        showPins();

    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;

        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onCameraIdle() {
        //Snackbar mySnackbar = Snackbar.make(findViewById(R.layout.drawer_layout), mMap.getCameraPosition().toString(), Snackbar.LENGTH_LONG).show();
        //Toast.makeText(this, mMap.getCameraPosition().toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * Before the map is ready many calls will fail.
     * This should be called on all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (mMap == null) {
            //Toast.makeText(this, "Map ain't ready yet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Click handler for clamping to Pacific button.
     */
    public void zoomToLocation(Location location) {
        if (!checkReady()) {
            return;
        }
        mMap.setLatLngBoundsForCameraTarget(PACIFIC);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(PACIFIC_CAMERA));
    }
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            //mMarker = mMap.addMarker(new MarkerOptions().position(loc));
            if(mMap != null){
                //String lattt = Double.toString(location.getLatitude());
                //Toast.makeText(MainActivity.this, lattt, Toast.LENGTH_LONG).show();

                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f)); //to animate
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f));
            }
        }
    };

    private void dropPin(double coords[]) {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(coords[0], coords[1])).title("Marker"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(40,-105)).title("Marker"));
    }

    private void showPins() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(locationData.get("Latitude"),
                locationData.get("Longitude"))).title("Marker"));

    }

//    private void adjustZoom(Int yes) {
//
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//    }


}

