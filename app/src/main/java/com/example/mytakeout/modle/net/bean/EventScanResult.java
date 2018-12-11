package com.example.mytakeout.modle.net.bean;

import android.net.wifi.ScanResult;

import java.util.List;

public class EventScanResult {
    private List<ScanResult> mScanResults;


    public List<ScanResult> getScanResults() {
        return mScanResults;
    }

    public void setScanResults(List<ScanResult> scanResults) {
        mScanResults = scanResults;
    }
}
