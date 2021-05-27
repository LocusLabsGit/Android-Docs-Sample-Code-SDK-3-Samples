package com.myco

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.locuslabs.sdk.llpublic.*
import java.util.*


class MultiVenueActivity : AppCompatActivity() {

    private lateinit var venueListRecyclerView: RecyclerView
    private lateinit var llLocusMapsFragment: LLLocusMapsFragment
    private lateinit var initializationAnimationViewBackground: View
    private lateinit var initializationAnimationView: ImageView
    private lateinit var initializationAnimationDrawable: AnimationDrawable
    private lateinit var mapContainer: ConstraintLayout
    private var mapFragmentReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_venue)

        mapContainer = findViewById(R.id.map_container)

        venueListRecyclerView = findViewById(R.id.recycler_view)
        val dividerItemDecoration = DividerItemDecoration(this,
            RecyclerView.VERTICAL)
        venueListRecyclerView.addItemDecoration(dividerItemDecoration)

        getVenueList()
        initLocusMaps()
    }

    override fun onStart() {
        super.onStart()
        llLocusMapsFragment?.onStart()
    }

    override fun onPause() {
        super.onPause()
        llLocusMapsFragment?.onPause()
    }

    override fun onStop() {
        super.onStop()
        llLocusMapsFragment?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        llLocusMapsFragment?.onStop()
    }

    override fun onResume() {
        super.onResume()
        llLocusMapsFragment?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        llLocusMapsFragment?.onDestroy()
    }

    private fun getVenueList() {

        val llVenueDatabase = LLVenueDatabase()
        llVenueDatabase.getVenueList(object : LLOnGetVenueListCallback {
            override fun successCallback(llVenueList: LLVenueList) {
                val venueListEntries = ArrayList<LLVenueListEntry>()
                for ((_, value) in llVenueList) {
                    venueListEntries.add(value)
                }
                val venueListAdapter: VenueListAdapter = VenueListAdapter(venueListEntries)
                venueListRecyclerView.adapter = venueListAdapter
            }

            override fun failureCallback(throwable: Throwable) {

                // Failed to get venue details
                Log.d("Error", "Failed to get venues:" + throwable.localizedMessage)
            }
        })
    }

    private fun initLocusMaps() {

        initializationAnimationViewBackground = findViewById(R.id.initializationAnimationViewBackground)
        initializationAnimationView = findViewById(R.id.initializationAnimationView)
        initInitializationProgressIndicator()

        llLocusMapsFragment = supportFragmentManager.findFragmentById(R.id.llLocusMapsFragment) as LLLocusMapsFragment

        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                super.onFragmentStarted(fm, f)

                if (f == llLocusMapsFragment && !mapFragmentReady) {

                    mapFragmentReady = true
                }
            }

        }, false)

        LLDependencyInjector.singleton.onInitializationProgressListener = object : LLOnProgressListener {
            override fun onProgressUpdate(fractionComplete: Double, progressDescription: String) {
                if (PROGRESS_BAR_FRACTION_FINISH == fractionComplete) {

                    hideInitializationProgressIndicator()
                    mapReady()
                }
            }
        }

        LLDependencyInjector.singleton.onLevelLoadingProgressListener = object : LLOnProgressListener {
            override fun onProgressUpdate(fractionComplete: Double, progressDescription: String) {
                updateLevelLoadingProgressIndicator(fractionComplete, progressDescription)
            }
        }

        LLDependencyInjector.singleton.onPOIURLClickedListener = object : LLOnPOIURLClickedListener {
            override fun onPOIURLClicked(url: String) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        }

        LLDependencyInjector.singleton.onPOIPhoneClickedListener = object : LLOnPOIPhoneClickedListener {
            override fun onPOIPhoneClicked(phone: String) {
                val intent = Intent(Intent.ACTION_DIAL)
                startActivity(intent)
            }
        }

        LLDependencyInjector.singleton.onWarningListener = object : LLOnWarningListener {

            override fun onWarning(throwable: Throwable) {

                // SDK warnings are sent here
            }
        }

        LLDependencyInjector.singleton.onFailureListener = object : LLOnFailureListener {
            override fun onFailure(throwable: Throwable) {

                Log.e("LOG", "stack trace: ${Log.getStackTraceString(throwable)}")
                Log.e("LOG", "stack trace cause: ${Log.getStackTraceString(throwable.cause)}")
            }
        }
    }

    public fun showVenue(venueListEntry: LLVenueListEntry) {

        if (!mapFragmentReady) return
        showInitializationProgressIndicator()
        mapContainer.visibility = View.VISIBLE
        val llVenueAssetVersion = venueListEntry.assetVersion
        val llVenueFiles = venueListEntry.files
        llLocusMapsFragment.showVenue(venueListEntry.id!!, llVenueAssetVersion, llVenueFiles)
    }

    private fun initInitializationProgressIndicator() {

        initializationAnimationView.setBackgroundResource(R.drawable.ll_navigation_loading_animation)
        initializationAnimationDrawable = initializationAnimationView.background as AnimationDrawable
        initializationAnimationDrawable.start()
        initializationAnimationDrawable.setVisible(false, false)
    }

    private fun showInitializationProgressIndicator() {

        initializationAnimationViewBackground.visibility = View.VISIBLE
        initializationAnimationView.visibility = View.VISIBLE
        initializationAnimationDrawable.setVisible(true, true)
    }

    private fun hideInitializationProgressIndicator() {

        initializationAnimationViewBackground.visibility = View.GONE
        initializationAnimationView.visibility = View.GONE
        initializationAnimationDrawable.setVisible(false, false)
    }

    private fun updateLevelLoadingProgressIndicator(fractionComplete: Double, progressDescription: String) {

        // Use this section to implement a loading progress indicator if desired
        val percentComplete = (fractionComplete * FRACTION_TO_PERCENT_CONVERSION_RATIO).toInt()
        Log.d(
            "LOG",
            "LocusMaps Android SDK Loading Progress: ${percentComplete}%\t${progressDescription}"
        )

        if (PROGRESS_BAR_FRACTION_FINISH == fractionComplete) {
            // Load complete
        }
    }

    override fun onBackPressed() {

        if (llLocusMapsFragment.hasBackStackItems()) {

            llLocusMapsFragment.popBackStack()
        }
        else if (mapContainer.visibility == View.VISIBLE) {

            getVenueList()
            mapContainer.visibility = View.INVISIBLE
        }
        else {

            super.onBackPressed()
        }
    }

    private fun mapReady() {

    }

    inner class VenueListAdapter(private val venueListEntries: ArrayList<LLVenueListEntry>) : RecyclerView.Adapter<VenueListAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView

            init {
                textView = view.findViewById<View>(R.id.title_text_view) as TextView
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.sample_row, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val venueListEntry = venueListEntries[position]
            viewHolder.textView.text = venueListEntry.name
            viewHolder.itemView.setOnClickListener {
                val venueListEntry = venueListEntries[position]
                this@MultiVenueActivity.showVenue(venueListEntry)
            }
        }

        override fun getItemCount(): Int {
            return venueListEntries.size
        }
    }
}