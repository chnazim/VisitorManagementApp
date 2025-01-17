package com.bennellin.app.visitormanagementapp.tab.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bennellin.app.visitormanagementapp.Logger.Logger;
import com.bennellin.app.visitormanagementapp.R;
import com.bennellin.app.visitormanagementapp.tab.fragments.PublicDataReadingFragment;

public class EidScanActivityNew extends AppCompatActivity {

    private boolean isNFCRequired = true;
    private NfcAdapter adapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] techListsArray;
    private Tag tag;
    private int type = 5;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eid_scan_new);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        adapter = NfcAdapter.getDefaultAdapter(this);
        if (null != adapter) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
                mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                        getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            } else {
                mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                        getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
            }
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            mIntentFilters = new IntentFilter[]{ndef, tech};
            techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
        }

        loadFragment();
    }

    private void loadFragment() {
        fragment = new PublicDataReadingFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null && isNFCRequired) {
            Logger.d("onResume :: enableForegroundDispatch called");
            adapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, techListsArray);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("Calling OnNewIntent");
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Logger.d("Calling OnNewIntent tag  : " + tag);
        setTagToFragment();
    }

    private void setTagToFragment() {
        switch (type) {
            case 4:
                Logger.d("Tag found setting new tag to read public data");
//                ((CardSerialNumberFragment) fragment).setNfcMode(tag);
                break;

            case 5:
                Logger.d("Tag found setting new tag to read public data");
                ((PublicDataReadingFragment) fragment).setNfcMode(tag);
                break;
            case 7:
                Logger.d("Tag found setting new tag to read verify data");
//                ((VerifyFragment) fragment).setNfcMode(tag);
                break;
            default:
                Toast.makeText(this, "This functionality is not supported in NFC mode", Toast.LENGTH_SHORT).show();
                return;
        }
    }

}