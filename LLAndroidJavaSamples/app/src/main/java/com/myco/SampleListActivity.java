package com.myco;

import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Date;

public class SampleListActivity extends AppCompatActivity {

    private ArrayList<String> samplesArray = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        samplesArray.add("Fullscreen Map");
        samplesArray.add("Embedded Map");
        samplesArray.add("Bundled Map");
        samplesArray.add("Custom Actions");
        samplesArray.add("Custom Theme");
        samplesArray.add("Directions Show");
        samplesArray.add("Directions - Steps & ETA");
        samplesArray.add("External Location Data");
        samplesArray.add("Grab");
        samplesArray.add("Headless Mode");
        samplesArray.add("Map Basics");
        samplesArray.add("Markers");
        samplesArray.add("Multi Venue");
        samplesArray.add("POI Button");
        samplesArray.add("POI Show");
        samplesArray.add("Search Auto Display");
        samplesArray.add("Search Categories");
        samplesArray.add("Search General");
        samplesArray.add("Search MultiTerm");
        samplesArray.add("Search Proximity");
        samplesArray.add("Venue Data");

        ListView samplesLV = findViewById(R.id.samplesLV);
        SamplesAdapter samplesAdapter = new SamplesAdapter(this);
        samplesLV.setAdapter(samplesAdapter);

        samplesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String sampleTitle = samplesArray.get(i);
                Intent intent = null;
                if (sampleTitle.equals("Fullscreen Map")) intent = new Intent(SampleListActivity.this, FullscreenMapActivity.class);
                else if (sampleTitle.equals("Embedded Map")) intent = new Intent(SampleListActivity.this, EmbeddedMapActivity.class);
                else if (sampleTitle.equals("Bundled Map")) intent = new Intent(SampleListActivity.this, BundledMapActivity.class);
                else if (sampleTitle.equals("Custom Actions")) intent = new Intent(SampleListActivity.this, CustomActionsActivity.class);
                else if (sampleTitle.equals("Custom Theme")) intent = new Intent(SampleListActivity.this, CustomThemeActivity.class);
                else if (sampleTitle.equals("Directions Show")) intent = new Intent(SampleListActivity.this, DirectionsShowActivity.class);
                else if (sampleTitle.equals("Directions - Steps & ETA")) intent = new Intent(SampleListActivity.this, DirectionsStepsETAActivity.class);
                else if (sampleTitle.equals("Grab")) intent = new Intent(SampleListActivity.this, GrabMapActivity.class);
                else if (sampleTitle.equals("Headless Mode")) intent = new Intent(SampleListActivity.this, HeadlessModeActivity.class);
                else if (sampleTitle.equals("Map Basics")) intent = new Intent(SampleListActivity.this, MapBasicsActivity.class);
                else if (sampleTitle.equals("Markers")) intent = new Intent(SampleListActivity.this, MarkersActivity.class);
                else if (sampleTitle.equals("POI Button")) intent = new Intent(SampleListActivity.this, POIButtonActivity.class);
                else if (sampleTitle.equals("POI Show")) intent = new Intent(SampleListActivity.this, POIShowActivity.class);
                else if (sampleTitle.equals("Search Auto Display")) intent = new Intent(SampleListActivity.this, SearchAutoDisplayActivity.class);
                else if (sampleTitle.equals("Search Categories")) intent = new Intent(SampleListActivity.this, SearchCategoriesActivity.class);
                else if (sampleTitle.equals("Search General")) intent = new Intent(SampleListActivity.this, SearchGeneralActivity.class);
                else if (sampleTitle.equals("Search MultiTerm")) intent = new Intent(SampleListActivity.this, SearchMultiTermActivity.class);
                else if (sampleTitle.equals("Search Proximity")) intent = new Intent(SampleListActivity.this, SearchProximityActivity.class);
                else if (sampleTitle.equals("Venue Data")) intent = new Intent(SampleListActivity.this, VenueDataActivity.class);
                else if (sampleTitle.equals("Multi Venue")) intent = new Intent(SampleListActivity.this, MultiVenueActivity.class);
                else if (sampleTitle.equals("External Location Data")) intent = new Intent(SampleListActivity.this, ExternalLocationDataActivity.class);

                startActivity(intent);

            }
        });
    }

    private class SamplesAdapter extends ArrayAdapter {

        private Context context;

        private class ViewHolder {

            TextView titleTV;
        }

        public SamplesAdapter(Context context) {

            super(context, R.layout.sample_row, samplesArray);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.sample_row, parent, false);

                viewHolder = new ViewHolder();

                viewHolder.titleTV = convertView.findViewById(R.id.title_text_view);

                convertView.setTag(viewHolder);
            }
            else {

                viewHolder = (ViewHolder) convertView.getTag();
            }

            String sampleTitle = samplesArray.get(position);
            viewHolder.titleTV.setText(sampleTitle);

            return convertView;
        }
    }
}
