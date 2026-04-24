package com.example.safradigital.views.funcionarios;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.example.safradigital.db.Database;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddFuncionarioFragment extends Fragment {
    private Database db;
    EditText inputNome, inputCpf, inputTelefone, inputPix;
    Button btnSalvarFuncionario;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_funcionario, container, false);
        db = new Database(requireContext());
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
                inputNome.setError("Por favor, insira o nome do Funcionário");
            } else{
                if(cpf.isEmpty()){
                    inputCpf.setError("Por favor, insira o cpf do Funcionário");
                } else{
                    if(telefone.isEmpty()){
                        inputTelefone.setError("Por favor, insira o telefone do Funcionário");
                    } else {
                        if(chavePix.isEmpty()){
                            inputPix.setError("Por favor, insira a chave PIX do Funcionário");
                        } else {
                            db.addFuncionario(nomeFuncionario, cpf, telefone, chavePix);
                            Toast.makeText(getContext(), "Funcionário salvo com sucesso!", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        }
                    }
                }
            }
        });

        return view;
    }
}