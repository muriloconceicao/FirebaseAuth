package com.muriloconceicao.firebaseauth.signin;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.muriloconceicao.firebaseauth.R;
import com.muriloconceicao.firebaseauth.home.HomeActivity;
import com.muriloconceicao.firebaseauth.signup.SignUpActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
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
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onCreate: " + user.getUid());
            }
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

    private void checkLoginEditTexts() {
        String userEmail = editText_email.getText().toString();
        String userPassword = editText_password.getText().toString();

        if(userEmail.length() <= 0 || userPassword.length() <= 0)
            showError("Digite o email e senha.");
        else
            login(userEmail, userPassword);
    }

    private void login(String userEmail, String userPassword) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, task -> {
            if(!task.isSuccessful())
                showError("Email ou Senha inválido.");
            else {
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

}