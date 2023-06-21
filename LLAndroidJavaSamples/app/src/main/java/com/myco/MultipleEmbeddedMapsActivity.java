package com.myco;

import static com.locuslabs.sdk.llpublic.LLConstantsKt.PROGRESS_BAR_FRACTION_FINISH;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.locuslabs.sdk.llpublic.LLConfiguration;
import com.locuslabs.sdk.llpublic.LLDependencyInjector;
import com.locuslabs.sdk.llpublic.LLLocusMapsFragment;
import com.locuslabs.sdk.llpublic.LLNavAccessibilityType;
import com.locuslabs.sdk.llpublic.LLNavigationPoint;
import com.locuslabs.sdk.llpublic.LLNavigationPointForPOI;
import com.locuslabs.sdk.llpublic.LLOnGetVenueListCallback;
import com.locuslabs.sdk.llpublic.LLOnProgressListener;
import com.locuslabs.sdk.llpublic.LLVenueDatabase;
import com.locuslabs.sdk.llpublic.LLVenueFiles;
import com.locuslabs.sdk.llpublic.LLVenueList;
import com.locuslabs.sdk.llpublic.LLVenueListEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleEmbeddedMapsActivity extends AppCompatActivity {

    private LLLocusMapsFragment llLocusMapsFragment1;
    private LLLocusMapsFragment llLocusMapsFragment2;
    private LLLocusMapsFragment llLocusMapsFragment3;
    private LLLocusMapsFragment llLocusMapsFragment4;
    private LLLocusMapsFragment llLocusMapsFragment5;

    private boolean showVenue1Called;
    private boolean showVenue2Called;
    private boolean showVenue3Called;
    private boolean showVenue4Called;
    private boolean showVenue5Called;

    private String venueID = "lax";

    private int loadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_embedded_maps);

        LLConfiguration.Companion.getSingleton().setHideMapControls(true);
        LLConfiguration.Companion.getSingleton().setHideDirectionsSummaryControls(true);
        LLConfiguration.Companion.getSingleton().setHideDirectionsSummaryAccessibilityControls(true);

        llLocusMapsFragment1 = (LLLocusMapsFragment) getSupportFragmentManager().findFragmentById(R.id.llLocusMapsFragment1);
        llLocusMapsFragment2 = (LLLocusMapsFragment) getSupportFragmentManager().findFragmentById(R.id.llLocusMapsFragment2);
        llLocusMapsFragment3 = (LLLocusMapsFragment) getSupportFragmentManager().findFragmentById(R.id.llLocusMapsFragment3);
        llLocusMapsFragment4 = (LLLocusMapsFragment) getSupportFragmentManager().findFragmentById(R.id.llLocusMapsFragment4);
        llLocusMapsFragment5 = (LLLocusMapsFragment) getSupportFragmentManager().findFragmentById(R.id.llLocusMapsFragment5);

        initLocusMaps();
    }

    private void initLocusMaps() {

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentStarted(fm, f);

                if (f == llLocusMapsFragment1 && !showVenue1Called) {

                    showVenue1Called = true;
                    showVenue1();
                }
                else if (f == llLocusMapsFragment2 && !showVenue2Called) {

                    showVenue2Called = true;
                    showVenue2();
                }
                else if (f == llLocusMapsFragment3 && !showVenue3Called) {

                    showVenue3Called = true;
                    showVenue3();
                }
                else if (f == llLocusMapsFragment4 && !showVenue4Called) {

                    showVenue4Called = true;
                    showVenue4();
                }
                else if (f == llLocusMapsFragment5 && !showVenue5Called) {

                    showVenue5Called = true;
                    showVenue5();
                }
            }
        }, false);

        LLDependencyInjector.Companion.getSingleton().setOnInitializationProgressListener(
                new LLOnProgressListener() {
                    @Override
                    public void onProgressUpdate(double fractionComplete, String progressDescription) {

                        // Map Ready
                        if (PROGRESS_BAR_FRACTION_FINISH == fractionComplete) {

                           loadCount++;
                           if (loadCount == 5) { // 5 is the number of embedded maps

                               mapsReady();
                           }
                        }
                    }
                }
        );
    }

    private void showVenue1() {

        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();

        llVenueDatabase.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(LLVenueList llVenueList) {

                LLVenueListEntry venueListEntry = llVenueList.get(venueID);
                if (venueListEntry == null)  {

                    // A venue loading error occurred
                    return;
                }

                String llVenueAssetVersion = venueListEntry.getAssetVersion();
                LLVenueFiles llVenueFiles = venueListEntry.getFiles();

                llLocusMapsFragment1.showVenue(venueID, llVenueAssetVersion, llVenueFiles);
            }

            @Override
            public void failureCallback(Throwable throwable) {

                // Failed to get venue details
            }
        });
    }

    private void showVenue2() {

        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();

        llVenueDatabase.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(LLVenueList llVenueList) {

                LLVenueListEntry venueListEntry = llVenueList.get(venueID);
                if (venueListEntry == null)  {

                    // A venue loading error occurred
                    return;
                }

                String llVenueAssetVersion = venueListEntry.getAssetVersion();
                LLVenueFiles llVenueFiles = venueListEntry.getFiles();

                llLocusMapsFragment2.showVenue(venueID, llVenueAssetVersion, llVenueFiles);
            }

            @Override
            public void failureCallback(Throwable throwable) {

                // Failed to get venue details
            }
        });
    }

    private void showVenue3() {

        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();

        llVenueDatabase.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(LLVenueList llVenueList) {

                LLVenueListEntry venueListEntry = llVenueList.get(venueID);
                if (venueListEntry == null)  {

                    // A venue loading error occurred
                    return;
                }

                String llVenueAssetVersion = venueListEntry.getAssetVersion();
                LLVenueFiles llVenueFiles = venueListEntry.getFiles();

                llLocusMapsFragment3.showVenue(venueID, llVenueAssetVersion, llVenueFiles);
            }

            @Override
            public void failureCallback(Throwable throwable) {

                // Failed to get venue details
            }
        });
    }

    private void showVenue4() {

        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();

        llVenueDatabase.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(LLVenueList llVenueList) {

                LLVenueListEntry venueListEntry = llVenueList.get(venueID);
                if (venueListEntry == null)  {

                    // A venue loading error occurred
                    return;
                }

                String llVenueAssetVersion = venueListEntry.getAssetVersion();
                LLVenueFiles llVenueFiles = venueListEntry.getFiles();

                llLocusMapsFragment4.showVenue(venueID, llVenueAssetVersion, llVenueFiles);
            }

            @Override
            public void failureCallback(Throwable throwable) {

                // Failed to get venue details
            }
        });
    }

    private void showVenue5() {

        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();

        llVenueDatabase.getVenueList(new LLOnGetVenueListCallback() {
            @Override
            public void successCallback(LLVenueList llVenueList) {

                LLVenueListEntry venueListEntry = llVenueList.get(venueID);
                if (venueListEntry == null)  {

                    // A venue loading error occurred
                    return;
                }

                String llVenueAssetVersion = venueListEntry.getAssetVersion();
                LLVenueFiles llVenueFiles = venueListEntry.getFiles();

                llLocusMapsFragment5.showVenue(venueID, llVenueAssetVersion, llVenueFiles);
            }

            @Override
            public void failureCallback(Throwable throwable) {

                // Failed to get venue details
            }
        });
    }

    private void mapsReady() {

        Map<String, List<String>> securityQueueTypes = new HashMap<>();

        LLNavigationPoint startPoint =  new LLNavigationPointForPOI("1025");
        LLNavigationPoint endPoint =  new LLNavigationPointForPOI("566");

        llLocusMapsFragment1.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes);
        llLocusMapsFragment2.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes);
        llLocusMapsFragment3.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes);
        llLocusMapsFragment4.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes);
        llLocusMapsFragment5.showDirectionsSummary(startPoint, endPoint, LLNavAccessibilityType.Direct, securityQueueTypes);
    }
}