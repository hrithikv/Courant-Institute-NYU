package com.uii.academico.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.uii.academico.Utility.Utils;




public class FcmInstanceIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("TOKEN NYA : ", "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        simpanRegistrasiToken(refreshedToken);
    }

    public void  simpanRegistrasiToken (String refreshedToken) {

        // Token disimpan di Utils sharedpreference kemudian di ambil LoginActivity untuk post ke server academico
        if (Utils.sharedPreferences.contains("FcmToken")) {

           // Gak melakukan apa-apa

        } else {

            Utils.editSP.putString("FcmToken",refreshedToken);
            Utils.editSP.commit();
        }


    }


}
