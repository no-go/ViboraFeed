package de.vibora.viborafeed;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * Das ist die Activity, die die Einstellungen zeigt.
 * Sie lädt das Layout, in dem das {@link MyPreferenceFragment} enthalten ist.
 * Aussderm setzt die Activity ViboraApp.withGui auf true, wenn sie aktiv ist.
 */
public class PreferencesActivity extends AppCompatActivity {

    /**
     * Diese Methode ermöglicht das Verlassen der Activity über den Home-Button der Action Bar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_main);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        Log.d(ViboraApp.TAG, "Pref onPause");
        ViboraApp.withGui = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(ViboraApp.TAG, "Pref onResume");
        ViboraApp.withGui = true;
        super.onResume();
    }

}

