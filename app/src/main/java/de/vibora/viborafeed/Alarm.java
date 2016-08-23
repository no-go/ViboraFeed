package de.vibora.viborafeed;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import org.w3c.dom.Document;

import java.util.Random;

/**
 * Der Alarm, der früher einmal ein Service war.
 * Es sieht/sah so aus, als könne man hier nicht auf {@link ViboraApp#getContextOfApplication()}
 * zugreifen.
 */
public class Alarm extends BroadcastReceiver {
    private SharedPreferences _pref;

    @Override
    public void onReceive(Context context, Intent intent) {

        AsyncTask<Context, Void, Void> asyncTask = new AsyncTask<Context, Void, Void>() {
            @Override
            protected Void doInBackground(Context... contexts) {
                Context ctx = contexts[0];
                _pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                Refresher refresher = Refresher.ME(ctx);

                if (! refresher.isOnline()) {
                    Log.w(ViboraApp.TAG, "Retry alarm in seconds: " + ViboraApp.Config.RETRYSEC_AFTER_OFFLINE);
                    ViboraApp.alarm.retry(ctx, ViboraApp.Config.RETRYSEC_AFTER_OFFLINE);
                    return null;
                }

                String rssurl1 = ViboraApp.Source1.path;
                int expunge1 = ViboraApp.Source1.expunge;

                String rssurl2 = _pref.getString("rss_url", ViboraApp.Source2.path);
                int expunge2 = ViboraApp.Source2.expunge;

                refresher._newFeeds.clear();
                Document doc = refresher.getDoc(rssurl1, expunge1);
                refresher.insertToDb(doc,expunge1, ViboraApp.Source1.id);

                if (!rssurl2.equals("")) {
                    doc = refresher.getDoc(rssurl2, expunge2);
                    refresher.insertToDb(doc, expunge2, ViboraApp.Source2.id);
                }
                refresher.sortFeeds();
                if (refresher._newFeeds.size() > 0) {

                    Intent notificationIntent = new Intent(ctx, MainActivity.class);
                    notificationIntent.setFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );
                    PendingIntent pi = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);

                    if (ViboraApp.withGui) {
                        refresher.makeNotify(pi);
                    } else {
                        refresher.makeNotifies(pi);
                    }
                    Intent intent = new Intent(ctx.getString(R.string.serviceHasNews));
                    intent.putExtra("count", refresher._newFeeds.size());
                    ctx.sendBroadcast(intent);
                }
                return null;
            }
        };
        asyncTask.execute(context);
    }

    /**
     * Sollte keine Verbindung bestehen, wird der Alarm in RETRYSEC_AFTER_OFFLINE Sekunden erneut
     * aufgerufen.
     *
     * @param context the context
     * @param sec Sekunden bis der neue Versuch starten soll
     * @see ViboraApp.Config
     */
    public void retry(Context context, long sec) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + sec * 1000L, pi);
    }

    /**
     * Startet den Alarm. Die Methode legt einen Alarm an, der regelmäßig einen Broadcast
     * sendet. Sollte als Zeit in den einstellungen etwas größer als 4 Minuten eingestellt
     * sein, so varriert die Alarmzeit (beim Anlegen des Alarms!!) um 3 Min.
     *
     * @param context the context
     */
    public void start(Context context) {
        _pref = PreferenceManager.getDefaultSharedPreferences(context);

        long refreshInterval = Long.parseLong(
                _pref.getString("rss_sec", ViboraApp.Config.DEFAULT_rsssec)
        ) * 1000L;

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        i.putExtra("isRetry", false);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        long mod = 0;
        if (refreshInterval >= 240000) { // more than 4 minutes
            Random r = new Random(System.currentTimeMillis());
            mod = r.nextInt(360000) - 180000; // plusminus 3min
        }
        am.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                refreshInterval,
                refreshInterval + mod,
                pi
        );
    }

    /**
     * Stop.
     *
     * @param context the context
     */
    public void stop(Context context) {
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    /**
     * Restart. Wird von {@link MainActivity#onCreate(Bundle)} genutzt, um beim Starten der
     * App nach neuen Feeds zu schauen.
     *
     * @param context the context
     */
    public void restart(Context context) {
        stop(context);
        start(context);
    }
}