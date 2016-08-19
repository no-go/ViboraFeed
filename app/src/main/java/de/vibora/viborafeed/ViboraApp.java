package de.vibora.viborafeed;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Der Einstiegspunkt des Launchers.
 * In der Applications-Klasse befinden sich auch die initalen
 * Konfigurationen der SharedPreferences sowie einige Konstanten.
 *
 * @author Jochen Peters
 */
public class ViboraApp extends Application {

    public static class Config {
        public static final String DEFAULT_rsssec = "10800";
        public static final String DEFAULT_notifyColor = "#FF00FFFF";
        public static final String DEFAULT_notifyType = "2";
        /**
         * im Feed Text von Vibora ist leider ein total überflüssiger Inhalt enthalten,
         * der hinter dem Wort {@value #DEFAULT_lastRssWord} abgeschnitten werden muss.
         */
        public static final String DEFAULT_lastRssWord = "weiterlesen";
        /**
         * Die App ist für diese URL gemacht: {@value #DEFAULT_rssurl}
         * Ein Unit-Test testet das!!
         */
        public static final String DEFAULT_rssurl = "http://vibora.de/feed/";

        /**
         * really delete old database entries (marked as deleted)
         * older than {@value #DAYS_BEFORE_EXPUNGE} days
         */
        public static final int DAYS_BEFORE_EXPUNGE = 90;
        /**
         * sets a static image size to {@value #MAX_IMG_WIDTH}
         */
        public static final int MAX_IMG_WIDTH = 120;
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

        new PrefLoaderTask().execute();
        contextOfApplication = getApplicationContext();
        if (alarm == null) alarm = new Alarm();
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    public class PrefLoaderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mPreferences.contains("dummy");
            return null;
        }
    }
}
