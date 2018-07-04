/*
    Offscreen Android Views library for Qt

    Authors:
    Uladzislau Vasilyeu <vasvlad@gmail.com>

    Distrbuted under The BSD License

    Copyright (c) 2017.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of the DoubleGIS, LLC nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS
    BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
    THE POSSIBILITY OF SUCH DAMAGE.
*/

package ru.dublgis.androidhelpers.mobility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import ru.dublgis.androidhelpers.Log;
import android.os.RemoteException;
import device.common.DecodeResult;
import device.common.ScanConst;
import device.sdk.ScanManager;
import com.honeywell.aidc.*;



public class ScannerListener extends BroadcastReceiver
{
    final private static String LOG_TAG = "Grym/ScannerListener";
    final private static boolean verbose_ = false;
    private static long native_ptr_ = 0;
    private boolean started_ = false;
    private static BarcodeReader myBarcodeReader;
    private AidcManager myAidcManager;


	private static ScanManager iScanner;
	private static DecodeResult mDecodeResult;
    private static String result = "";

    public ScannerListener(){
    }

    public ScannerListener(long native_ptr)
    {
        native_ptr_ = native_ptr;
    }


    String result(){
        synchronized(result){
            return result;
        }
    }

    public void onReceive(Context context, Intent intent) {
        Log.e(LOG_TAG, "Broadcast ");
        if (iScanner != null) {
            iScanner.aDecodeGetResult(mDecodeResult.recycle());
            Log.e(LOG_TAG, "Result QR!!!!!!!!!!!!!!!!!: "+ mDecodeResult.symName);
           // Log.e(LOG_TAG, "Result QR!!!!!!!!!!!!!!!!!: "+ mDecodeResult.decodeValue);
            //String s = new String("Polish");
            result = new String(mDecodeResult.decodeValue);
            scannerInfoUpdate(native_ptr_, true);
        }
    }

    public void onBarcodeEvent(final BarcodeReadEvent event) {
        
        Log.e(LOG_TAG, "Broadcast ");
        Log.e(LOG_TAG, "Result QR!!!!!!!!!!!!!!!!!: " + event.getBarcodeData());
        result = new String(event.getBarcodeData());
        scannerInfoUpdate(native_ptr_, true);

    }


    //! Called from C++ to notify us that the associated C++ object is being destroyed.
    public void cppDestroyed()
    {
        native_ptr_ = 0;
    }

    public synchronized boolean init() throws RemoteException {
        try
        {
            Log.e(LOG_TAG, "Try Init scanner  ");
            iScanner = new ScanManager();
            if (iScanner != null) {
                mDecodeResult = new DecodeResult();
                Log.e(LOG_TAG, "Init scanner: MP60");

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                iScanner.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_USERMSG);
                return true;
            }
        }
        catch (final Throwable e)
        {
            Log.e(LOG_TAG, "Exception while starting ScannerListener: ", e);
            return false;
        }
        return false;
    }

    public synchronized boolean initeda() throws RemoteException {
        try
        {
            Log.e(LOG_TAG, "Try Init scanner  ");
            AidcManager.create(this, new CreatedCallback() {

                @Override
                public void onCreated(AidcManager aidcManager) {
                    myAidcManager = aidcManager;
                    myBarcodeReader = myAidcManager.createBarcodeReader();
                    if (myBarcodeReader != null) {

                        // register bar code event listener
                        myBarcodeReader.addBarcodeListener(this);

                        // set the trigger mode to client control
                        try {
                            myBarcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                                BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
                        } catch (UnsupportedPropertyException e) {
                            Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
                        }
                        // register trigger state change listener
                        myBarcodeReader.addTriggerListener(this);
                        myBarcodeReader.claim();
                    }

                }
            });
            return true;

        }
        catch (final Throwable e)
        {
            Log.e(LOG_TAG, "Exception while starting ScannerListener: ", e);
            return false;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myBarcodeReader != null) {
            // close BarcodeReader to clean up resources.
            myBarcodeReader.release();
            myBarcodeReader.close();
            myBarcodeReader = null;
        }

        if (myAidcManager != null) {
            // close AidcManager to disconnect from the scanner service.
            // once closed, the object can no longer be used.
            myAidcManager.close();
        }
    }


    private native void scannerInfoUpdate(long native_ptr, boolean code);
    private native Context getContext();

} // class ScannerListener
