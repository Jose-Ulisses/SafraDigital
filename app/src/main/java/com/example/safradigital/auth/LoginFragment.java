package com.example.safradigital.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.MainActivity;
import com.example.safradigital.R;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText editEmail, editPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
        }

        editEmail = view.findViewById(R.id.edit_email);
        editPassword = view.findViewById(R.id.edit_password);
        Button btnLogin = view.findViewById(R.id.btn_login);
        TextView txtRegister = view.findViewById(R.id.txt_register);

        btnLogin.setOnClickListener(v -> login());
        txtRegister.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).replaceFragment(new RegisterFragment());
            }
        });

        return view;
    }

    private void login() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(requireContext(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(requireContext(), "Erro ao entrar: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}