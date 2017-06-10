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
import android.os.ServiceManager;
import android.os.RemoteException;
import device.scanner.DecodeResult;
import device.scanner.IScannerService;
import device.scanner.ScannerService;



public class ScannerListener extends BroadcastReceiver
{
    final private static String LOG_TAG = "Grym/ScannerListener";
    final private static boolean verbose_ = false;
    private static long native_ptr_ = 0;
    private boolean started_ = false;

	private static IScannerService iScanner = null;
	private static DecodeResult mDecodeResult = null;
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
				try {
                   // mDecodeResult.recycle();
					iScanner.aDecodeGetResult(mDecodeResult);
                    Log.e(LOG_TAG, "Result QR!!!!!!!!!!!!!!!!!: "+ mDecodeResult.symName);
                    Log.e(LOG_TAG, "Result QR!!!!!!!!!!!!!!!!!: "+ mDecodeResult.decodeValue);
                    String s = new String("Polish");
                    result = mDecodeResult.decodeValue;
                    scannerInfoUpdate(native_ptr_, true);

				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

        }

    //! Called from C++ to notify us that the associated C++ object is being destroyed.
    public void cppDestroyed()
    {
        native_ptr_ = 0;
    }

	public synchronized boolean init() throws RemoteException {
        try
        {
            iScanner = IScannerService.Stub.asInterface(ServiceManager
                    .getService("ScannerService"));
            Log.e(LOG_TAG, "Try Init scanner  ");
            if (iScanner != null) {
                mDecodeResult = new DecodeResult();
                Log.e(LOG_TAG, "Init scanner: MP60");
                iScanner.aDecodeAPIInit();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                iScanner.aDecodeSetDecodeEnable(1);
                iScanner.aDecodeSetResultType(ScannerService.ResultType.DCD_RESULT_USERMSG);

                return true;
	        }
        }    
        catch (final Throwable e)
        {
            Log.e(LOG_TAG, "Exception while starting BatteryListener: ", e);
            return false;
        }
        return false;
	}
    private native void scannerInfoUpdate(long native_ptr, boolean code);
    private native Context getContext();

} // class ScannerListener
