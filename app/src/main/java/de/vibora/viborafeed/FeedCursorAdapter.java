package de.vibora.viborafeed;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Der FeedCursorAdapter verknüpft den Daten(Bank)Cursor mit den Feldern eines Views.
 */
public class FeedCursorAdapter extends CursorAdapter {


    public FeedCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.feed, parent, false);
    }

    /**
     * Diese Methode ist so ähnlich wie <tt>CursorAdapter#getView(int, View, ViewGroup)</tt>, recycled
     * jedoch bereits zuvor genutzte Views.
     * <p>
     * Wegen Recycling sind entsprechende Else-Zweige sind nötig, da sonst Padding/Color aus einem
     * alten View genutzt/recycled wird.
     * </p><p>
     * Sollte in der Datenbank ein Bild existieren, wird ein Abstand zum Body-Text eingebaut.
     * </p>
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tt = (TextView) view.findViewById(R.id.feedTitle);
        String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedContract.Feeds.COLUMN_Title));
        if (!ViboraApp.query.equals("")) {
            tt.setText(highlight(ViboraApp.query, title));
        } else {
            tt.setText(title);
        }

        TextView td = (TextView) view.findViewById(R.id.feedDate);
        td.setText(FeedContract.getDate(cursor.getString(
                cursor.getColumnIndexOrThrow(FeedContract.Feeds.COLUMN_Date)
        )));

        TextView tb = (TextView) view.findViewById(R.id.feedBody);
        String body = FeedContract.removeHtml(cursor.getString(cursor.getColumnIndexOrThrow(FeedContract.Feeds.COLUMN_Body)));
        if (!ViboraApp.query.equals("")) {
            tb.setText(highlight(ViboraApp.query, body));
        } else {
            tb.setText(body);
        }

        tt.setPadding(10, 20,  5, 0);
        tb.setPadding(10,  0, 10, 0);

        Bitmap bmp = FeedContract.getImage(
                cursor.getBlob(cursor.getColumnIndexOrThrow(FeedContract.Feeds.COLUMN_Image))
        );
        ImageView iv = (ImageView) view.findViewById(R.id.image);
        iv.setImageBitmap(bmp);
        int source = cursor.getInt(cursor.getColumnIndexOrThrow(FeedContract.Feeds.COLUMN_Source));
        if (bmp != null) {
            iv.setPadding(20, 30, 10, 0);
        } else {
            if (source == ViboraApp.Source1.id) {
                Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                iv.setImageBitmap(largeIcon);
                iv.setPadding(20, 30, 10, 0);
            } else {
                iv.setPadding( 0, 0, 0, 0);
                tt.setPadding(20, 10,  5, 0);
                tb.setPadding(20,  0, 10, 0);
            }
        }
        int isNew = cursor.getInt(cursor.getColumnIndexOrThrow(FeedContract.Feeds.COLUMN_Flag));
        if (isNew == 0) {
            int oldTxt = ContextCompat.getColor(context, R.color.colorOldText);
            tt.setTextColor(oldTxt);
            td.setTextColor(oldTxt);
            tb.setTextColor(oldTxt);
            iv.setAlpha(0.3f);
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOld));
        } else {
            tt.setTextColor(ContextCompat.getColor(context, R.color.colorTitle));
            td.setTextColor(ContextCompat.getColor(context, R.color.colorDate));
            tb.setTextColor(ContextCompat.getColor(context, R.color.colorBody));
            iv.setAlpha(1.0f);
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground));
        }
    }

    public Spanned highlight(String key, String msg) {
        msg = msg.replaceAll(
                "((?i)"+key+")",
                "<b><font color='"+ ViboraApp.Config.SEARCH_HINT_COLOR + "'>$1</font></b>"
        );
        return FeedContract.fromHtml(msg);
    }
}
