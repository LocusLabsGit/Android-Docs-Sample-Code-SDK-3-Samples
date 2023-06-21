package com.myco

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SampleListActivity : AppCompatActivity() {

    val samples: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_list)

        samples.add("Fullscreen Map")
        samples.add("Embedded Map")
        samples.add("Embedded Map (Multiple)")
        samples.add("Bundled Map")
        samples.add("Custom Actions")
        samples.add("Custom Theme")
        samples.add("Directions Show")
        samples.add("Directions - Steps & ETA")
        samples.add("External Location Data")
        samples.add("Grab")
        samples.add("Headless Mode")
        samples.add("Map Basics")
        samples.add("Markers")
        samples.add("Multi Venue")
        samples.add("POI Button")
        samples.add("POI Show")
        samples.add("Search Auto Display")
        samples.add("Search Categories")
        samples.add("Search General")
        samples.add("Search MultiTerm")
        samples.add("Search Proximity")
        samples.add("Venue Data")

        val samplesLV: ListView = findViewById(R.id.samplesLV)
        samplesLV.adapter = SamplesAdapter(this, samples)
        samplesLV.setOnItemClickListener { _, _, position, _ ->

            val sampleTitle = samples[position]

            var intent: Intent? = null

            if (sampleTitle == "Fullscreen Map") intent = Intent(this@SampleListActivity, FullscreenMapActivity::class.java)
            else if (sampleTitle == "Embedded Map") intent = Intent(this@SampleListActivity, EmbeddedMapActivity::class.java)
            else if (sampleTitle == "Embedded Map (Multiple)") intent = Intent(this@SampleListActivity, MultipleEmbeddedMapsActivity::class.java)
            else if (sampleTitle == "Bundled Map") intent = Intent(this@SampleListActivity, BundledMapActivity::class.java)
            else if (sampleTitle == "Custom Actions") intent = Intent(this@SampleListActivity, CustomActionsActivity::class.java)
            else if (sampleTitle == "Custom Theme") intent = Intent(this@SampleListActivity, CustomThemeActivity::class.java)
            else if (sampleTitle == "Directions Show") intent = Intent(this@SampleListActivity, DirectionsShowActivity::class.java)
            else if (sampleTitle == "Directions - Steps & ETA") intent = Intent(this@SampleListActivity, DirectionsStepsETAActivity::class.java)
            else if (sampleTitle == "Grab") intent = Intent(this@SampleListActivity, GrabMapActivity::class.java)
            else if (sampleTitle == "Headless Mode") intent = Intent(this@SampleListActivity, HeadlessModeActivity::class.java)
            else if (sampleTitle == "Map Basics") intent = Intent(this@SampleListActivity, MapBasicsActivity::class.java)
            else if (sampleTitle == "Markers") intent = Intent(this@SampleListActivity, MarkersActivity::class.java)
            else if (sampleTitle == "POI Button") intent = Intent(this@SampleListActivity, POIButtonActivity::class.java)
            else if (sampleTitle == "POI Show") intent = Intent(this@SampleListActivity, POIShowActivity::class.java)
            else if (sampleTitle == "Search Auto Display") intent = Intent(this@SampleListActivity, SearchAutoDisplayActivity::class.java)
            else if (sampleTitle == "Search Categories") intent = Intent(this@SampleListActivity, SearchCategoriesActivity::class.java)
            else if (sampleTitle == "Search General") intent = Intent(this@SampleListActivity, SearchGeneralActivity::class.java)
            else if (sampleTitle == "Search MultiTerm") intent = Intent(this@SampleListActivity, SearchMultiTermActivity::class.java)
            else if (sampleTitle == "Search Proximity") intent = Intent(this@SampleListActivity, SearchProximityActivity::class.java)
            else if (sampleTitle == "Venue Data") intent = Intent(this@SampleListActivity, VenueDataActivity::class.java)
            else if (sampleTitle == "Multi Venue") intent = Intent(this@SampleListActivity, MultiVenueActivity::class.java)
            else if (sampleTitle == "External Location Data") intent = Intent(this@SampleListActivity, ExternalLocationDataActivity::class.java)

            startActivity(intent)
        }
    }

    class SamplesAdapter(
        private val context: Context,
        private val dataSource: ArrayList<String>
    ) : BaseAdapter() {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): String {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Get view for row item
            val rowView = inflater.inflate(R.layout.sample_row, parent, false)

            val titleTextView = rowView.findViewById(R.id.title_text_view) as TextView

            val sample = getItem(position)
            titleTextView.text = sample

            return rowView
        }
    }
}