package in.ac.iiit.cvit.heritage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

/**
 * Created by HOME on 06-03-2017.
 */

public class HeritageSite {

    public static final String LOGTAG = "HeritageSite";
    private HashMap<String, String> heritageSitedetails;
    private SessionManager sessionManager;

    private static final String dataLocation = "Android/data/in.ac.iiit.cvit.heritage/files/introPackages/extracted/";

    private static final String imageType = ".jpg";
    private static final String latitudeTag = "lat";
    private static final String longitudeTag = "long";
    private static final String imageTag = "image";
    private static final String infoTag = "info";
    private static final String shortInfoTag = "shortinfo";


    public HeritageSite() {
        heritageSitedetails = new HashMap<String, String>();
    }

    /**
     * This method sets the interest point(monument) details
     *
     * @param key   It's the tag name of the particular xml field
     * @param value It's the value in the relevant tag of the xml file
     */
    public void setHeritageSite(String key, String value) {
        heritageSitedetails.put(key, value);
    }

    /**
     * This method gives back the interest point(monument) details
     * @param key It's the tag name of the particular xml field
     * @return Value of the selected xml field
     */
    public String getHeritageSite(String key) {
        return heritageSitedetails.get(key);
    }


    public Bitmap getHeritageSiteImage(String packageName) {


        String imageName = heritageSitedetails.get(imageTag);
        //       Log.v("getImage","reached getImage");

        String image_path = dataLocation + imageName + imageType;
        //String image_path =  dataLocation+"golconda_1.jpg";

        File imageFile = new File(Environment.getExternalStorageDirectory(),image_path);
        Log.v(LOGTAG, imageFile.getAbsolutePath());
        if(imageFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            //            bitmap = bitmap.createScaledBitmap(bitmap, 627, 353, false);
            Log.v(LOGTAG, "image exists");

            return bitmap;
        }

        return null;
    }






}
