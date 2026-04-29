package com.example.safradigital.views.funcionarios;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.google.firebase.firestore.FirebaseFirestore;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

public class AddFuncionarioFragment extends Fragment {
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    EditText inputNome, inputCpf, inputTelefone, inputPix;
    Button btnSalvarFuncionario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_funcionario, container, false);
        
        inputNome = view.findViewById(R.id.input_nome_Funcionario);
        inputCpf = view.findViewById(R.id.input_cpf);
        inputTelefone = view.findViewById(R.id.input_telefone);
        inputPix = view.findViewById(R.id.input_chave_pix);
        btnSalvarFuncionario = view.findViewById(R.id.button_salvar_Funcionario);

        btnSalvarFuncionario.setOnClickListener(v -> {
            String nomeFuncionario = inputNome.getText().toString().trim();
            String cpf = inputCpf.getText().toString().trim();
            String telefone = inputTelefone.getText().toString().trim();
            String chavePix = inputPix.getText().toString().trim();

            if (nomeFuncionario.isEmpty()) {
                inputNome.setError(getString(R.string.insira_nome_funcionario));
            } else if (cpf.isEmpty()) {
                inputCpf.setError(getString(R.string.insira_cpf_funcionario));
            } else if (telefone.isEmpty()) {
                inputTelefone.setError(getString(R.string.insira_telefone_funcionario));
            } else if (chavePix.isEmpty()) {
                inputPix.setError(getString(R.string.insira_pix_funcionario));
            } else {
                salvarFuncionario(nomeFuncionario, cpf, telefone, chavePix);
            }
        });

        return view;
    }

    private void salvarFuncionario(String nome, String cpf, String telefone, String pix) {
        btnSalvarFuncionario.setEnabled(false);

        Map<String, Object> funcionario = new HashMap<>();
        funcionario.put("nomeFuncionario", nome);
        funcionario.put("cpf", cpf);
        funcionario.put("telefone", telefone);
        funcionario.put("chavePix", pix);

        dbFirestore.collection("funcionarios")
                .add(funcionario)
                .addOnSuccessListener(documentReference -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), R.string.funcionario_salvo_sucesso, Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    btnSalvarFuncionario.setEnabled(true);
                    Log.e("Firestore", "Erro ao salvar funcionário", e);
                    Toast.makeText(getContext(), R.string.erro_salvar_funcionario, Toast.LENGTH_SHORT).show();
                });
    }
}
