package com.myco

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.locuslabs.sdk.llpublic.*
import java.util.HashMap

class MultipleEmbeddedMapsActivity : AppCompatActivity() {

    private var llLocusMapsFragment1: LLLocusMapsFragment? = null
    private var llLocusMapsFragment2: LLLocusMapsFragment? = null
    private var llLocusMapsFragment3: LLLocusMapsFragment? = null
    private var llLocusMapsFragment4: LLLocusMapsFragment? = null
    private var llLocusMapsFragment5: LLLocusMapsFragment? = null

    private var showVenue1Called = false
    private var showVenue2Called = false
    private var showVenue3Called = false
    private var showVenue4Called = false
    private var showVenue5Called = false

    private var venueID = "lax"
    private var loadCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.multiple_embedded_maps)

        LLConfiguration.singleton.hideMapControls = true
        LLConfiguration.singleton.hideDirectionsSummaryControls = true
        LLConfiguration.singleton.hideDirectionsSummaryAccessibilityControls = true

        llLocusMapsFragment1 =supportFragmentManager.findFragmentById(R.id.llLocusMapsFragment1) as LLLocusMapsFragment?
        llLocusMapsFragment2 =supportFragmentManager.findFragmentById(R.id.llLocusMapsFragment2) as LLLocusMapsFragment?
        llLocusMapsFragment3 =supportFragmentManager.findFragmentById(R.id.llLocusMapsFragment3) as LLLocusMapsFragment?
        llLocusMapsFragment4 =supportFragmentManager.findFragmentById(R.id.llLocusMapsFragment4) as LLLocusMapsFragment?
        llLocusMapsFragment5 =supportFragmentManager.findFragmentById(R.id.llLocusMapsFragment5) as LLLocusMapsFragment?

        initLocusMaps()
    }

    private fun initLocusMaps() {

        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                super.onFragmentStarted(fm, f)

                if (f == llLocusMapsFragment1 && !showVenue1Called) {

                    showVenue1Called = true
                    showVenue1()
                }
                else if (f == llLocusMapsFragment2 && !showVenue2Called) {

                    showVenue2Called = true
                    showVenue2()
                }
                else if (f == llLocusMapsFragment3 && !showVenue3Called) {

                    showVenue3Called = true
                    showVenue3()
                }
                else if (f == llLocusMapsFragment4 && !showVenue4Called) {

                    showVenue4Called = true
                    showVenue4()
                }
                else if (f == llLocusMapsFragment5 && !showVenue5Called) {

                    showVenue5Called = true
                    showVenue5()
                }
            }
        }, false)

        LLDependencyInjector.singleton.onInitializationProgressListener = object :
            LLOnProgressListener {
            override fun onProgressUpdate(fractionComplete: Double, progressDescription: String) {
                if (PROGRESS_BAR_FRACTION_FINISH == fractionComplete) {

                    loadCount++

                    if (loadCount == 5) { // 5 is the number of embedded maps

                        mapsReady()
                    }
                }
            }
        }
    }

    private fun showVenue1() {

        val llVenueDatabase = LLVenueDatabase()
        llVenueDatabase.getVenueList(object : LLOnGetVenueListCallback {
            override fun successCallback(llVenueList: LLVenueList) {

                val venueListEntry1 = llVenueList[venueID]
                if (venueListEntry1 == null) {
                    Log.e("error", "venueListEntry1 null")
                    return
                }
                val llVenueAssetVersion1 = venueListEntry1.assetVersion
                val llVenueFiles1 = venueListEntry1.files
                llLocusMapsFragment1!!.showVenue(venueID, llVenueAssetVersion1, llVenueFiles1)
            }

            override fun failureCallback(throwable: Throwable) {
                Log.d("error", "getVenueList1 failureCallback $throwable")
            }
        })

    }

    private fun showVenue2() {

        val llVenueDatabase = LLVenueDatabase()
        llVenueDatabase.getVenueList(object : LLOnGetVenueListCallback {
            override fun successCallback(llVenueList: LLVenueList) {

                val venueListEntry1 = llVenueList[venueID]
                if (venueListEntry1 == null) {
                    Log.e("error", "venueListEntry1 null")
                    return
                }
                val llVenueAssetVersion1 = venueListEntry1.assetVersion
                val llVenueFiles1 = venueListEntry1.files
                llLocusMapsFragment2!!.showVenue(venueID, llVenueAssetVersion1, llVenueFiles1)
            }

            override fun failureCallback(throwable: Throwable) {
                Log.d("error", "getVenueList1 failureCallback $throwable")
            }
        })
    }

    private fun showVenue3() {

        val llVenueDatabase = LLVenueDatabase()
        llVenueDatabase.getVenueList(object : LLOnGetVenueListCallback {
            override fun successCallback(llVenueList: LLVenueList) {

                val venueListEntry1 = llVenueList[venueID]
                if (venueListEntry1 == null) {
                    Log.e("error", "venueListEntry1 null")
                    return
                }
                val llVenueAssetVersion1 = venueListEntry1.assetVersion
                val llVenueFiles1 = venueListEntry1.files
                llLocusMapsFragment3!!.showVenue(venueID, llVenueAssetVersion1, llVenueFiles1)
            }

            override fun failureCallback(throwable: Throwable) {
                Log.d("error", "getVenueList1 failureCallback $throwable")
            }
        })
    }

    private fun showVenue4() {

        val llVenueDatabase = LLVenueDatabase()
        llVenueDatabase.getVenueList(object : LLOnGetVenueListCallback {
            override fun successCallback(llVenueList: LLVenueList) {

                val venueListEntry1 = llVenueList[venueID]
                if (venueListEntry1 == null) {
                    Log.e("error", "venueListEntry1 null")
                    return
                }
                val llVenueAssetVersion1 = venueListEntry1.assetVersion
                val llVenueFiles1 = venueListEntry1.files
                llLocusMapsFragment4!!.showVenue(venueID, llVenueAssetVersion1, llVenueFiles1)
            }

            override fun failureCallback(throwable: Throwable) {
                Log.d("error", "getVenueList1 failureCallback $throwable")
            }
        })
    }

    private fun showVenue5() {

        val llVenueDatabase = LLVenueDatabase()
        llVenueDatabase.getVenueList(object : LLOnGetVenueListCallback {
            override fun successCallback(llVenueList: LLVenueList) {

                val venueListEntry1 = llVenueList[venueID]
                if (venueListEntry1 == null) {
                    Log.e("error", "venueListEntry1 null")
                    return
                }
                val llVenueAssetVersion1 = venueListEntry1.assetVersion
                val llVenueFiles1 = venueListEntry1.files
                llLocusMapsFragment5!!.showVenue(venueID, llVenueAssetVersion1, llVenueFiles1)
            }

            override fun failureCallback(throwable: Throwable) {
                Log.d("error", "getVenueList1 failureCallback $throwable")
            }
        })
    }

    private fun mapsReady() {

        val securityQueueTypes: Map<String, List<String>> = HashMap()
        val startPoint: LLNavigationPoint = LLNavigationPointForPOI("1025")
        val endPoint: LLNavigationPoint = LLNavigationPointForPOI("566")

        llLocusMapsFragment1!!.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes)
        llLocusMapsFragment2!!.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes)
        llLocusMapsFragment3!!.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes)
        llLocusMapsFragment4!!.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes)
        llLocusMapsFragment5!!.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes)
    }
}