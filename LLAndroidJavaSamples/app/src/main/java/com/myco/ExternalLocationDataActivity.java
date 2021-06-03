package com.myco;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.locuslabs.sdk.llpublic.LLDependencyInjector;
import com.locuslabs.sdk.llpublic.LLLatLng;
import com.locuslabs.sdk.llpublic.LLLocusMapsFragment;
import com.locuslabs.sdk.llpublic.LLOnFailureListener;
import com.locuslabs.sdk.llpublic.LLOnGetVenueListCallback;
import com.locuslabs.sdk.llpublic.LLOnPOIPhoneClickedListener;
import com.locuslabs.sdk.llpublic.LLOnPOIURLClickedListener;
import com.locuslabs.sdk.llpublic.LLOnProgressListener;
import com.locuslabs.sdk.llpublic.LLOnWarningListener;
import com.locuslabs.sdk.llpublic.LLVenueDatabase;
import com.locuslabs.sdk.llpublic.LLVenueFiles;
import com.locuslabs.sdk.llpublic.LLVenueList;
import com.locuslabs.sdk.llpublic.LLVenueListEntry;

import static com.locuslabs.sdk.llpublic.LLConstantsKt.FRACTION_TO_PERCENT_CONVERSION_RATIO;
import static com.locuslabs.sdk.llpublic.LLConstantsKt.PROGRESS_BAR_FRACTION_FINISH;

public class ExternalLocationDataActivity extends AppCompatActivity {

    private LLLocusMapsFragment llLocusMapsFragment;
    private View initializationAnimationViewBackground;
    private ImageView initializationAnimationView;
    private AnimationDrawable initializationAnimationDrawable;
    private boolean showVenueCalled;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_map);

        // Reference views
        initializationAnimationViewBackground = findViewById(R.id.initializationAnimationViewBackground);
        initializationAnimationView = findViewById(R.id.initializationAnimationView);

        initLocusMaps();
        initInitializationProgressIndicator();
        showInitializationProgressIndicator();
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (llLocusMapsFragment != null) llLocusMapsFragment.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (llLocusMapsFragment != null) llLocusMapsFragment.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (llLocusMapsFragment != null) llLocusMapsFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();
        if (llLocusMapsFragment != null) llLocusMapsFragment.onLowMemory();
    }

    @Override
    public void onPause() {

        super.onPause();
        if (llLocusMapsFragment != null) llLocusMapsFragment.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (llLocusMapsFragment != null) llLocusMapsFragment.onResume();
    }

    private void initLocusMaps() {

        llLocusMapsFragment = (LLLocusMapsFragment) getSupportFragmentManager().findFragmentById(R.id.llLocusMapsFragment);

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentStarted(fm, f);

                if (f == llLocusMapsFragment && !showVenueCalled) {

                    showVenueCalled = true;
                    showVenue();
                }
            }
        }, false);

        LLDependencyInjector.Companion.getSingleton().setOnInitializationProgressListener(
                new LLOnProgressListener() {
                    @Override
                    public void onProgressUpdate(double fractionComplete, String progressDescription) {

                        // Map Ready
                        if (PROGRESS_BAR_FRACTION_FINISH == fractionComplete) {

                            hideInitializationProgressIndicator();
                            mapReady();
                        }
                    }
                }
        );

        LLDependencyInjector.Companion.getSingleton().setOnLevelLoadingProgressListener(
                new LLOnProgressListener() {
                    @Override
                    public void onProgressUpdate(double fractionComplete, String progressDescription) {
                        updateLevelLoadingProgressIndicator(fractionComplete, progressDescription);
                    }
                }
        );

        LLDependencyInjector.Companion.getSingleton().setOnPOIURLClickedListener(
                new LLOnPOIURLClickedListener() {
                    @Override
                    public void onPOIURLClicked(String url) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                }
        );

        LLDependencyInjector.Companion.getSingleton().setOnPOIPhoneClickedListener(
                new LLOnPOIPhoneClickedListener() {
                    @Override
                    public void onPOIPhoneClicked(String phone) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(phone));
                        startActivity(intent);
                    }
                }
        );

        LLDependencyInjector.Companion.getSingleton().setOnWarningListener(new LLOnWarningListener() {
            @Override
            public void onWarning(Throwable throwable) {

                // SDK warnings are sent here
            }
        });

        LLDependencyInjector.Companion.getSingleton().setOnFailureListener(new LLOnFailureListener() {
            @Override
            public void onFailure(Throwable throwable) {

                // SDK fatal errors are sent here
                Log.e("LOG", "stack trace: " + Log.getStackTraceString(throwable));
                Log.e("LOG", "stack trace cause: " + Log.getStackTraceString(throwable.getCause()));
            }
        });
    }

    private void showVenue() {

        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();

        llVenueDatabase.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(LLVenueList llVenueList) {

                String venueID = "lax";

                LLVenueListEntry venueListEntry = llVenueList.get(venueID);
                if (venueListEntry == null)  {

                    // A venue loading error occurred
                    return;
                }

                String llVenueAssetVersion = venueListEntry.getAssetVersion();
                LLVenueFiles llVenueFiles = venueListEntry.getFiles();

                llLocusMapsFragment.showVenue(venueID, llVenueAssetVersion, llVenueFiles);
            }

            @Override
            public void failureCallback(Throwable throwable) {

                // Failed to get venue details
            }
        });
    }

    private void initInitializationProgressIndicator() {

        initializationAnimationView.setBackgroundResource(R.drawable.ll_navigation_loading_animation);
        initializationAnimationDrawable = (AnimationDrawable) initializationAnimationView.getBackground();
        initializationAnimationDrawable.start();
        initializationAnimationDrawable.setVisible(false, false);
    }

    private void showInitializationProgressIndicator() {

        initializationAnimationViewBackground.setVisibility(View.VISIBLE);
        initializationAnimationView.setVisibility(View.VISIBLE);
        initializationAnimationDrawable.setVisible(true, true);
    }

    private void hideInitializationProgressIndicator() {

        initializationAnimationViewBackground.setVisibility(View.GONE);
        initializationAnimationView.setVisibility(View.GONE);
        initializationAnimationDrawable.setVisible(false, false);
    }

    private void updateLevelLoadingProgressIndicator(double fractionComplete, String progressDescription) {

        // Use this section to implement a loading progress indicator if desired
        int percentComplete = (int) (fractionComplete * FRACTION_TO_PERCENT_CONVERSION_RATIO);

        Log.d("LOG", "LocusMaps Android SDK Level Loading Progress: " + percentComplete +"\t" + progressDescription);

        if (PROGRESS_BAR_FRACTION_FINISH == fractionComplete) {

            // Load complete
        }
    }

    @Override
    public void onBackPressed() {

        if (llLocusMapsFragment.hasBackStackItems()) {

            llLocusMapsFragment.popBackStack();
        }
        else {

            super.onBackPressed();
        }
    }

    private void mapReady() {

        // This is the appropriate place to take most actions that affect the map
        mockExternalLocationData();
    }

    private void externalLocationUpdateReceived(double lat, double lon, String floorID, double accuracy, double heading) {

        LLLatLng latLng = new LLLatLng(lat, lon);
        String locusLabsFloorID = locusLabsFloorIDForExternalFloorID(floorID);

        llLocusMapsFragment.setCurrentLocation(latLng, locusLabsFloorID);
    }

    private String locusLabsFloorIDForExternalFloorID(String floorID) {

        // If you are not able to compile this mapping table yourself, please send us a list (help@locuslabs.com) of
        // the building names and associated floor ids as provided by your external mapping provider and we will compile the mapping table

        String locusLabsFloorID = null;

        if (floorID.equals("T48L3")) locusLabsFloorID = "lax-south-departures";

        return (locusLabsFloorID != null ? locusLabsFloorID : "");
    }

    private void hideBlueDot() {

        llLocusMapsFragment.setCurrentLocation(null, null);
    }

    private void mockExternalLocationData() {

        // Position 1 (Initial - DFS Duty Free)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                externalLocationUpdateReceived(33.941485, -118.40195, "T48L3", 0, 0);
            }
        }, 1000);

        // Position 2 (2 secs later)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                externalLocationUpdateReceived(33.941398, -118.401916, "T48L3", 0, 0);
            }
        }, 3000);

        // Position 3 (4 secs later)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                externalLocationUpdateReceived(33.941283, -118.401863, "T48L3", 0, 0);
            }
        }, 5000);

        // Position 4 (6 secs later)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                externalLocationUpdateReceived(33.941102, -118.401902, "T48L3", 0, 0);
            }
        }, 7000);

        // Position 5 (8 secs later - Destination - Gate 64B)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                externalLocationUpdateReceived(33.940908, -118.40177, "T48L3", 0, 0);
            }
        }, 9000);
    }
}