package com.myco

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.locuslabs.sdk.llpublic.*

class ExternalLocationDataActivity : AppCompatActivity() {

    private lateinit var llLocusMapsFragment: LLLocusMapsFragment
    private lateinit var initializationAnimationViewBackground: View
    private lateinit var initializationAnimationView: ImageView
    private lateinit var initializationAnimationDrawable: AnimationDrawable
    private var showVenueCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fullscreen_map)

        // Reference views
        initializationAnimationViewBackground = findViewById(R.id.initializationAnimationViewBackground)
        initializationAnimationView = findViewById(R.id.initializationAnimationView)

        initLocusMaps()
        initInitializationProgressIndicator()
        showInitializationProgressIndicator()
    }

    override fun onStart() {
        super.onStart()
        llLocusMapsFragment.onStart()
    }

    override fun onPause() {
        super.onPause()
        llLocusMapsFragment.onPause()
    }

    override fun onStop() {
        super.onStop()
        llLocusMapsFragment.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        llLocusMapsFragment.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        llLocusMapsFragment.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        llLocusMapsFragment.onDestroy()
    }

    private fun initLocusMaps() {

        llLocusMapsFragment = supportFragmentManager.findFragmentById(R.id.llLocusMapsFragment) as LLLocusMapsFragment

        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
                FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                super.onFragmentStarted(fm, f)

                if (f == llLocusMapsFragment && !showVenueCalled) {

                    showVenueCalled = true
                    showVenue()
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
                this@ExternalLocationDataActivity.startActivity(intent)
            }
        }

        LLDependencyInjector.singleton.onPOIPhoneClickedListener = object : LLOnPOIPhoneClickedListener {
            override fun onPOIPhoneClicked(phone: String) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                this@ExternalLocationDataActivity.startActivity(intent)
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

    private fun showVenue() {

        val llVenueDatabase = LLVenueDatabase()

        var venueListCallback = object : LLOnGetVenueListCallback {

            override fun successCallback(venueList: LLVenueList) {

                val venueID = "lax"

                val venueListEntry = venueList[venueID]
                        ?: // A venue loading error occurred
                        return

                val llVenueAssetVersion = venueListEntry.assetVersion
                val llVenueFiles = venueListEntry.files

                llLocusMapsFragment.showVenue(venueID, llVenueAssetVersion, llVenueFiles)
            }

            override fun failureCallback(throwable: Throwable) {

                // Failed to get venue details
            }
        }

        llVenueDatabase.getVenueList(venueListCallback)
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
        else {

            super.onBackPressed()
        }
    }

    private fun mapReady() {

        // This is the appropriate place to take most actions that affect the map

        // Start mocking external location// Start mocking external location
        Handler(Looper.myLooper()!!).postDelayed({ mockExternalLocationData() }, 5000)
    }

    private fun externalLocationUpdateReceived(
        lat: Double,
        lon: Double,
        floorID: String,
        accuracy: Double,
        heading: Double
    ) {
        val latLng = LLLatLng(lat, lon)
        val locusLabsFloorID = locusLabsFloorIDForExternalFloorID(floorID)
        llLocusMapsFragment.setCurrentLocation(latLng, locusLabsFloorID)
    }

    private fun locusLabsFloorIDForExternalFloorID(floorID: String): String {

        // If you are not able to compile this mapping table yourself, please send us a list (help@locuslabs.com) of
        // the building names and associated floor ids as provided by your external mapping provider and we will compile the mapping table
        var locusLabsFloorID: String? = null
        if (floorID == "T48L3") locusLabsFloorID = "lax-south-departures"
        return locusLabsFloorID ?: ""
    }

    private fun hideBlueDot() {
        llLocusMapsFragment.setCurrentLocation(null, null)
    }

    private fun mockExternalLocationData() {

        // Position 1 (Initial - DFS Duty Free)
        Handler(Looper.myLooper()!!).postDelayed({
            externalLocationUpdateReceived(
                33.941485,
                -118.40195,
                "T48L3",
                0.0,
                0.0
            )
        }, 1000)

        // Position 2 (2 secs later)
        Handler(Looper.myLooper()!!).postDelayed({
            externalLocationUpdateReceived(
                33.941398,
                -118.401916,
                "T48L3",
                0.0,
                0.0
            )
        }, 3000)

        // Position 3 (4 secs later)
        Handler(Looper.myLooper()!!).postDelayed({
            externalLocationUpdateReceived(
                33.941283,
                -118.401863,
                "T48L3",
                0.0,
                0.0
            )
        }, 5000)

        // Position 4 (6 secs later)
        Handler(Looper.myLooper()!!).postDelayed({
            externalLocationUpdateReceived(
                33.941102,
                -118.401902,
                "T48L3",
                0.0,
                0.0
            )
        }, 7000)

        // Position 5 (8 secs later - Destination - Gate 64B)
        Handler(Looper.myLooper()!!).postDelayed({
            externalLocationUpdateReceived(
                33.940908,
                -118.40177,
                "T48L3",
                0.0,
                0.0
            )
        }, 9000)
    }
}