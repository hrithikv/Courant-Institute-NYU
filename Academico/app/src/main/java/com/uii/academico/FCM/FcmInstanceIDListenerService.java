package com.uii.academico.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.uii.academico.Utility.Utils;

public class FcmInstanceIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("TOKEN NYA : ", "Refreshed token: " + refreshedToken);
        simpanRegistrasiToken(refreshedToken);
    }

    public void  simpanRegistrasiToken (String refreshedToken) {

        if (Utils.sharedPreferences.contains("FcmToken")) {

        } else {

            Utils.editSP.putString("FcmToken",refreshedToken);
            Utils.editSP.commit();
        }


    }


}
