package com.example.tunhanmyae.DeliveeServer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tunhanmyae.DeliveeServer.Common.Common;
import com.example.tunhanmyae.DeliveeServer.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
    EditText edtPhone,edtPassword;
    Button btn;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPh);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPa);
        btn = (Button) findViewById(R.id.btnSing);

        db= FirebaseDatabase.getInstance();
        final DatabaseReference table_user=db.getReference("User");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);

                mDialog.setMessage("Please Wating");
                mDialog.show();
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(edtPhone.getText().toString()).exists())
                        {
                            mDialog.dismiss();
                            User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                            user.setPhone(edtPhone.getText().toString());


                           if (Boolean.parseBoolean(user.getIsstaff()))
                           {
                               if(user.getPassword().equals(edtPassword.getText().toString()))
                               {


                                  Intent i = new Intent(getApplicationContext(),Home.class);
                                  Common.currentUser = user;
                                  startActivity(i);
                                  finish();


                               }
                               else
                               {
                                   Toast.makeText(SignIn.this,"Wrogn Password!",Toast.LENGTH_SHORT).show();
                               }
                           }
                           else
                               Toast.makeText(SignIn.this,"Try with Staff Account",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(SignIn.this,"User not exit!",Toast.LENGTH_SHORT).show();
                        }
                    }




                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }


}
