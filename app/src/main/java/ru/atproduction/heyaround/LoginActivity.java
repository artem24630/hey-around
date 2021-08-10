package ru.atproduction.heyaround;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    boolean firstTime=false;
    private EditText textViewMail,textViewPassword;
    private Button btnSing,btnReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        firstTime=false;
        mAuth = FirebaseAuth.getInstance();
        textViewMail = findViewById(R.id.mail);
        textViewPassword= findViewById(R.id.pass);
        btnSing = findViewById(R.id.singin);
        btnReg = findViewById(R.id.registr);
        btnSing.setOnClickListener(View->{
            if(textViewMail.getText().toString() == null || textViewPassword.getText() == null || textViewMail.getText().toString().matches("\\s+"))
            {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
            else {

    //Проверка пользователя в БД


                mAuth.signInWithEmailAndPassword(textViewMail.getText().toString(), textViewPassword.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("APP", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                firstTime=false;
                               updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("APP", "signInWithEmail:failure", task.getException());
                                Toast.makeText(this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                              // updateUI(null);
                            }

                            // ...
                        });


            }
        });
        btnReg.setOnClickListener(View->{
            if(textViewPassword.getText().toString().length()<6)
                Toast.makeText(this, "Password has to be at least 6 symbols", Toast.LENGTH_SHORT).show();
            else {
                mAuth.createUserWithEmailAndPassword(textViewMail.getText().toString(), textViewPassword.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("APP", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                firstTime = true;
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("APP", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        });

            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


   public void updateUI(FirebaseUser user){
        if(user != null){


            Intent intent = new Intent(LoginActivity.this,MapsActivity.class);
            intent.putExtra("isFirstTime",firstTime);
            startActivity(intent);


        }

    }
}