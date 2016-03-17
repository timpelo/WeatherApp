package tamk.tiko.com.weatherapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * A weather widget that can also launch the app.
 *
 * @author Janne Koskinen
 * @version 1.0
 * @since 1.7
 */
public class WeatherWidget extends AppWidgetProvider {

    /**
     * Updates the widget and launches the main app when pressed.
     *
     * @param context the context of the application.
     * @param appWidgetManager a widget manager.
     * @param appWidgetId a widget id.
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetLocation = context.getString(R.string.city);
        CharSequence widgetTemp = "";

        if (context.getString(R.string.unit).equals("1")) {
            widgetTemp = context.getString(R.string.celsius) + "℃";
        } else if (context.getString(R.string.unit).equals("2")) {
            widgetTemp = context.getString(R.string.fahrenheit) + "℉";
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.locationView, widgetLocation);
        views.setTextViewText(R.id.tempView, widgetTemp);
        views.setImageViewResource(R.id.widgetIcon, R.drawable.icon_snow);

        int id = 900;

        try {
            id = Integer.parseInt(context.getString(R.string.condition_id));
        } catch (Exception e) {
            Log.w("updateAppWidget()", "can't parse condition");
        }

        if(id >= 200 && id <= 232) {
            views.setImageViewResource(R.id.widgetIcon, R.drawable.icon_thunder);
        }
        else if(id >= 300 && id <= 321) {
            views.setImageViewResource(R.id.widgetIcon, R.drawable.icon_drizzle);
        }
        else if(id >= 500 && id <= 531) {
            views.setImageViewResource(R.id.widgetIcon, R.drawable.icon_rain);
        }
        else if(id >= 600 && id <= 622) {
            views.setImageViewResource(R.id.widgetIcon, R.drawable.icon_snow);
        }
        else if(id >= 701 && id <= 761) {
            views.setImageViewResource(R.id.widgetIcon, R.drawable.icon_mist);
        }
        else if(id == 800) {
            views.setImageViewResource(R.id.widgetIcon, R.drawable.icon_sunny);
        }
        else if(id >= 801 && id <= 804) {
            views.setImageViewResource(R.id.widgetIcon, R.drawable.icon_cloudy);
        }
        else{
            views.setImageViewResource(R.id.widgetIcon, R.drawable.icon_na);
        }

        Intent defineIntent = new Intent(context, MainActivity.class);
        //defineIntent.setClassName(context,"MainActivity");
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0 /* no requestCode */, defineIntent, 0 /* no flags */);
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Calls the uppdateAppWidget-function from all existing widgets.
     *
     * @param context the context of the application.
     * @param appWidgetManager a widget manager.
     * @param appWidgetIds an array containing widget ids.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

