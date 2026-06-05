package com.example.safradigital.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.MainActivity;
import com.example.safradigital.R;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText editEmailRegister, editPasswordRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register, container, false);

        mAuth = FirebaseAuth.getInstance();

        editEmailRegister = view.findViewById(R.id.edit_email_register);
        editPasswordRegister = view.findViewById(R.id.edit_password_register);
        Button btnRegister = view.findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> register());

        return view;
    }

    private void register() {
        String email = editEmailRegister.getText().toString().trim();
        String password = editPasswordRegister.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos para cadastrar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(requireContext(), "Senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(requireContext(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(requireContext(), "Erro ao cadastrar: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}