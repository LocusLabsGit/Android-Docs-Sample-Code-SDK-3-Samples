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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.locuslabs.sdk.llpublic.LLConfiguration;
import com.locuslabs.sdk.llpublic.LLDependencyInjector;
import com.locuslabs.sdk.llpublic.LLLocusMapsFragment;
import com.locuslabs.sdk.llpublic.LLMapPackFinder;
import com.locuslabs.sdk.llpublic.LLOnFailureListener;
import com.locuslabs.sdk.llpublic.LLOnGetVenueListCallback;
import com.locuslabs.sdk.llpublic.LLOnPOIPhoneClickedListener;
import com.locuslabs.sdk.llpublic.LLOnPOIURLClickedListener;
import com.locuslabs.sdk.llpublic.LLOnProgressListener;
import com.locuslabs.sdk.llpublic.LLOnUnpackCallback;
import com.locuslabs.sdk.llpublic.LLVenueDatabase;
import com.locuslabs.sdk.llpublic.LLVenueFiles;
import com.locuslabs.sdk.llpublic.LLVenueList;
import com.locuslabs.sdk.llpublic.LLVenueListEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.locuslabs.sdk.llprivate.ConstantsKt.FRACTION_TO_PERCENT_CONVERSION_RATIO;
import static com.locuslabs.sdk.llprivate.ConstantsKt.PROGRESS_BAR_FRACTION_FINISH;

public class BundledMapActivity extends AppCompatActivity {

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
    protected void onResume() {

        super.onResume();
        if (mapLoaded) return;
        unpackMapPacksAndLoadVenue();
    }

    private void unpackMapPacksAndLoadVenue() {

        List<String> accountIdsForMapPacks = new ArrayList<String>();

        accountIdsForMapPacks.add(LLConfiguration.Companion.getSingleton().getAccountID());
        for (int i = 0; i < accountIdsForMapPacks.size(); i++) {
            String accountIdsForMapPack = accountIdsForMapPacks.get(i);

            LLOnUnpackCallback callback = new LLOnUnpackCallback() {
                @Override
                public void onUnpack(boolean b, Throwable throwable) {
                    if (throwable != null) {
                        Log.e("Log", "MapPack installation failed because: " +
                                throwable.getMessage());
                    } else {
                        loadVenueListThenShowVenue();
                    }
                }
            };

            LLMapPackFinder.Companion.installMapPack(accountIdsForMapPack, null, callback);
        }
    }

    private void loadVenueListThenShowVenue() {
        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();
        llVenueDatabase.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(@NonNull LLVenueList llVenueList) {

                String llVenueID = "lax";

                LLVenueListEntry llVenueListEntry = llVenueList.get(llVenueID);

                showVenue(llVenueID, llVenueListEntry);
            }

            @Override
            public void failureCallback(@NonNull Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Failed to get venue list because |" + Log.getStackTraceString(throwable) + "|", Toast.LENGTH_LONG).show();
                Log.e("Log", "Failed to get venue list because stack trace: " + Log.getStackTraceString(throwable));
                Log.e("Log", "Failed to get venue list because stack trace cause: " + Log.getStackTraceString(throwable.getCause()));
            }
        });
    }

    private void showVenue(String llVenueID, LLVenueListEntry llVenueListEntry) {

        String llVenueAssetVersion = llVenueListEntry.getAssetVersion();
        LLVenueFiles llVenueFiles = llVenueListEntry.getFiles();

        llLocusMapsFragment.showVenue(llVenueID, llVenueAssetVersion, llVenueFiles);
    }

    private void initLocusMaps() {
        llLocusMapsFragment = (LLLocusMapsFragment) getSupportFragmentManager().findFragmentById(R.id.llLocusMapsFragment);

        LLDependencyInjector.Companion.getSingleton().setOnInitializationProgressListener(
                new LLOnProgressListener() {
                    @Override
                    public void onProgressUpdate(double fractionComplete, String progressDescription) {
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
                Log.d("ccc", "Map load failed");
                Log.e("LOG", "stack trace: " + Log.getStackTraceString(throwable));
                Log.e("LOG", "stack trace cause: " + Log.getStackTraceString(throwable.getCause()));
            }
        });
    }

    private void mapReady() {

        mapLoaded = true;
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
