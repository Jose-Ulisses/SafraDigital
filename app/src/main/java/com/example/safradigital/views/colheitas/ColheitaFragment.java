package com.example.safradigital.views.colheitas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safradigital.R;
import com.example.safradigital.views.funcionarios.AddFuncionarioFragment;

public class ColheitaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_colheita, container, false);

        Button btnAddColheita = view.findViewById(R.id.button_add_colheita);
        btnAddColheita.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddColheitaFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}