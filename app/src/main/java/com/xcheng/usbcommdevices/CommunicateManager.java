package com.xcheng.usbcommdevices;

import android.content.Context;
import android.usbcommunicate.IXcUsbListener;
import android.usbcommunicate.XcUsbManager;
import android.util.Log;


public class CommunicateManager {

    private XcUsbManager mXcUsbManager;

    private IReceiveListener mReceiveListener;

    public CommunicateManager(Context context) {
        mXcUsbManager = (XcUsbManager) context.getSystemService(Context.XC_USB_SERVICE);
        Log.d("TAG", "CommunicateManager: mXcUsbManager = " + mXcUsbManager);
    }

    public boolean sendData(byte[] data) {
        try {
            return mXcUsbManager.sendData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setReceiveListener(IReceiveListener listener) {
        mReceiveListener = listener;
        mXcUsbManager.setReceiveListener(new IXcUsbListener.Stub() {
            @Override
            public void onReceiveData(byte[] data) {
                mReceiveListener.onReceiveData(data);
            }
        });
    }

}
