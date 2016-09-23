package de.vibora.viborafeed;

import android.app.NotificationManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Diese Activity stellt die Liste der Feeds dar. Die Liste selbst
 * ist in {@link FeedListFragment} zu finden.
 */
public class MainActivity extends AppCompatActivity {
    public Context ctx;
    private BroadcastReceiver alarmReceiver;
    private WebView webView;
    private ProgressBar progressBar;
    private UiModeManager umm;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        super.onCreateOptionsMenu(menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String msg = getString(R.string.searching) + " " + query;
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Toast.makeText(getApplicationContext(), R.string.close_search, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Toast.makeText(getApplicationContext(), R.string.start_search, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DbClear dbClear = new DbClear();
        switch (item.getItemId()) {
            case R.id.action_preferences:
                Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.action_delNotifies:
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
                nMgr.cancelAll();
                break;
            case R.id.action_readedFeeds:
                dbClear.execute(R.id.action_readedFeeds);
                break;
            case R.id.action_delFeeds:
                dbClear.execute(R.id.action_delFeeds);
                break;
            case R.id.action_additionalFeed:
                if (item.isChecked()) {
                    ViboraApp.showAdditionalFeed = false;
                    item.setChecked(false);
                    FeedListFragment fr = (FeedListFragment) getFragmentManager().findFragmentById(R.id.feedlist);
                    fr.getLoaderManager().restartLoader(0, null, fr);
                } else {
                    ViboraApp.showAdditionalFeed = true;
                    item.setChecked(true);
                    FeedListFragment fr = (FeedListFragment) getFragmentManager().findFragmentById(R.id.feedlist);
                    fr.getLoaderManager().restartLoader(0, null, fr);
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * Beinhaltet alle Start-Funktionen der App.
     * Funktionen:
     * <ul>
     *     <li>Alarm (neu) Starten</li>
     *     <li>Datenbank bereinigen (gelöschte Feeds entfernen)</li>
     *     <li>Ein BroadcastReceiver() wird registriert, um nach neuen Feeds durch den Alarm zu horchen</li>
     * </ul>
     * Außerdem wird das Icon in die ActionBar eingefügt.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(ViboraApp.TAG, "onCreate");
        ctx = this;
        setContentView(R.layout.activity_main);
        umm = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        ViboraApp.alarm.restart(this);
        new DbExpunge().execute();

        try {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setDisplayShowHomeEnabled(true);
                ab.setHomeButtonEnabled(true);
                ab.setDisplayUseLogoEnabled(true);
                ab.setLogo(R.mipmap.ic_launcher);
                ab.setTitle(" " + getString(R.string.app_name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        alarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(getString(R.string.serviceHasNews))) {
                    int countNews = intent.getIntExtra("count", 0);
                    Toast.makeText(
                            ctx,
                            getString(R.string.newFeeds) + ": " + countNews,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        };
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.serviceHasNews));
        registerReceiver(alarmReceiver, filter);
    }

    public boolean setWebView(String url) {
        if (webView == null) return false;
        webView.setWebViewClient(new MyWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(url);
        return true;
    }

    public class MyWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alarmReceiver);
    }

    @Override
    protected void onPause() {
        Log.d(ViboraApp.TAG, "onPause");
        ViboraApp.withGui = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(ViboraApp.TAG, "onResume");
        ViboraApp.withGui = true;

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean night = mPreferences.getBoolean("nightmode_use", false);
        if (night) {
            int startH = mPreferences.getInt("nightmode_use_start", ViboraApp.Config.DEFAULT_NIGHT_START);
            int stopH = mPreferences.getInt("nightmode_use_stop", ViboraApp.Config.DEFAULT_NIGHT_STOP);
            if (ViboraApp.inTimeSpan(startH, stopH) && umm.getNightMode() != UiModeManager.MODE_NIGHT_YES) {
                umm.setNightMode(UiModeManager.MODE_NIGHT_YES);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            if (!ViboraApp.inTimeSpan(startH, stopH) && umm.getNightMode() != UiModeManager.MODE_NIGHT_NO) {
                umm.setNightMode(UiModeManager.MODE_NIGHT_NO);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else {
            if (umm.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                umm.setNightMode(UiModeManager.MODE_NIGHT_NO);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
        super.onResume();
    }

    /**
     * Setzt unterschiedliche Lösch-Operationen in der DB um.
     */
    private class DbClear extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            ContentValues values = new ContentValues();
            switch (params[0]) {
                case R.id.action_delFeeds:
                    values.put(FeedContract.Feeds.COLUMN_Deleted, 1);
                    getContentResolver().update(FeedContentProvider.CONTENT_URI, values, null, null);
                    break;
                case R.id.action_readedFeeds:
                    values.put(FeedContract.Feeds.COLUMN_Isnew, 0);
                    getContentResolver().update(FeedContentProvider.CONTENT_URI, values, null, null);
                    break;
                default:
                    break;
            }
            return null;
        }
    }

    /**
     * Dient zum beseitigen von gelöschten Feeds. Achtung! Wird nur gemacht,
     * wenn man die App auch öffnet!
     */
    private class DbExpunge extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

            // Vibora Feed
            Date date = new Date();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -1 * ViboraApp.Source1.expunge);
            date = c.getTime();
            String dateStr = FeedContract.dbFriendlyDate(date);

            String where = FeedContract.Feeds.COLUMN_Date + "<? and "
                    + FeedContract.Feeds.COLUMN_Deleted + "=? and "
                    + FeedContract.Feeds.COLUMN_Source + "=?";
            getContentResolver().delete(
                    FeedContentProvider.CONTENT_URI,
                    where,
                    new String[]{dateStr, "1", ViboraApp.Source1.number}
            );

            // Feed, den man selbst einstellt
            date = new Date();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -1 * ViboraApp.Source2.expunge);
            date = c.getTime();
            dateStr = FeedContract.dbFriendlyDate(date);

            where = FeedContract.Feeds.COLUMN_Date + "<? and "
                    + FeedContract.Feeds.COLUMN_Deleted + "=? and "
                    + FeedContract.Feeds.COLUMN_Source + "=?";
            getContentResolver().delete(
                    FeedContentProvider.CONTENT_URI,
                    where,
                    new String[]{dateStr, "1", ViboraApp.Source2.number}
            );
            return null;
        }
    }

    /**
     * Macht einen Dialog, wenn man die App verlassen will.
     */
    @Override
    public void onBackPressed() {
        QuitDialogFragment dialog = new QuitDialogFragment();
        dialog.show(getSupportFragmentManager(), "Dialog");
    }

    public void onUserExit() {
        super.onBackPressed();
    }
}
