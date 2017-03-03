package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HOME on 03-03-2017.
 */

public class SessionManager {
    /**
     * This class is first called from PackageLisActivity.
     * When user clicks ona heritage site on his list, This class sets opens that heritage site as session
     * So, whatever happens from now on is related to that particular session.
     */
    private static final int CONTEXT_MODE = 0;
    private static final String SESSION_PREFERENCES = "UserSession";


    public void setSessionPreferences(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SESSION_PREFERENCES, CONTEXT_MODE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getStringSessionPreferences(Context context, String key, String default_value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES, CONTEXT_MODE);
        String preference = sharedPreferences.getString(key, default_value);
        return preference;
    }


    public void clearSessionPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES, CONTEXT_MODE);
        sharedPreferences.edit().clear().commit();
    }
}
