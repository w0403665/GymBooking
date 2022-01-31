//gmail account username: CapstoneRedGroup
//pw: INFT3000

package com.example.bookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button btn_welcome;

    EditText et_email,et_password;
    Button btn_login, btn_create_account;
    boolean validInput =true;
    FirebaseAuth fbAuth;
    FirebaseFirestore fbStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_welcome = findViewById(R.id.btn_login);


        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_create_account = findViewById(R.id.btn_go_reg);

        //instance the db and auth
        fbStore = FirebaseFirestore.getInstance();
        fbAuth = FirebaseAuth.getInstance();




        btn_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //make sure user enters email and password
                checkForInput(et_email);
                checkForInput(et_password);

                if(validInput){
                    //login based on username and password -- we can possibly save the email to use for creating appointments
                    fbAuth.signInWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            //check for admin
                            checkAccessLevel(authResult.getUser().getUid());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
        });
    }

    //check to make sure user put in input
    public boolean checkForInput(EditText input){
        if(input.getText().toString().isEmpty()){
            input.setText("Input is required!");
            validInput = false;
        }else{
            validInput = true;
        }
        return validInput;
    }


    private void checkAccessLevel(String uid) {

        DocumentReference docRef = fbStore.collection("Users").document(uid);  //find user by user id

        //extract data from the document
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {  //request snapshot which contains the data of the user id so user name, email, password etc.
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //if successful at retrieval --
                Log.d("TAG", "onSuccess: " + documentSnapshot.getData());

                //get user level
                if(documentSnapshot.getString("isAdmin") != null){  //if isAdmin is present, user is admin

                    //send to admin activity
                    startActivity(new Intent(getApplicationContext(), Admin.class));

                    //finish this activity
                    finish();
                }

                if(documentSnapshot.getString("isUser") != null){

                    //send to main dashboard activity
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    //finish this activity
                    finish();
                }

            }
        });
    }


//    //this auto log in user, need to pass it down other credentials current can only send both user/admin to same activity
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        //this is so out app automatically logs in if you have logged in before
//        if(FirebaseAuth.getInstance().getCurrentUser() != null){
//
//
//            startActivity(new Intent(getApplicationContext(),Dashboard.class));  //send to dashboard
//            finish();
//        }
//    }
}
