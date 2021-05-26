package com.myco

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.locuslabs.sdk.llprivate.llPublicDI
import com.locuslabs.sdk.llpublic.*

class CustomActionsActivity : AppCompatActivity() {

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

        LLConfiguration.singleton.customActionsFilePathRelativeToAssetsDir = "locuslabs/LLCustomActions.json"
        initLocusMaps()
        initInitializationProgressIndicator()
        showInitializationProgressIndicator()
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
                this@CustomActionsActivity.startActivity(intent)
            }
        }

        LLDependencyInjector.singleton.onPOIPhoneClickedListener = object : LLOnPOIPhoneClickedListener {
            override fun onPOIPhoneClicked(phone: String) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                this@CustomActionsActivity.startActivity(intent)
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

    }
}