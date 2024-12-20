package com.bennellin.app.visitormanagementapp.general;

import android.os.AsyncTask;

import ae.emiratesid.idcard.toolkit.ToolkitException;

public class InitializeToolkitTask extends AsyncTask<Void, Integer, String> {

    private boolean isToolkitInitialized;
    private final InitializationListener mInitializationListener;

    public InitializeToolkitTask(InitializationListener initializationListener) {
        this.mInitializationListener = initializationListener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String statusMessage = null;
        try {
            isToolkitInitialized = ConnectionController.initialize();
            statusMessage = "Toolkit Successfully initialized.";
        } catch (ToolkitException e) {
            e.printStackTrace();
            statusMessage = e.getMessage();
        }
        return statusMessage;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (null != mInitializationListener) {
            mInitializationListener.onToolkitInitialized(isToolkitInitialized, s);
        } else {
        }
    }

    public interface InitializationListener {
        void onToolkitInitialized(boolean isSuccessful, String statusMessage);
    }

}