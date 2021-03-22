package com.myco

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.locuslabs.sdk.llprivate.llPublicDI
import com.locuslabs.sdk.llpublic.*
import java.util.*

class DirectionsStepsETAActivity : AppCompatActivity() {

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

        llPublicDI().onInitializationProgressListener = object : LLOnProgressListener {
            override fun onProgressUpdate(fractionComplete: Double, progressDescription: String) {
                if (PROGRESS_BAR_FRACTION_FINISH == fractionComplete) {

                    hideInitializationProgressIndicator()
                    mapReady()
                }
            }
        }

        llPublicDI().onLevelLoadingProgressListener = object : LLOnProgressListener {
            override fun onProgressUpdate(fractionComplete: Double, progressDescription: String) {
                updateLevelLoadingProgressIndicator(fractionComplete, progressDescription)
            }
        }

        llPublicDI().onPOIURLClickedListener = object : LLOnPOIURLClickedListener {
            override fun onPOIURLClicked(url: String) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                this@DirectionsStepsETAActivity.startActivity(intent)
            }
        }

        llPublicDI().onPOIPhoneClickedListener = object : LLOnPOIPhoneClickedListener {
            override fun onPOIPhoneClicked(phone: String) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                this@DirectionsStepsETAActivity.startActivity(intent)
            }
        }

        llPublicDI().onWarningListener = object : LLOnWarningListener {

            override fun onWarning(throwable: Throwable) {

                // SDK warnings are sent here
            }
        }

        llPublicDI().onFailureListener = object : LLOnFailureListener {
            override fun onFailure(throwable: Throwable) {

                Log.e("LOG", "stack trace: ${Log.getStackTraceString(throwable)}")
                Log.e("LOG", "stack trace cause: ${Log.getStackTraceString(throwable.cause)}")
            }
        }
    }

    private fun showVenue() {

        val llVenueDatabase = LLVenueDatabase()

        var venueDetailsCallback = object : LLOnGetVenueDetailsCallback {

            override fun successCallback(venue: LLVenue) {

                val llVenueAssetVersion = venue.assetVersion
                val llVenueFiles = venue.venueFiles
                llLocusMapsFragment.showVenue(venue.id, llVenueAssetVersion, llVenueFiles)
            }

            override fun failureCallback(throwable: Throwable) {

                // Failed to get venue details
            }
        }

        llVenueDatabase.getVenueDetails("lax", venueDetailsCallback)
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
        val securityQueueTypes: Map<String, List<String>> = HashMap()
        val navDB = LLNavigationDatabase()

        // Note that another signature of this method takes LLNavigationPoints in place of POI IDs
        navDB.getDirections("lax", "1025", "566", LLNavAccessibilityType.Direct, securityQueueTypes, object : LLOnGetDirectionsCallback {
            override fun successCallback(llNavPath: LLNavPath) {
                var message = """ETA(secs): ${llNavPath.transitTime()}
             
             Segments:
             
             
             """.trimIndent()
                for (segment in llNavPath.segments(Locale.getDefault())) {
                    message = """
                $message$segment
                
                
                """.trimIndent()
                }
                val dialog = AlertDialog.Builder(this@DirectionsStepsETAActivity)
                dialog.setMessage(message)
                dialog.setTitle("Obtained Directions")
                dialog.setPositiveButton("OK", null)
                dialog.create().show()
            }

            override fun failureCallback(throwable: Throwable) {
                val dialog = AlertDialog.Builder(this@DirectionsStepsETAActivity)
                dialog.setMessage(throwable.localizedMessage)
                dialog.setTitle("Directions Error")
                dialog.setPositiveButton("OK", null)
                dialog.create().show()
            }
        })
    }
}