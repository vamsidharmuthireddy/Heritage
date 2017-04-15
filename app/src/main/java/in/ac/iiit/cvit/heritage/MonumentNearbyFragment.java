package in.ac.iiit.cvit.heritage;

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
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by HOME on 13-03-2017.
 */

public class MonumentNearbyFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    /**
     * This class gives back the three nearest interest points and uses PageAdapter class to set them on screen
     * computeNearby() is called whenever location data or sensor data is changed
     * From onCreateView only refreshRecyclerView() is called
     */


    private static final String LOGTAG = "MonumentNearby";
    //Define a request code to send to Google Play services
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
    private ArrayList<InterestPoint> sortedInterestPoints = null;
    private ArrayList<InterestPoint> interestPoints = null;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    //    private RecyclerView.LayoutManager recyclerViewLayoutManager;//cannot be used to set the position after refresh
    private LinearLayoutManager recyclerViewLayoutManager;
    private Context context;
    private long currentTime;
    private long previousTime;

    IntentFilter gpsFilter = new IntentFilter("android.location.PROVIDERS_CHANGED");
    private BroadcastReceiver gpsBroadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final LayoutInflater _inflater = inflater;
        final ViewGroup _container = container;

        final View root = inflater.inflate(R.layout.fragment_nearby_monuments, container, false);
        context = getActivity();
        mGoogleApiClient = null;
        createLocationClients();


        Calendar calendar = Calendar.getInstance();
        currentTime = calendar.getTimeInMillis();
        previousTime = 0;

//        Log.d(LOGTAG,"sensors in onCreate got created");

        //interestPoints = new PackageContentActivity().giveMonumentList(context);
        //interestPoints = new MonumentActivity().monumentList;

        interestPoints = ((MonumentActivity) this.getActivity()).monumentList;
        //Log.v(LOGTAG, "interestPoints size is " + interestPoints.size());

//initializing the array
        sortedInterestPoints = new ArrayList<InterestPoint>();
        for (int i = 0; i < Math.min(TRUNCATION_LIMIT, interestPoints.size()); i++) {
            //sortedInterestPoints.add(interestPoints.get(i));
        }

        final TextView textView = (TextView) root.findViewById(R.id.cardview_text);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_nearby_monuments);

        //The following will listen for change in gps status when activity is running
        gpsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocationManager broadcastLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (!broadcastLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        && !broadcastLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationEnabled = false;
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.turnon_gps);
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), getString(R.string.turnon_gps), Toast.LENGTH_LONG).show();
                } else {
                    //Initializing the recyclerView and calling the refreshRecyclerView

                    textView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setHasFixedSize(true);
                    //recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerViewLayoutManager = new MainActivity.PreLoadingLinearLayoutManager(getContext());
                    new MainActivity.PreLoadingLinearLayoutManager(getContext()).setPages(1);

                    recyclerView.setLayoutManager(recyclerViewLayoutManager);
                    refreshRecyclerView();

                }
                Log.v(LOGTAG, "Broadcast Manager ");
            }
        };


//The following will detect gps status when activity starts
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationEnabled = false;

            //inflating a text view to tell about turning on gps

            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.turnon_gps);
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getString(R.string.turnon_gps), Toast.LENGTH_LONG).show();
        } else {
            locationEnabled = true;

            //Initializing the recyclerView and calling the refreshRecyclerView
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            //recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
            recyclerViewLayoutManager = new MainActivity.PreLoadingLinearLayoutManager(getContext());
            new MainActivity.PreLoadingLinearLayoutManager(getContext()).setPages(1);

            recyclerView.setLayoutManager(recyclerViewLayoutManager);
            refreshRecyclerView();


        }

        return root;
    }

    private void refreshRecyclerView() {
        //setting the view of the NEARBY tab
//        Log.v(LOGTAG, "going to set sorted interest points");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int yOffset = recyclerView.computeVerticalScrollOffset();
                int index = recyclerViewLayoutManager.findFirstVisibleItemPosition();
                View v = recyclerViewLayoutManager.getChildAt(0);
                if (v != null) {
                    itemOffset = v.getTop();    //y-offset of an item
                }
                if (yOffset != 0) {
                    yPosition = yOffset;        //y-offset of the entire view
                    yIndex = index;             //index of first visible item
                }

            }
        });
        //Log.i(LOGTAG, "scroll yPosition = " + yPosition + " yIndex = " + yIndex + " itemOffset = " + itemOffset);


        String packageName_en = ((MonumentActivity) this.getActivity()).packageName_en;
        //After refreshing the view with new data. The following line sets the position of view to previous one
        recyclerViewLayoutManager.scrollToPositionWithOffset(yIndex, itemOffset);
        recyclerViewAdapter = new MonumentNearbyAdapter(sortedInterestPoints, getContext(), packageName_en);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        getActivity().registerReceiver(gpsBroadcastReceiver, gpsFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        getActivity().unregisterReceiver(gpsBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
        getActivity().registerReceiver(gpsBroadcastReceiver, gpsFilter);
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
            mGoogleApiClient = new GoogleApiClient.Builder(context)
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
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
        computeNearby(currentLatitude, currentLongitude);


    }

    public void computeNearby(Double currentLatitude, Double currentLongitude) {
        ArrayList<Pair<Double, Integer>> Indices = new ArrayList<>();
        double distance;
        Pair<Double, Integer> P;

        for (int i = 0; i < interestPoints.size(); i++) {
            //getting the distance of all the interest points from the current location
            distance = interestPoints.get(i).distance(currentLatitude, currentLongitude);
            //           Log.d(LOGTAG, "Distance = "+distance);
            P = new Pair(distance, i);
            Indices.add(P);

        }

        //Arranging the distances in their ascending order
        Collections.sort(Indices, new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(final Pair<Double, Integer> left, final Pair<Double, Integer> right) {
                if (left.first < right.first) {
                    return -1;
                } else if (left.first == right.first) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        //just adding the sorted list to the main list with new indices
        //not needed?
        for (int i = 0; i < interestPoints.size(); i++) {
            distance = Indices.get(i).first;
            //          Log.d(LOGTAG, "SDistance = "+distance);
            P = new Pair(distance, i);
            Indices.add(P);
        }

        //setting the order of interest points
        InterestPoint interestPoint;
        for (int i = 0; i < Math.min(TRUNCATION_LIMIT, interestPoints.size()); i++) {
            interestPoint = interestPoints.get(Indices.get(i).second);
            if (sortedInterestPoints.size() < TRUNCATION_LIMIT) {
                sortedInterestPoints.add(i, interestPoint);
            } else {
                sortedInterestPoints.set(i, interestPoint);
            }

        }
        Log.v(LOGTAG, "sortedInterestPoints.size() = " + sortedInterestPoints.size());
        refreshRecyclerView();
    }


}
