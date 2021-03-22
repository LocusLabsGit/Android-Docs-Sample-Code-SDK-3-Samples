package com.myco

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.locuslabs.sdk.llprivate.llPublicDI
import com.locuslabs.sdk.llpublic.*
import com.locuslabs.sdk.llpublic.LLDependencyInjector.Companion.singleton

class POIButtonActivity : AppCompatActivity() {

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

        llPublicDI().poiExtraButtonHandler = object : LLPOIExtraButtonHandler {
            override fun doShowExtraButtonForPOI(llVenue: LLVenue, llpoi: LLPOI): Boolean {
                return if (llpoi.id == "870") {
                    true
                } else false
            }

            override fun getExtraButtonLabelForPOI(llVenue: LLVenue, llpoi: LLPOI): String {
                return if (llpoi.id == "870") {
                    "Custom1"
                }
                else ""
            }


            override fun getExtraButtonIconForPOI(venue: LLVenue, poi: LLPOI): Drawable {

                val markerDrawable = ContextCompat.getDrawable(this@POIButtonActivity, R.drawable.outline_circle)

                return if (poi.id == "870") {
                    markerDrawable!!
                }
                else markerDrawable!!
            }

            override fun onExtraButtonClickedListener(llVenue: LLVenue, llpoi: LLPOI) {
                if (llpoi.id == "870") {
                    Log.d("Log", "Custom1 button tapped")
                }
            }
        }

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
                this@POIButtonActivity.startActivity(intent)
            }
        }

        llPublicDI().onPOIPhoneClickedListener = object : LLOnPOIPhoneClickedListener {
            override fun onPOIPhoneClicked(phone: String) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                this@POIButtonActivity.startActivity(intent)
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

    }
}