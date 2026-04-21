package com.example.safradigital.views.lavouras;

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
import com.example.safradigital.R;
import com.example.safradigital.db.Database;

public class AddLavouraFragment extends Fragment {
    private Database db;
    private EditText inputNomeLavoura;
    Button btnSalvarLavoura;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_lavoura, container, false);
        
        db = new Database(requireContext());
        inputNomeLavoura = view.findViewById(R.id.input_nome_lavoura);
        
        btnSalvarLavoura = view.findViewById(R.id.button_salvar_Lavoura);
        btnSalvarLavoura.setOnClickListener(v -> {
            String nomeLavoura = inputNomeLavoura.getText().toString().trim();
            
            if (!nomeLavoura.isEmpty()) {
                db.addLavoura(nomeLavoura);
                Toast.makeText(getContext(), "Lavoura salva com sucesso!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else {
                inputNomeLavoura.setError("Por favor, insira o nome da lavoura");
            }
        });

        return view;
    }
}