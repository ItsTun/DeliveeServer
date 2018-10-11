package com.example.tunhanmyae.DeliveeServer.Service;

import com.example.tunhanmyae.DeliveeServer.Common.Common;
import com.example.tunhanmyae.DeliveeServer.model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refershedToken = FirebaseInstanceId.getInstance().getToken();
        updateToServer(refershedToken);

    }

    private void updateToServer(String refershedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token = new Token(refershedToken,true);
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
