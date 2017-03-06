package in.ac.iiit.cvit.heritage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by HOME on 06-03-2017.
 */

public class LocaleManager {
    /**
     * This class sets and the Locale for the app
     */

    private static final String LOGTAG = "LocaleManager";
    private Locale myLocale;
    private Context context;

    public LocaleManager(Context _context){
        context = _context;

    }


    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

    }

    public void saveLocale(String lang)
    {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
//        Log.v(LOGTAG,"saving language = "+lang);

    }
    public void loadLocale()
    {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
//        Log.v(LOGTAG,"loading language = "+language);
        changeLang(language);
    }
}
