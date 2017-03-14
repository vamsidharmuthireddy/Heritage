package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by HOME on 13-03-2017.
 */

public class MonumentNearbyFragment extends Fragment implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    /**
     * This class gives back the three nearest interest points and uses PageAdapter class to set them on screen
     * computeNearby() is called whenever location data or sensor data is changed
     * From onCreateView only refreshRecyclerView() is called
     */


    private static final String LOGTAG = "Heritage:Nearby";
    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

    private ArrayList<InterestPoint> sortedInterestPoints = null;
    private ArrayList<InterestPoint> interestPoints = null;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    //    private RecyclerView.LayoutManager recyclerViewLayoutManager;//cannot be used to set the position after refresh
    private LinearLayoutManager recyclerViewLayoutManager;

    private static int yPosition;
    private static int yIndex;
    private static int itemOffset;

    private Context context;

    private static final int TRUNCATION_LIMIT = 3;


    private static final int waitTimeInSeconds = 2;
    private long currentTime;
    private long previousTime;

    Float azimuth = (float) 0;
    Float pitch = (float) 0;
    Float roll = (float) 0;

    float oldAzimuth = (float) 0;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nearby_monuments, container, false);
        context = getActivity();
        mGoogleApiClient = null;
        createLocationClients();

        Calendar calendar = Calendar.getInstance();
        currentTime = calendar.getTimeInMillis();
        previousTime = 0;

        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        Log.d(LOGTAG,"sensors in onCreate got created");


        //interestPoints = new PackageContentActivity().giveMonumentList(context);
        //interestPoints = new MonumentActivity().monumentList;

        interestPoints = ((MonumentActivity) this.getActivity()).monumentList;
        Log.v(LOGTAG, "interestPoints size is " + interestPoints.size());

        //initializing the array
        sortedInterestPoints = new ArrayList<InterestPoint>();
        for (int i = 0; i < Math.min(TRUNCATION_LIMIT, interestPoints.size()); i++) {
            sortedInterestPoints.add(interestPoints.get(i));
        }

        //Initializing the recyclerView and calling the refreshRecyclerView
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_nearby_monuments);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        refreshRecyclerView();

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
        Log.i(LOGTAG, "scroll yPosition = " + yPosition + " yIndex = " + yIndex + " itemOffset = " + itemOffset);


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
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, 5000);
        mSensorManager.registerListener(this, magnetometer, 5000);
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        //       Log.v(this.getClass().getSimpleName(), "onPause()");


        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onConnectionSuspended(int i) {
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
        //Log.d(LOGTAG, "Running onConnected");
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //Log.d(LOGTAG, "Permission Denied");
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            // Display lat long on screen
            /*
            setContentView(R.layout.activity_main);
            TextView textView = (TextView) findViewById(R.id.coordinates);
            textView.setText("latitude: " + currentLatitude + " longitude: " + currentLongitude);
            */
            //Log.d(LOGTAG, "Creating toast, onConnected");
            //Toast.makeText(context, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
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
            //Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
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
        //Toast.makeText(context, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        computeNearby(currentLatitude, currentLongitude);


    }


    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                // orientation contains azimuth, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);


                oldAzimuth = azimuth;
                azimuth = (float) (180 * orientation[0] / Math.PI);
                pitch = (float) (180 * orientation[1] / Math.PI);
                roll = (float) (180 * orientation[2] / Math.PI);


                if (azimuth < 0) {
                    azimuth = azimuth + (float) Math.PI;
                }

                if (roll < 0) {
                    roll = roll + (float) Math.PI;
                }
                if (pitch < 0) {
                    pitch = pitch + (float) Math.PI / 2;
                }

                //               Log.d(LOGTAG,"calling computeNearby");
                //               computeNearby(currentLatitude, currentLongitude);

                //               Log.d("onSensorChanged:", "azimuth = "+ azimuth);
                //             Log.d("onSensorChanged:", "oldAzimuth = "+ oldAzimuth);
            }

            Calendar calendar = Calendar.getInstance();
            currentTime = calendar.getTimeInMillis();
            int timeDifference = (int) (currentTime - previousTime) / 1000;
//            Log.v(LOGTAG, "currentTime = "+currentTime+" previousTime = "+previousTime+" timeDifference = "+timeDifference);
            if (timeDifference > waitTimeInSeconds) {
                computeNearby(currentLatitude, currentLongitude);
                previousTime = currentTime;
            }


        }
    }


    public void computeNearby(Double currentLatitude, Double currentLongitude) {
        ArrayList<Pair<Double, Integer>> Indices = new ArrayList<>();
        double distance;
        Pair<Double, Integer> P;

        ArrayList<Pair<Double, Integer>> angleIndices = new ArrayList<>();
        double angle;
        Pair<Double, Integer> angleP;


        for (int i = 0; i < interestPoints.size(); i++) {
            //getting the distance of all the interest points from the current location
            distance = interestPoints.get(i).distance(currentLatitude, currentLongitude);
            //           Log.d(LOGTAG, "Distance = "+distance);
            P = new Pair(distance, i);
            Indices.add(P);

            //getting the co-efficients of line of view
            double[] coEfficients = setLine(currentLatitude, currentLongitude);
            //getting view angle of interest point from line of sight
            angle = interestPoints.get(i).giveAngle(currentLatitude, currentLongitude, coEfficients);
//            Log.d(LOGTAG, "angle = "+ angle);
            angleP = new Pair(angle, i);
            angleIndices.add(angleP);
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
            sortedInterestPoints.set(i, interestPoint);
        }

        /**
         * Edited by vamsi
         */
        ArrayList<Pair<Double, Integer>> finalAngleIndices = new ArrayList<>();
        //setting the angle in the ascending order of distances
        //arranging the angles in the order of distances
        for (int i = 0; i < interestPoints.size(); i++) {

            int key = Indices.get(i).second;    //getting the nearest interest point number

            double tempAngle = angleIndices.get(key).first; //getting the angle of nearest interest point

//            Log.d(LOGTAG, "nearest Distance angles= "+tempAngle);
            Pair tempP = new Pair(tempAngle, key);
            finalAngleIndices.add(tempP);
        }

        //continue from here
        //sort the first three nearest distance points based on their view angles

        ArrayList<Pair<Double, Integer>> finalThreeAngleIndices = new ArrayList<>();
        if (finalAngleIndices.size() != 0) {
            finalThreeAngleIndices.add(new Pair(finalAngleIndices.get(0).first, finalAngleIndices.get(0).second));
            finalThreeAngleIndices.add(new Pair(finalAngleIndices.get(1).first, finalAngleIndices.get(1).second));
            finalThreeAngleIndices.add(new Pair(finalAngleIndices.get(2).first, finalAngleIndices.get(2).second));

            Collections.sort(finalThreeAngleIndices, new Comparator<Pair<Double, Integer>>() {
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


            Log.i(LOGTAG, "angle0 = " + finalThreeAngleIndices.get(0).first * 180 / Math.PI);
            Log.i(LOGTAG, "angle1 = " + finalThreeAngleIndices.get(1).first * 180 / Math.PI);
            Log.i(LOGTAG, "angle2 = " + finalThreeAngleIndices.get(2).first * 180 / Math.PI);
        }
        //setting the order of three points based on the view angles of the nearest points
        for (int i = 0; i < Math.min(TRUNCATION_LIMIT, interestPoints.size()); i++) {
            interestPoint = interestPoints.get(finalThreeAngleIndices.get(i).second);
            sortedInterestPoints.set(i, interestPoint);
            Log.i(LOGTAG, "interestPoint " + i + " = " + interestPoint.getMonument(getString(R.string.interest_point_title)));
        }

        refreshRecyclerView();
    }


    /**
     * This method calculates line equation of mobile axis
     *
     * @param currentLatitude
     * @param currentLongitude
     * @return co-efficients of the line a.x + b.y + c = 0
     */
    public double[] setLine(Double currentLatitude, Double currentLongitude) {

        double angle = 1;
        double a, b, c;
        double[] coEfficients = {1, 1, 0};
        //       Log.d("setLine:", "azimuth = "+ azimuth);
        //       Log.d("setLine:", "oldAzimuth = "+ oldAzimuth);

        if (azimuth != null) {
            angle = (float) azimuth;
            if (angle == 0) {
                angle = Math.PI / 180;
            }
            if (angle % ((Math.PI) / 2) == 0) {
                a = 0;
                b = 1;
                c = (-currentLongitude);
            } else {
                a = -(Math.tan((double) angle));
                b = 1;
                c = (Math.tan((double) angle) * currentLatitude) - currentLongitude;
            }
//            Log.d("setLine:Using azimuth", "azimuth = "+ angle);

            coEfficients[0] = a;
            coEfficients[1] = b;
            coEfficients[2] = c;

        } else {
            angle = (float) oldAzimuth;
            if (angle == 0) {
                angle = Math.PI / 180;
            }

            if (angle % ((Math.PI) / 2) == 0) {
                a = 0;
                b = 1;
                c = (-currentLongitude);
            } else {
                a = -(Math.tan((double) angle));
                b = 1;
                c = (Math.tan((double) angle) * currentLatitude) - currentLongitude;
            }
//            Log.d("setLine:UsingOldAzimuth", "oldAzimuth = "+ angle);

            coEfficients[0] = a;
            coEfficients[1] = b;
            coEfficients[2] = c;
        }


        return coEfficients;
    }


}
