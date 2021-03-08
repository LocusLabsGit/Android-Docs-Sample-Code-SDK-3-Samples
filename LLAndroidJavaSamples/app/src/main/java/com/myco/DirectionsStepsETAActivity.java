package com.myco;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.locuslabs.sdk.llpublic.LLDependencyInjector;
import com.locuslabs.sdk.llpublic.LLLocusMapsFragment;
import com.locuslabs.sdk.llpublic.LLNavAccessibilityType;
import com.locuslabs.sdk.llpublic.LLNavPath;
import com.locuslabs.sdk.llpublic.LLNavigationDatabase;
import com.locuslabs.sdk.llpublic.LLOnFailureListener;
import com.locuslabs.sdk.llpublic.LLOnGetDirectionsCallback;
import com.locuslabs.sdk.llpublic.LLOnGetVenueListCallback;
import com.locuslabs.sdk.llpublic.LLOnPOIPhoneClickedListener;
import com.locuslabs.sdk.llpublic.LLOnPOIURLClickedListener;
import com.locuslabs.sdk.llpublic.LLOnProgressListener;
import com.locuslabs.sdk.llpublic.LLSegment;
import com.locuslabs.sdk.llpublic.LLVenueDatabase;
import com.locuslabs.sdk.llpublic.LLVenueFiles;
import com.locuslabs.sdk.llpublic.LLVenueList;
import com.locuslabs.sdk.llpublic.LLVenueListEntry;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.locuslabs.sdk.llprivate.ConstantsKt.FRACTION_TO_PERCENT_CONVERSION_RATIO;
import static com.locuslabs.sdk.llprivate.ConstantsKt.PROGRESS_BAR_FRACTION_FINISH;

public class DirectionsStepsETAActivity extends AppCompatActivity {

    private LLLocusMapsFragment llLocusMapsFragment;
    private View initializationAnimationViewBackground;
    private ImageView initializationAnimationView;
    private AnimationDrawable initializationAnimationDrawable;
    private ProgressBar loadingProgressBar;
    private long loadingStartTimeInMillis;
    private boolean mapLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_map);

        // Reference views
        initializationAnimationViewBackground = findViewById(R.id.initializationAnimationViewBackground);
        initializationAnimationView = findViewById(R.id.initializationAnimationView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

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

        if (mapLoaded) return;

        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();
        llVenueDatabase.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(@NonNull LLVenueList llVenueList) {

                String llVenueID = "lax";

                LLVenueListEntry llVenueListEntry = llVenueList.get(llVenueID);

                String llVenueAssetVersion = llVenueListEntry.getAssetVersion();
                LLVenueFiles llVenueFiles = llVenueListEntry.getFiles();

                llLocusMapsFragment.showVenue(llVenueID, llVenueAssetVersion, llVenueFiles);
            }

            @Override
            public void failureCallback(@NonNull Throwable throwable) {
                // Failed to get venue list
            }
        });
    }

    private void initLocusMaps() {
        llLocusMapsFragment = (LLLocusMapsFragment) getSupportFragmentManager().findFragmentById(R.id.llLocusMapsFragment);

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


        LLDependencyInjector.Companion.getSingleton().setOnFailureListener(new LLOnFailureListener() {
            @Override
            public void onFailure(Throwable throwable) {
                Log.e("LOG", "stack trace: " + Log.getStackTraceString(throwable));
                Log.e("LOG", "stack trace cause: " + Log.getStackTraceString(throwable.getCause()));
            }
        });
    }

    private void mapReady() {

        mapLoaded = true;

        Map<String, List<String>> securityQueueTypes = new HashMap<>();
        LLNavigationDatabase navDB = new LLNavigationDatabase();

        navDB.getDirections("lax", "1025", "566", LLNavAccessibilityType.Direct, securityQueueTypes, new LLOnGetDirectionsCallback() {
            @Override
            public void successCallback(LLNavPath llNavPath) {

                String message = "ETA(secs): " +llNavPath.transitTime() +"\n\nSegments:\n\n";

                for (LLSegment segment: llNavPath.segments(Locale.getDefault())) {

                    message = message +segment.toString() +"\n\n";
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(DirectionsStepsETAActivity.this);
                dialog.setMessage(message);
                dialog.setTitle("Obtained Directions");
                dialog.setPositiveButton("OK", null);
                dialog.create().show();
            }

            @Override
            public void failureCallback(Throwable throwable) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(DirectionsStepsETAActivity.this);
                dialog.setMessage(throwable.getLocalizedMessage());
                dialog.setTitle("Directions Error");
                dialog.setPositiveButton("OK", null);
                dialog.create().show();
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
        if (0.0 == fractionComplete) {
            loadingStartTimeInMillis = Calendar.getInstance().getTimeInMillis();
        }

        int percentComplete = (int) (fractionComplete * FRACTION_TO_PERCENT_CONVERSION_RATIO);
        double timeElapsedInMillis = Calendar.getInstance().getTimeInMillis() - loadingStartTimeInMillis;

        Log.d("LOG", "LocusMaps Android SDK Level Loading Progress: " + percentComplete + "\t" + timeElapsedInMillis + "\t" + progressDescription);

        loadingProgressBar.setProgress(percentComplete);
        loadingProgressBar.setVisibility(View.VISIBLE);

        if (PROGRESS_BAR_FRACTION_FINISH == fractionComplete) {
            (new Handler(Looper.getMainLooper(), null)).postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingProgressBar.setVisibility(View.GONE);
                }
            }, 50);
        }
    }

    @Override
    public void onBackPressed() {
        if (llLocusMapsFragment.hasBackStackItems()) {
            llLocusMapsFragment.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}