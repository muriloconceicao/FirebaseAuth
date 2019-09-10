package com.muriloconceicao.firebaseauth;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @BindView(R.id.editText_email) EditText editText_email;
    @BindView(R.id.editText_password) EditText editText_password;

    @OnClick(R.id.btn_signin)
    public void onSignInClick() {
        checkLoginEditTexts();
    }

    @OnClick(R.id.btn_signup)
    public void onSignUpClick() {
        // TODO
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null)
                Log.d(TAG, "onAuthStateChanged: " + user.getUid());
            else
                Log.d(TAG, "onAuthStateChanged: Signed_Out");
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthStateListener != null)
            firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    public void checkLoginEditTexts() {
        String userEmail = editText_email.getText().toString();
        String userPassword = editText_password.getText().toString();

        if(userEmail.length() <= 0 || userPassword.length() <= 0)
            Toast.makeText(this, "Digite seu Email e Senha.", Toast.LENGTH_SHORT).show();
        else
            login(userEmail, userPassword);
    }

    private void login(String userEmail, String userPassword) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, task -> {
            if(!task.isSuccessful())
                Toast.makeText(this, "Email ou Senha inválidos.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
        });
    }
}