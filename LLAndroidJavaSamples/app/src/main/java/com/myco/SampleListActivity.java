package com.myco;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

                viewHolder.titleTV = convertView.findViewById(R.id.samplesTitleTV);

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
