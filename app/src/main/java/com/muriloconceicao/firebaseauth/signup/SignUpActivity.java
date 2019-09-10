package com.muriloconceicao.firebaseauth.signup;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muriloconceicao.firebaseauth.R;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseFirestore firestoreDb;
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.editText_email) EditText editText_email;
    @BindView(R.id.editText_password) EditText editText_password;
    @BindView(R.id.editText_firstname) EditText editText_firstname;
    @BindView(R.id.editText_lastname) EditText editText_lastname;

    @OnClick(R.id.btn_signup)
    public void onBtnSignUpClick() {
        checkSignUpEditTexts();
    }

    @OnClick(R.id.btn_reset)
    public void onBtnResetClick() {
        clearEditTexts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();
    }

    private void checkSignUpEditTexts() {
        String userEmail = editText_email.getText().toString();
        String userPassword = editText_password.getText().toString();
        String userFirstName = editText_firstname.getText().toString();
        String userLastName = editText_lastname.getText().toString();

        if(userEmail.length() <= 0 || userPassword.length() <= 0)
            Toast.makeText(this, "Digite seu Email e Senha.", Toast.LENGTH_SHORT).show();
        else
            signup(userEmail, userPassword, userFirstName, userLastName);
    }

    private void signup(String userEmail, String userPassword, String userFirstName, String userLastName) {
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                createUserCollection(userEmail, userFirstName, userLastName);
            }
            else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthWeakPasswordException e) {
                    editText_password.setError("A senha deve conter no minímo 6 digitos.");
                    editText_password.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    editText_email.setError("Email inválido.");
                    editText_email.requestFocus();
                } catch (FirebaseAuthUserCollisionException e) {
                    editText_email.setError("Usuário já existe.");
                    editText_email.requestFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createUserCollection(String userEmail,String userFirstName, String userLastName) {
        Map<String, Object> user = new HashMap<>();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        user.put("id", userId);
        user.put("first_name",userFirstName);
        user.put("last_name",userLastName);
        user.put("email", userEmail);

        firestoreDb.collection("users")
                .document(userEmail)
                .set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(SignUpActivity.this, "Usuário criado." , Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Falha ao criar usuário." + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearEditTexts() {
        editText_email.setText("");
        editText_password.setText("");
        editText_email.requestFocus();
    }
}