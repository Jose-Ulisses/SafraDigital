package com.example.safradigital.views.lavouras.talhao;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTalhaoFragment extends Fragment {
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    EditText inputNomeTalhao, inputPrecoTalhao;
    Button btnSalvarTalhao;
    ArrayAdapter<String> arrayAdapter;
    AutoCompleteTextView autoCompleteLavoura;
    String idLavoura;
    int precoTalhao;
    private final Map<String, String> lavouraIdsMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_talhao, container, false);

        inputNomeTalhao = view.findViewById(R.id.input_nome_talhao);
        inputPrecoTalhao = view.findViewById(R.id.input_preco);
        btnSalvarTalhao = view.findViewById(R.id.button_salvar_Lavoura);
        autoCompleteLavoura = view.findViewById(R.id.autoCompleteLavouraTalhao);

        //AutoComplete lavouras
        List<String> lavouras = new ArrayList<>();
        dbFirestore.collection("lavouras")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String nome = document.getString("nomeLavoura");
                        if (nome != null) {
                            lavouras.add(nome);
                            lavouraIdsMap.put(nome, document.getId());
                        }
                    }
                    arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, R.id.textView, lavouras);
                    if (autoCompleteLavoura != null) {
                        autoCompleteLavoura.setAdapter(arrayAdapter);
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Log.e("Firestore", getString(R.string.erro_buscar_lavouras), e);
                });

        if (autoCompleteLavoura != null) {
            autoCompleteLavoura.setOnItemClickListener((adapterView, view1, i, l) -> {
                String nomeLavoura = adapterView.getItemAtPosition(i).toString();
                idLavoura = lavouraIdsMap.get(nomeLavoura);
            });
        }

        btnSalvarTalhao.setOnClickListener(v -> {
            String nomeTalhao = inputNomeTalhao.getText().toString().trim();
            String precotemp = inputPrecoTalhao.getText().toString().trim();
            if(!TextUtils.isEmpty(precotemp)){
                precoTalhao = Integer.parseInt(precotemp);
            }

            if(!TextUtils.isEmpty(nomeTalhao)){
                if(precoTalhao != 0 && idLavoura != null){
                    btnSalvarTalhao.setEnabled(false);

                    Map<String, Object> talhao = new HashMap<>();
                    talhao.put("nomeTalhao", nomeTalhao);
                    talhao.put("precoTalhao", precoTalhao);
                    talhao.put("idLavoura", idLavoura);
                    talhao.put("totalTalhao", 0.0);

                    dbFirestore.collection("talhoes")
                            .add(talhao)
                            .addOnSuccessListener(documentReference -> {
                                if (!isAdded()) return;
                                Toast.makeText(getContext(), R.string.talhao_salvo_sucesso, Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack();
                            })
                            .addOnFailureListener(e -> {
                                if (!isAdded()) return;
                                btnSalvarTalhao.setEnabled(true);
                                Toast.makeText(getContext(), R.string.erro_salvar_talhao, Toast.LENGTH_SHORT).show();
                            });
                } else if (idLavoura == null) {
                    autoCompleteLavoura.setError(getString(R.string.selecione_lavoura));
                } else{
                    inputPrecoTalhao.setError(getString(R.string.insira_preco_talhao));
                }

            } else {
                inputNomeTalhao.setError(getString(R.string.insira_nome_talhao));
            }
        });

        return view;
    }
}