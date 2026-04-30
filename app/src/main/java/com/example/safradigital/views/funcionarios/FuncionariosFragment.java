package com.example.safradigital.views.funcionarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;

public class FuncionariosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_funcionarios, container, false);

        View btnAddFuncionario = view.findViewById(R.id.button_add_funcionario_container);
        btnAddFuncionario.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddFuncionarioFragment())
                .addToBackStack(null)
                .commit());

        View btnViewFuncionarios = view.findViewById(R.id.button_view_funcionarios_container);
        btnViewFuncionarios.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AllFuncionariosFragment())
                .addToBackStack(null)
                .commit());

        return view;
    }
}