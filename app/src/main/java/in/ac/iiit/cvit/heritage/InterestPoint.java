package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HOME on 03-03-2017.
 */

public class InterestPoint implements Serializable {
    private int _id;
    private HashMap<String, String> monumentDetails;
    private HashMap<String, String> royalDetails;
    private SessionManager sessionManager;

    private static final String dataLocation = "completePackages/extracted/";

    private static final String imageType = ".jpg";
    private static final String latitudeTag = "lat";
    private static final String longitudeTag = "long";
    private static final String imageTag = "image";
    private static final String imagesTag = "images";

    private static final String imagesNameSplitter = ",";


    private static final String LOGTAG = "InterestPoint";

    public InterestPoint() {
        //Don't take context in constructor. This class will not be serializable
        monumentDetails = new HashMap<String, String>();
        royalDetails = new HashMap<String, String>();

    }


    /**
     * This method sets the interest point(monument) details
     *
     * @param key   It's the tag name of the particular xml field
     * @param value It's the value in the relevant tag of the xml file
     */
    public void setMonument(String key, String value) {
        monumentDetails.put(key, value);
    }

    /**
     * This method gives back the interest point(monument) details
     * @param key It's the tag name of the particular xml field
     * @return Value of the selected xml field
     */
    public String getMonument(String key) {
        return monumentDetails.get(key);
    }

    /**
     * This method sets the kings details
     * @param key It's the tag name of the particular xml field
     * @param value It's the value in the relevant tag of the xml file
     */
    public void setKing(String key, String value) {
        royalDetails.put(key, value);
    }

    /**
     * This method gives back the kings details
     * @param key It's the tag name of the particular xml field
     * @return Value of the selected xml field
     */
    public String getKing(String key) {
        return royalDetails.get(key);
    }


    /**
     * This class is used to get the image related to a particular interest point
     *
     * @return Image of Interest point in Bitmap data type
     */
    public Bitmap getMonumentTitleImage(String packageName_en, String interestPointName, Context context) {

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

        String imageName = monumentDetails.get(imageTag);
        //Log.v(LOGTAG, "interestPointName is " + interestPointName);

        String image_path = dataLocation + packageName_en + File.separator + imageName + imageType;

        File imageFile = new File(context.getFilesDir(), image_path);
        //Log.v(LOGTAG, imageFile.getAbsolutePath());
        if(imageFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            //Log.v(LOGTAG, imageName + imageType);

            return bitmap;
        }

        return null;

    }


    /**
     * This class is used to get the image path related to a particular interest point
     *
     * @return Image path of Interest point in String data type
     */
    public String getMonumentTitleImagePath(String packageName_en, String interestPointName, Context context) {

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

        String imageName = monumentDetails.get(imageTag);
        //Log.v(LOGTAG, "interestPointName is " + interestPointName);

        String image_path = dataLocation + packageName_en + File.separator + imageName + imageType;

        File imageFile = new File(context.getFilesDir(), image_path);
        //Log.v(LOGTAG, imageFile.getAbsolutePath());
        if (imageFile.exists()) {
            return imageFile.getAbsolutePath();
        }

        return null;

    }


    /**
     * This class is used to get the image related to a particular interest point
     *
     * @return Image of Interest point in Bitmap data type
     */
    public Bitmap getKingTitleImage(String packageName_en, String interestPointName, Context context) {

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

        String imageName = monumentDetails.get(imageTag);
        //       Log.v("getImage","reached getImage");


        String image_path = dataLocation + packageName_en + "/" + imageName + imageType;

        File imageFile = new File(context.getFilesDir(), image_path);
        Log.v("getImage", imageFile.getAbsolutePath());
        if (imageFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            //            bitmap = bitmap.createScaledBitmap(bitmap, 627, 353, false);
            Log.v("getImage", imageName + ".JPG");

            return bitmap;
        }

        return null;


    }


    /**
     * This class is called from ImagePagerFragmentActivity when Image button is clicked
     * This class is used to get all the images related to a particular interest point.
     * This class is not hard coded.
     *
     * @return Images of Interest point in Bitmap Array data type
     */
    public ArrayList<Bitmap> getMonumentImages(Context context, String packageName_en, String interestPointName) {

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

//        String[] image_names = {"a1", "a2", "a3", "a4", "a5"};

        String allImages = monumentDetails.get(imagesTag);

        //Log.v("getImages",interestPointName);

//        Log.v("getImages",allImages);
        List<String> imagesList = Arrays.asList(allImages.split(imagesNameSplitter));

        ArrayList<Bitmap> image_bitmaps = new ArrayList<Bitmap>();


        for (int i = 0; i < imagesList.size(); i++) {
            String imageName = imagesList.get(i);
//            Log.v("getImages",imageName);
            String image_path = dataLocation + packageName_en + "/" + imageName + imageType;

            File imageFile = new File(context.getFilesDir(), image_path);
            if (imageFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
//                bitmap = bitmap.createScaledBitmap(bitmap, 627, 353, false);
                image_bitmaps.add(bitmap);
            }
        }

        return image_bitmaps;

    }


    public ArrayList<String> getMonumentImagePaths(Context context, String packageName_en, String interestPointName) {

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

        String allImages = monumentDetails.get(imagesTag);

        //Log.v("getImages",interestPointName);

//        Log.v("getImages",allImages);
        ArrayList<String> imagesList = new ArrayList<String>();
        imagesList.addAll(Arrays.asList(allImages.split(imagesNameSplitter)));

        for (int i = 0; i < imagesList.size(); i++) {

            String image_path = context.getFilesDir() + File.separator
                    + dataLocation + packageName_en + File.separator + imagesList.get(i) + imageType;

            imagesList.set(i, image_path);

            File imageFile = new File(image_path);
            if (!imageFile.exists()) {
                imagesList.remove(i);
            }

        }

        return imagesList;

    }


    private double betweenDistance;

    double distance(double iLat, double iLong) {
        double pLat, pLong;
        double dLat, dLong;
        double sum;

        pLat = Double.parseDouble(monumentDetails.get(latitudeTag));
        pLong = Double.parseDouble(monumentDetails.get(longitudeTag));

//        Log.d("InterestPoint:distance", "pLat="+ pLat);
//        Log.d("InterestPoint:distance", "pLong="+ pLong);

        /* Euclidean distance. Should work. */
        dLat = pLat - iLat;
        dLong = pLong - iLong;
        sum = dLat * dLat + dLong * dLong;

        betweenDistance = Math.sqrt(sum);

        return Math.sqrt(sum);
    }

    /**
     * This method is called from NearbyPointsFragment. This method gives the view angle of the interest point
     * based on user's current location and mobile's direction
     *
     * @param iLat  latitude
     * @param iLong longitude
     * @return view angle from mobile's direction od axis
     */
    double giveAngle(double iLat, double iLong, double[] coEfficients) {

        double pLat;
        double pLong;
        double angle = 0;
        double perpDist = 0;


        double a = coEfficients[0];
        double b = coEfficients[1];
        double c = coEfficients[2];

//         Log.d("giveAngle", "a="+ a);
//         Log.d("giveAngle", "b="+ b);
//         Log.d("giveAngle", "c="+ c);

        pLat = Double.parseDouble(monumentDetails.get(latitudeTag));
        pLong = Double.parseDouble(monumentDetails.get(longitudeTag));
        //        Log.d("InterestPoint:distance", "pLat="+ pLat);
        //        Log.d("InterestPoint:distance", "pLong="+ pLong);

        perpDist = (a * pLat + b * pLong + c) / (Math.sqrt((Math.pow(a, 2) + Math.pow(b, 2))));
//         Log.d("giveAngle:perpDist", "perpDist="+ perpDist);

        angle = Math.asin(perpDist / betweenDistance);

        return angle;
    }
}
