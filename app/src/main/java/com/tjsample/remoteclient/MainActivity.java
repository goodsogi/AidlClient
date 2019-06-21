package com.tjsample.remoteclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tjsample.remoteserver.IMyAidlCallbackInterface;
import com.tjsample.remoteserver.IMyAidlInterface;

public class MainActivity extends AppCompatActivity {


    private IMyAidlInterface myAidlInterface;

    /**
     * callback
     */
    private IMyAidlCallbackInterface callback = new IMyAidlCallbackInterface.Stub() {
        @Override
        public void valueChanged(long value) throws RemoteException {
           JeffLogger.log("valueChanged() / value: " + value);
        }
    };

    /**
     * service connection
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            JeffLogger.log( "onServiceConnected()");

            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);

            try {
                String msg = myAidlInterface.hello();
                JeffLogger.log( "hello() / " + msg);
                myAidlInterface.registerCallback(callback);
            } catch (RemoteException e) {
                JeffLogger.log( "remote exception: " +  e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            JeffLogger.log("onServiceDisconnected()");
            myAidlInterface = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent();
        intent.setClassName("com.tjsample.remoteserver", "com.tjsample.remoteserver.MyService");
        getApplicationContext().bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
