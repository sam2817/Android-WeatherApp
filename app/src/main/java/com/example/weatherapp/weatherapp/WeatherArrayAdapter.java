package com.example.weatherapp.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SamuelAaron on 2016-08-21.
 */

// An ArrayAdapter for displaying a List<Weather>'s elements in a ListView
public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    // Class for reusing views as list items scroll off and onto the screen
    public static class ViewHolder {
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView highTextView;
        TextView humidityTextView;
    }

    // Stores already downloaded Bitmaps for reuse
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    // Constructor to initialize superclass inherited members
    public WeatherArrayAdapter(Context context, List<Weather> forecast) {
        super(context, -1, forecast);
    }

    // Creates the custom views for the ListView's items
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get Weather object for this specified ListView position
        Weather day = getItem(position);

        ViewHolder viewHolder; // Object that reference's list item's views

        // Check for reusable ViewHolder from a ListView item that scrolled offscreen;
        // otherwise, create a new ViewHolder
        if(convertView == null) { // No reusable ViewHolder, so create one
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.conditionImageView = (ImageView) convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = (TextView) convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = (TextView) convertView.findViewById(R.id.lowTextView);
            viewHolder.highTextView = (TextView) convertView.findViewById(R.id.highTextView);
            viewHolder.humidityTextView = (TextView) convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(viewHolder);
        }
        else { // Reuse existing ViewHolder stored as the list item's tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // If weather condition icon already downloaded, use it;
        // otherwise, download icon in a separate thread
        if(bitmaps.containsKey(day.iconURL)) {
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(day.iconURL));
        }
        else {
            // Download and display weather condition image
            new LoadImageTask(viewHolder.conditionImageView).execute(day.iconURL);
        }

        // Get other data from weather object and place into views
        Context context = getContext(); // For loading String resources
        viewHolder.dayTextView.setText(context.getString(R.string.day_description, day.dayOfWeek, day.description));
        viewHolder.lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
        viewHolder.highTextView.setText(context.getString(R.string.high_temp, day.maxTemp));
        viewHolder.humidityTextView.setText(context.getString(R.string.humidity, day.humidity));

        return convertView; // return completed list item to display
    }

    // AsyncTask to load weather condition icons in a separate thread
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView; // Displays the thumbnail

        // Store ImageView on which to set the download Bitmap
        public LoadImageTask(ImageView imageView) { this.imageView = imageView; }

        // Load image; params[0] is the String URL representing the image
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]); // Create URL for image

                // Open an HttpURLConnection, get its InputStream and download the image
                connection = (HttpURLConnection) url.openConnection();

                try(InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0], bitmap); // Cache for later use
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                connection.disconnect(); // Close the HttpURLConnection
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) { imageView.setImageBitmap(bitmap); }
    }
}
