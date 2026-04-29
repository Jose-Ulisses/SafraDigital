package com.example.safradigital.views.colheitas;

import android.os.Bundle;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddColheitaFragment extends Fragment {
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    ArrayAdapter<String> arrayAdapterLavoura, arrayAdapterTalhao, arrayAdapterFuncionario;
    AutoCompleteTextView autoCompleteLavoura, autoCompleteTalhao, autoCompleteFuncionario;
    EditText inputQntd;
    Button btnSalvarColheita;
    DateTimeFormatter format;
    LocalDateTime dateHour;
    String idLavoura, idTalhao, idFuncionario;
    
    private final Map<String, String> lavouraIdsMap = new HashMap<>();
    private final Map<String, String> talhaoIdsMap = new HashMap<>();
    private final Map<String, String> funcionarioIdsMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_colheita, container, false);

        autoCompleteLavoura = view.findViewById(R.id.autoCompleteLavouraColheita);
        autoCompleteTalhao = view.findViewById(R.id.autoCompleteTalhao);
        autoCompleteFuncionario = view.findViewById(R.id.autoCompleteFuncionario);
        inputQntd = view.findViewById(R.id.input_qntd);
        btnSalvarColheita = view.findViewById(R.id.button_salvar_colheita);

        buscarLavouras();
        buscarFuncionarios();

        autoCompleteLavoura.setOnItemClickListener((adapterView, view1, i, l) -> {
            String nomeLavoura = adapterView.getItemAtPosition(i).toString();
            idLavoura = lavouraIdsMap.get(nomeLavoura);
            buscarTalhoes(idLavoura);
        });

        autoCompleteTalhao.setOnItemClickListener((adapterView1, view2, i1, l1) -> {
            String nomeTalhao = adapterView1.getItemAtPosition(i1).toString();
            idTalhao = talhaoIdsMap.get(nomeTalhao);
        });

        autoCompleteFuncionario.setOnItemClickListener((adapterView, view1, i, l) -> {
            String nomeFuncionario = adapterView.getItemAtPosition(i).toString();
            idFuncionario = funcionarioIdsMap.get(nomeFuncionario);
        });

        btnSalvarColheita.setOnClickListener(v -> {
            String qntdTemp = inputQntd.getText().toString().trim();
            
            if (qntdTemp.isEmpty()) {
                inputQntd.setError(getString(R.string.informe_quantidade));
                return;
            }

            float qntd = Float.parseFloat(qntdTemp);

            if (idLavoura == null) {
                autoCompleteLavoura.setError(getString(R.string.selecione_lavoura));
            } else if (idTalhao == null) {
                autoCompleteTalhao.setError(getString(R.string.selecione_talhao));
            } else if (idFuncionario == null) {
                autoCompleteFuncionario.setError(getString(R.string.selecione_funcionario));
            } else if (qntd == 0) {
                inputQntd.setError(getString(R.string.informe_quantidade));
            } else {
                salvarColheita(qntd);
            }
        });

        return view;
    }

    private void buscarLavouras() {
        dbFirestore.collection("lavouras")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;

                    List<String> nomes = new ArrayList<>();
                    lavouraIdsMap.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nome = doc.getString("nomeLavoura");
                        if (nome != null) {
                            nomes.add(nome);
                            lavouraIdsMap.put(nome, doc.getId());
                        }
                    }
                    arrayAdapterLavoura = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, R.id.textView, nomes);
                    autoCompleteLavoura.setAdapter(arrayAdapterLavoura);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Log.e("Firestore", getString(R.string.erro_buscar_lavouras), e);
                });
    }

    private void buscarTalhoes(String lavouraId) {
        dbFirestore.collection("talhoes")
                .whereEqualTo("idLavoura", lavouraId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;

                    List<String> nomes = new ArrayList<>();
                    talhaoIdsMap.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nome = doc.getString("nomeTalhao");
                        if (nome != null) {
                            nomes.add(nome);
                            talhaoIdsMap.put(nome, doc.getId());
                        }
                    }
                    arrayAdapterTalhao = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, R.id.textView, nomes);
                    autoCompleteTalhao.setAdapter(arrayAdapterTalhao);
                    autoCompleteTalhao.setText(""); // Limpa seleção anterior
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Log.e("Firestore", getString(R.string.erro_buscar_talhoes), e);
                });
    }

    private void buscarFuncionarios() {
        dbFirestore.collection("funcionarios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;

                    List<String> nomes = new ArrayList<>();
                    funcionarioIdsMap.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nome = doc.getString("nomeFuncionario");
                        if (nome != null) {
                            nomes.add(nome);
                            funcionarioIdsMap.put(nome, doc.getId());
                        }
                    }
                    arrayAdapterFuncionario = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, R.id.textView, nomes);
                    autoCompleteFuncionario.setAdapter(arrayAdapterFuncionario);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Log.e("Firestore", getString(R.string.erro_buscar_funcionarios), e);
                });
    }

    private void salvarColheita(float qntd) {
        if (btnSalvarColheita != null) btnSalvarColheita.setEnabled(false);

        dateHour = LocalDateTime.now();
        format = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
        String data = dateHour.format(format);

        Map<String, Object> colheita = new HashMap<>();
        colheita.put("idLavoura", idLavoura);
        colheita.put("idTalhao", idTalhao);
        colheita.put("idFuncionario", idFuncionario);
        colheita.put("quantidade", qntd);
        colheita.put("data", data);

        dbFirestore.collection("colheitas")
                .add(colheita)
                .addOnSuccessListener(documentReference -> {
                    if (!isAdded()) return;

                    dbFirestore.collection("lavouras").document(idLavoura)
                            .update("totalLavoura", FieldValue.increment(qntd));

                    dbFirestore.collection("talhoes").document(idTalhao)
                            .update("totalTalhao", FieldValue.increment(qntd));

                    Toast.makeText(getContext(), R.string.colheita_salva_sucesso, Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    if (btnSalvarColheita != null) btnSalvarColheita.setEnabled(true);
                    Log.e("Firestore", "Erro ao salvar colheita", e);
                    Toast.makeText(getContext(), R.string.erro_salvar_colheita, Toast.LENGTH_SHORT).show();
                });
    }
}