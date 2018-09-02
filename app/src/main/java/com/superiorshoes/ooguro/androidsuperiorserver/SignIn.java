package com.superiorshoes.ooguro.androidsuperiorserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.superiorshoes.ooguro.androidsuperiorserver.Common.Common;
import com.superiorshoes.ooguro.androidsuperiorserver.Model.User;

import info.hoang8f.widget.FButton;

public class SignIn extends AppCompatActivity {

    EditText ssPhone,ssPassword;
    Button btnSignIn;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ssPhone = (MaterialEditText)findViewById(R.id.ssPhone);
        ssPassword = (MaterialEditText)findViewById(R.id.ssPassword);
        btnSignIn = (FButton)findViewById(R.id.btnSignIn);

        //Init Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignInUser(ssPhone.getText().toString(),ssPassword.getText().toString());

            }
        });
    }

    private void SignInUser(String phone, String password) {

        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Please wait....");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(localPhone).exists())
                {
                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if(Boolean.parseBoolean(user.getIsStaff()))
                    {
                        if(user.getPassword().equals(localPassword))
                        {
                            Intent login = new Intent(SignIn.this,Home.class);
                            Common.currentUser = user;
                            startActivity(login);
                            finish();
                        }
                        else
                            Toast.makeText(SignIn.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(SignIn.this, "Please login with staff account", Toast.LENGTH_SHORT).show();
                }
                else
                {
                   mDialog.dismiss();
                    Toast.makeText(SignIn.this, "User doesn't exist in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

    }
}
