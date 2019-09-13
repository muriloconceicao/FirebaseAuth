package com.muriloconceicao.firebaseauth.signup;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.muriloconceicao.firebaseauth.R;
import com.muriloconceicao.firebaseauth.home.HomeActivity;
import com.muriloconceicao.firebaseauth.model.User;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseFirestore firestoreDb;
    private FirebaseAuth firebaseAuth;
    private FirebaseFunctions firebaseFunctions;

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
        startFirebaseMethods();
    }

    private void checkSignUpEditTexts() {
        User user = new User();

        user.setEmail(editText_email.getText().toString());
        user.setPassword(editText_password.getText().toString());
        user.setFirstName(editText_firstname.getText().toString());
        user.setLastName(editText_lastname.getText().toString());
        String fullName = editText_firstname.getText().toString() + editText_lastname.getText().toString();
        user.setFullName(fullName);

        if(user.getEmail().length() <= 0 || user.getPassword().length() <= 0)
            showError("Digite seu email e senha.");

        fullNameExists(user);

    }

    private void fullNameExists(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", user.getFullName());

        firebaseFunctions
                .getHttpsCallable("fullNameExists")
                .call(data)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        signup(user);
                    } else {
                        try {
                            throw (FirebaseFunctionsException) Objects.requireNonNull(task.getException());
                        } catch (FirebaseFunctionsException e) {
                            FirebaseFunctionsException.Code code = e.getCode();
                            String codeStr = code.toString();
                            if(codeStr.equals("ALREADY_EXISTS"))
                                showError("Nome completo já cadastrado.");
                            else
                                showError(codeStr);
                        } catch (Exception e) {
                            showError(e.getMessage());
                        }
                    }
                });
    }

    private void signup(User user) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                createUserCollection(user);
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
                    showError(e.getMessage());
                }
            }
        });
    }

    private void createUserCollection(User user) {
        Map<String, Object> userCollection = new HashMap<>();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        userCollection.put("id", userId);
        userCollection.put("fullName", user.getFullName());
        userCollection.put("email", user.getEmail());
        userCollection.put("time", new Date().getTime());

        firestoreDb.collection("users")
                .document(user.getFullName())
                .set(userCollection)
                .addOnSuccessListener(aVoid -> showSuccess())
                .addOnFailureListener(e -> showError("Erro ao criar usuário."));
    }

    private void startFirebaseMethods() {
        firestoreDb = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFunctions = FirebaseFunctions.getInstance();
    }

    private void clearEditTexts() {
        editText_email.setText("");
        editText_password.setText("");
        editText_firstname.setText("");
        editText_lastname.setText("");
        editText_email.requestFocus();
    }

    private void showSuccess() {
        Toast.makeText(this, "Usuário criado com sucesso.", Toast.LENGTH_SHORT).show();
        clearEditTexts();
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        clearEditTexts();
    }
}