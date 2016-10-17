package de.vibora.viborafeed;

import android.app.Application;
import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import java.util.Calendar;

/**
 * Der Einstiegspunkt des Launchers.
 * In der Applications-Klasse befinden sich auch die initalen
 * Konfigurationen der SharedPreferences sowie einige Konstanten.
 *
 * @author Jochen Peters
 */
public class ViboraApp extends Application {
    public static boolean showAdditionalFeed = false;
    public static String query = "";

    public static class Source1 {
        /**
         * really delete old database entries (marked as deleted)
         * older than {@value #expunge} days
         */
        public static final int expunge = 90;
        public static final String number = "1";
        public static final int id = 1;
        public static final String path = "http://vibora.de/feed/";
    }

    public static class Source2 {
        public static final int expunge = 3;
        public static final String number = "2";
        public static final int id = 2;
        public static final String path = "";
    }

    public static class Config {
        public static final String DEFAULT_rsssec = "10800";
        public static final String DEFAULT_notifyColor = "#FF00FFFF";
        public static final String DEFAULT_notifyType = "2";
        public static final int DEFAULT_NIGHT_START = 18;
        public static final int DEFAULT_NIGHT_STOP = 6;
        public static final String SEARCH_HINT_COLOR = "#FFAA00";

        /**
         * im Feed Text von Vibora ist leider ein total überflüssiger Inhalt enthalten,
         * der hinter dem Wort {@value #DEFAULT_lastRssWord} abgeschnitten werden muss.
         */
        public static final String DEFAULT_lastRssWord = "weiterlesen";

        /**
         * sets a static image size to {@value #MAX_IMG_WIDTH}
         */
        public static final int MAX_IMG_WIDTH = 120;
        public static final float IMG_ROUND = 20f;
        /**
         * sollte eine Verbindung nicht zu sande kommen, wird ein neuer
         * Alarm in {@value #RETRYSEC_AFTER_OFFLINE} sec ausgelöst
         */
        public static final long RETRYSEC_AFTER_OFFLINE = 75L;
    }

    public static Alarm alarm = null;

    /**
     * So kann der {@link Refresher} erkennen, ob er nur im Hintergrund läuft.
     * Wäre withGui auf true, wird nur eine HeadUp Notifikation gezeigt.
     * An dieser Stelle wird klar, dass der Alarm <i>doch</i> auf ViboraApp zugreifen kann (?)
     */
    public static boolean withGui = false;
    public static final String TAG = ViboraApp.class.getSimpleName();
    private static Context contextOfApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        contextOfApplication = getApplicationContext();

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!mPreferences.contains("nightmode_use_start")) {
            mPreferences.edit().putInt("nightmode_use_start", Config.DEFAULT_NIGHT_START).commit();
        }
        if (!mPreferences.contains("nightmode_use_stop")) {
            mPreferences.edit().putInt("nightmode_use_stop", Config.DEFAULT_NIGHT_STOP).commit();
        }

        if (alarm == null) alarm = new Alarm();
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    public static boolean inTimeSpan(int startH, int stopH) {
        int nowH = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (startH == stopH && startH == nowH) return true;
        if (startH > stopH && (nowH <= stopH || nowH >= startH)) return true;
        if (startH < stopH && nowH >= startH && nowH <= stopH) return true;
        return false;
    }
}
