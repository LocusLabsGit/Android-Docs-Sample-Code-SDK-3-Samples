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

import com.locuslabs.sdk.llpublic.LLConfiguration;
import com.locuslabs.sdk.llpublic.LLDependencyInjector;
import com.locuslabs.sdk.llpublic.LLLocusMapsFragment;
import com.locuslabs.sdk.llpublic.LLNavigationDatabase;
import com.locuslabs.sdk.llpublic.LLOnFailureListener;
import com.locuslabs.sdk.llpublic.LLOnGetPOIDetailsCallback;
import com.locuslabs.sdk.llpublic.LLOnGetVenueListCallback;
import com.locuslabs.sdk.llpublic.LLOnPOIPhoneClickedListener;
import com.locuslabs.sdk.llpublic.LLOnPOIURLClickedListener;
import com.locuslabs.sdk.llpublic.LLOnProgressListener;
import com.locuslabs.sdk.llpublic.LLPOI;
import com.locuslabs.sdk.llpublic.LLPOIDatabase;
import com.locuslabs.sdk.llpublic.LLVenueDatabase;
import com.locuslabs.sdk.llpublic.LLVenueFiles;
import com.locuslabs.sdk.llpublic.LLVenueList;
import com.locuslabs.sdk.llpublic.LLVenueListEntry;

import java.util.Calendar;

import static com.locuslabs.sdk.llprivate.ConstantsKt.FRACTION_TO_PERCENT_CONVERSION_RATIO;
import static com.locuslabs.sdk.llprivate.ConstantsKt.PROGRESS_BAR_FRACTION_FINISH;

public class HeadlessModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headless_mode);

        startHeadlessMode();
    }

    private void startHeadlessMode() {

        LLVenueDatabase llVenueDatabase = new LLVenueDatabase();
        LLPOIDatabase llpoiDatabase = new LLPOIDatabase();
        LLNavigationDatabase llNavigationDatabase = new LLNavigationDatabase();

        // The SDK is now ready to perform all headless operations including getting venue data, search results, directions, etc. e.g   get details for a POI
        llpoiDatabase.getPOIDetails("lax", "870", new LLOnGetPOIDetailsCallback() {
            @Override
            public void successCallback(LLPOI llpoi) {

                String message =  "Name: " +llpoi.getName() + "\nID: " +llpoi.getId() +"\nDesc: " +llpoi.getDescription();
                AlertDialog.Builder dialog = new AlertDialog.Builder(HeadlessModeActivity.this);
                dialog.setMessage(message);
                dialog.setTitle("Got POI details in headless mode");
                dialog.setPositiveButton("OK", null);
                dialog.create().show();
            }

            @Override
            public void failureCallback(Throwable throwable) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}