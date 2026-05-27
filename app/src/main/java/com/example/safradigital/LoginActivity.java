package com.example.safradigital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editEmail, editPassword;
    private EditText editEmailRegister , editPasswordRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView txtRegister = findViewById(R.id.txt_register);

        btnLogin.setOnClickListener(v -> login());
        txtRegister.setOnClickListener(v -> register());
    }

    private void login() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Erro ao entrar: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void register() {
        setContentView(R.layout.activity_register);

        editEmailRegister = findViewById(R.id.edit_email_register);
        editPasswordRegister = findViewById(R.id.edit_password_register);
        Button btnRegister = findViewById(R.id.btn_register);


        btnRegister.setOnClickListener(v -> {
            String emailRegister = editEmailRegister.getText().toString().trim();
            String passwordRegister = editPasswordRegister.getText().toString().trim();

            if (emailRegister.isEmpty() || passwordRegister.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos para cadastrar", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passwordRegister.length() < 6) {
                Toast.makeText(this, "Senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(emailRegister, passwordRegister)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Erro ao cadastrar: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}