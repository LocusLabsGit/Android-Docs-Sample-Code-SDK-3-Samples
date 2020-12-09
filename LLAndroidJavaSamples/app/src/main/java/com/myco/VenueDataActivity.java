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

import com.locuslabs.sdk.llprivate.Building;
import com.locuslabs.sdk.llprivate.Level;
import com.locuslabs.sdk.llpublic.LLBuilding;
import com.locuslabs.sdk.llpublic.LLDependencyInjector;
import com.locuslabs.sdk.llpublic.LLLevel;
import com.locuslabs.sdk.llpublic.LLLocusMapsFragment;
import com.locuslabs.sdk.llpublic.LLOnFailureListener;
import com.locuslabs.sdk.llpublic.LLOnGetPOIDetailsCallback;
import com.locuslabs.sdk.llpublic.LLOnGetVenueDetailsCallback;
import com.locuslabs.sdk.llpublic.LLOnGetVenueListCallback;
import com.locuslabs.sdk.llpublic.LLOnPOIPhoneClickedListener;
import com.locuslabs.sdk.llpublic.LLOnPOIURLClickedListener;
import com.locuslabs.sdk.llpublic.LLOnProgressListener;
import com.locuslabs.sdk.llpublic.LLPOI;
import com.locuslabs.sdk.llpublic.LLPOIDatabase;
import com.locuslabs.sdk.llpublic.LLVenue;
import com.locuslabs.sdk.llpublic.LLVenueDatabase;
import com.locuslabs.sdk.llpublic.LLVenueFiles;
import com.locuslabs.sdk.llpublic.LLVenueList;
import com.locuslabs.sdk.llpublic.LLVenueListEntry;

import java.util.Calendar;

import static com.locuslabs.sdk.llprivate.ConstantsKt.FRACTION_TO_PERCENT_CONVERSION_RATIO;
import static com.locuslabs.sdk.llprivate.ConstantsKt.PROGRESS_BAR_FRACTION_FINISH;

public class VenueDataActivity extends AppCompatActivity {

    private LLLocusMapsFragment llLocusMapsFragment;
    private View initializationAnimationViewBackground;
    private ImageView initializationAnimationView;
    private AnimationDrawable initializationAnimationDrawable;
    private ProgressBar loadingProgressBar;
    private long loadingStartTimeInMillis;

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
    protected void onResume() {
        super.onResume();

        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();
        llVenueDatabase.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(@NonNull LLVenueList llVenueList) {

                String llVenueID = "lax";

                LLVenueListEntry llVenueListEntry = llVenueList.get(llVenueID);

                String llVenueAssetVersion = llVenueListEntry.getAssetVersion();
                LLVenueFiles llVenueFiles = llVenueListEntry.getFiles();

                llLocusMapsFragment.loadVenue(llVenueID, llVenueAssetVersion, llVenueFiles);
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

        getVenues();
        getVenueDetails("lax");
    }

    private void getVenues() {

        LLVenueDatabase venueDB = new LLVenueDatabase();
        venueDB.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(LLVenueList llVenueList) {

                String message = "";

                for (Object venueID: llVenueList.getKeys()) {

                    LLVenueListEntry  venueListEntry = (LLVenueListEntry) llVenueList.get(venueID);
                    message = message +venueListEntry.getName() +"\n";
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(VenueDataActivity.this);
                dialog.setMessage(message);
                dialog.setTitle("Account venues: (" +String.valueOf(llVenueList.size()) +")");
                dialog.setPositiveButton("OK", null);
                dialog.create().show();
            }

            @Override
            public void failureCallback(Throwable throwable) {

            }
        });
    }

    private void getVenueDetails(String venueID) {

        LLVenueDatabase venueDB = new LLVenueDatabase();
        venueDB.getVenueDetails(venueID, new LLOnGetVenueDetailsCallback() {
            @Override
            public void successCallback(LLVenue llVenue) {

                String message = "";

                for (Building building: llVenue.getBuildings()) {

                    message = message +"Building name: " +building.getName() +"\nBuilding id: " +building.getId() +"\n";

                    for (Level level: building.getLevels()) {

                        message = message +"Floor level: " +String.valueOf(level.getOrdinal()) +"\nFloor id: " +level.getId() +"\n";
                    }

                    message = message +"\n";
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(VenueDataActivity.this);
                dialog.setMessage(message);
                dialog.setTitle("LAX venue details:");
                dialog.setPositiveButton("OK", null);
                dialog.create().show();
            }

            @Override
            public void failureCallback(Throwable throwable) {

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