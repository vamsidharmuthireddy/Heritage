package in.ac.iiit.cvit.heritage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MapsActivityGoogle extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String LOGTAG = "MapsActivityGoogle";

    private GoogleMap googleMap;
    private MapView mapView;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int TRUNCATION_LIMIT = 3;
    private static final int waitTimeInSeconds = 2;
    private static int yPosition;
    private static int yIndex;
    private static int itemOffset;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private boolean locationEnabled;
    private double currentLatitude;
    private double currentLongitude;

    IntentFilter gpsFilter = new IntentFilter("android.location.PROVIDERS_CHANGED");
    private BroadcastReceiver gpsBroadcastReceiver;


    private SessionManager sessionManager;
    private String packageName;
    public String packageName_en;
    public String interestPointName;
    public String interestPointType;
    private Toolbar toolbar;
    private String language;
    public ArrayList<InterestPoint> monumentList;
    public ArrayList<InterestPoint> monumentList_en;
    public static String decider = new String();
    public ArrayList<Double> latitudeList = new ArrayList<Double>();
    public ArrayList<Double> longitudeList = new ArrayList<Double>();
    public ArrayList<String> titleList = new ArrayList<String>();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LocaleManager localeManager = new LocaleManager(MapsActivityGoogle.this);
        localeManager.loadLocale();
        language = Locale.getDefault().getLanguage();

        setContentView(R.layout.activity_maps_google);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        packageName = getIntent().getStringExtra(getString(R.string.package_name));
        packageName_en = getIntent().getStringExtra(getString(R.string.package_name_en));
        decider = getIntent().getStringExtra(getString(R.string.location_list));
        sessionManager = new SessionManager();
        sessionManager.setSessionPreferences(MapsActivityGoogle.this, getString(R.string.package_name), packageName);

        if (decider.equals(getString(R.string.all))) {
            Log.v(LOGTAG, "Entered Gallery from PackageContentActivity");

            Bundle bundle = getIntent().getBundleExtra(getString(R.string.monumentList_bundle));
            monumentList = (ArrayList<InterestPoint>) bundle.getSerializable(getString(R.string.monumentList));
            monumentList_en = (ArrayList<InterestPoint>) bundle.getSerializable(getString(R.string.monumentList_en));
        } else {
            Log.v(LOGTAG, "Entered Gallery from InterestPointActivity");
            interestPointName = getIntent().getStringExtra(getString(R.string.interestpoint_name));
            interestPointType = getIntent().getStringExtra(getString(R.string.interest_point_type));
            toolbar.setTitle(interestPointName.toUpperCase());
            Log.v(LOGTAG, "clicked interest point is " + interestPointName.toUpperCase());
        }

//        getLocationList();
        initializeMapClients();

        new loadActivityContent().execute();
    }

    private class loadActivityContent extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapsActivityGoogle.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(0);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getLocationList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    public void getLocationList() {

        String name[] = new String[1000];
        Double lat[] = new Double[1000];
        Double lon[] = new Double[1000];
        for (int i = 0; i < monumentList.size(); i++) {

            InterestPoint monument = monumentList.get(i);

            name[i] = monument.getMonument(getString(R.string.interest_point_title));
            lat[i] = Double.parseDouble(monument.getMonument(getString(R.string.interest_point_latitude)));
            lon[i] = Double.parseDouble(monument.getMonument(getString(R.string.interest_point_longitude)));

            if (name[i] != null && lat[i] != null && lon[i] != null) {
                titleList.add(name[i]);
                latitudeList.add(lat[i]);
                longitudeList.add(lon[i]);
            }

        }


    }


    private void initializeMapClients() {
        mGoogleApiClient = null;
        createLocationClients();

//The following will listen for change in gps status when activity is running
        gpsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocationManager broadcastLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (!broadcastLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        && !broadcastLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationEnabled = false;
                    //Toast.makeText(MapsActivityGoogle.this, getString(R.string.turnon_gps), Toast.LENGTH_LONG).show();
                }
                Log.v(LOGTAG, "Broadcast Manager ");
            }
        };

//The following will detect gps status when activity starts
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationEnabled = false;
            Toast.makeText(MapsActivityGoogle.this, getString(R.string.turnon_gps), Toast.LENGTH_LONG).show();
        } else {
            locationEnabled = true;
        }

    }

    private class placeMarkers extends AsyncTask<Void, MarkerOptions, LatLngBounds.Builder> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapsActivityGoogle.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(0);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected LatLngBounds.Builder doInBackground(Void... params) {
            double latSum = 0;
            double lonSum = 0;
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (int i = 0; i < titleList.size(); i++) {

                int hue = (int) (Math.random() * 360 + 0);
                LatLng interestPointLocation = new LatLng(latitudeList.get(i), longitudeList.get(i));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(interestPointLocation)
                        .title(titleList.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker());

                //googleMap.addMarker(markerOptions);
                publishProgress(markerOptions);

                latSum = latSum + latitudeList.get(i);
                lonSum = lonSum + longitudeList.get(i);
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(interestPointLocation));

                boundsBuilder.include(interestPointLocation);
            }


            return boundsBuilder;
        }


        protected void onProgressUpdate(MarkerOptions... options) {
            // super.onProgressUpdate(values);
            googleMap.addMarker(options[0]);
        }

        @Override
        protected void onPostExecute(LatLngBounds.Builder boundsBuilder) {
            //super.onPostExecute(aVoid);
            progressDialog.dismiss();

            LatLngBounds bounds = boundsBuilder.build();
            //Change the padding as per needed
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, width, 15);
            //using height takes the markers out of view
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,width, height, 15);
            googleMap.animateCamera(cameraUpdate);


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
    @Override
    public void onMapReady(GoogleMap _googleMap) {
        this.googleMap = _googleMap;

//        Location location = new Location(googleMap.getMyLocation());

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        //Asking for gps permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        googleMap.setMyLocationEnabled(true);
        new placeMarkers().execute();
/*
        double latSum = 0;
        double lonSum = 0;
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for(int i=0;i<titleList.size();i++){

            int hue = (int )(Math.random() * 360 + 0);
            LatLng interestPointLocation = new LatLng(latitudeList.get(i),longitudeList.get(i));

            googleMap.addMarker(new MarkerOptions()
            .position(interestPointLocation)
            .title(titleList.get(i))
            .icon(BitmapDescriptorFactory.defaultMarker()));

            latSum = latSum + latitudeList.get(i);
            lonSum = lonSum + longitudeList.get(i);
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(interestPointLocation));

            boundsBuilder.include(interestPointLocation);
        }

        LatLngBounds bounds = boundsBuilder.build();
        //Change the padding as per needed
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,width, width, 15);
        //using height takes the markers out of view
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,width, height, 15);
        googleMap.animateCamera(cameraUpdate);
*/
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latSum, lonSum)));
    }


    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        registerReceiver(gpsBroadcastReceiver, gpsFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        unregisterReceiver(gpsBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
        registerReceiver(gpsBroadcastReceiver, gpsFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOGTAG, "onPause");
        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.v(LOGTAG, "Connection is suspended");
    }

    private void createLocationClients() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(MapsActivityGoogle.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        //Log.d(LOGTAG, "Clients Created");
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.v(LOGTAG, "Running onConnected");
        if (ActivityCompat.checkSelfPermission(MapsActivityGoogle.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivityGoogle.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v(LOGTAG, "GPS got turned off");
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.v(LOGTAG, "OnConnected currentLatitude = null currentLongitude = null");

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            Log.v(LOGTAG, "OnConnected currentLatitude = " + currentLatitude + " currentLongitude = " + currentLongitude + " accuracy = " + location.getAccuracy());

        }

        boolean mRequestingLocationUpdates = true;
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        //Log.d(LOGTAG, "Calling startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(MapsActivityGoogle.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivityGoogle.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             * This is caused when the device doesn't has the appropriate version of the Google Play services APK
             */
        Log.v(LOGTAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(MapsActivityGoogle.this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */

        }
    }

    /**
     * We call computeNearby method from here which calculates nearby interest points based on gps
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.v(LOGTAG, "OnLocationChanged currentLatitude = " + currentLatitude + " currentLongitude = " + currentLongitude + " accuracy = " + location.getAccuracy());


    }




}
