package in.ac.iiit.cvit.heritage;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String LOGTAG = "MapsActivity";

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


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                    //Toast.makeText(MapsActivity.this, getString(R.string.turnon_gps), Toast.LENGTH_LONG).show();
                }
                Log.v(LOGTAG, "Broadcast Manager ");
            }
        };

//The following will detect gps status when activity starts
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationEnabled = false;
            Toast.makeText(MapsActivity.this, getString(R.string.turnon_gps), Toast.LENGTH_LONG).show();
        } else {
            locationEnabled = true;
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

        this.googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        );
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
            mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
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
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            this.googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(17.383186, 78.401838))
                    .title("Marker in me")
            );
        }

        boolean mRequestingLocationUpdates = true;
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        //Log.d(LOGTAG, "Calling startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                connectionResult.startResolutionForResult(MapsActivity.this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
