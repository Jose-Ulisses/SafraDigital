package com.example.safradigital.views.funcionarios;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcertoFragment extends Fragment {
    private static final String ARG_ID_FUNCIONARIO = "id_funcionario";
    private static final String ARG_NOME_FUNCIONARIO = "nome_funcionario";
    
    private String idFuncionario;
    private String nomeFuncionario;
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    private LinearLayout linearLayout;

    public static AcertoFragment newInstance(String idFuncionario, String nomeFuncionario) {
        AcertoFragment fragment = new AcertoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_FUNCIONARIO, idFuncionario);
        args.putString(ARG_NOME_FUNCIONARIO, nomeFuncionario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idFuncionario = getArguments().getString(ARG_ID_FUNCIONARIO);
            nomeFuncionario = getArguments().getString(ARG_NOME_FUNCIONARIO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutList);

        TextView tvNome = new TextView(requireContext());
        tvNome.setText(nomeFuncionario);
        tvNome.setTextColor(requireContext().getColor(R.color.primary));
        tvNome.setTextSize(32);
        tvNome.setPadding(0, 0, 0, 48);
        tvNome.setGravity(Gravity.CENTER);
        linearLayout.addView(tvNome);

        calcularAcerto();

        return view;
    }

    private void calcularAcerto() {
        dbFirestore.collection("colheitas")
                .whereEqualTo("idFuncionario", idFuncionario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Float> agregacao = new HashMap<>(); // key: idLavoura_idTalhao, value: totalQuantidade
                    Map<String, String> keysMapping = new HashMap<>(); // key: idLavoura_idTalhao, value: idLavoura|idTalhao

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String idL = doc.getString("idLavoura");
                        String idT = doc.getString("idTalhao");
                        Double qntdVal = doc.getDouble("quantidade");
                        if (qntdVal == null) qntdVal = 0.0;

                        String key = idL + "_" + idT;
                        Float currentTotal = agregacao.get(key);
                        if (currentTotal == null) currentTotal = 0f;
                        agregacao.put(key, currentTotal + qntdVal.floatValue());
                        keysMapping.put(key, idL + "|" + idT);
                    }

                    processarAgregados(agregacao, keysMapping);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erro ao buscar colheitas para acerto", e));
    }

    @SuppressLint("SetTextI18n")
    private void processarAgregados(Map<String, Float> agregacao, Map<String, String> keysMapping) {
        if (agregacao.isEmpty()) return;

        final float[] totalGeral = {0};
        List<Task<?>> tasks = new ArrayList<>();

        for (Map.Entry<String, Float> entry : agregacao.entrySet()) {
            String mapping = keysMapping.get(entry.getKey());
            if (mapping == null) continue;
            
            String[] ids = mapping.split("\\|");
            String idL = ids[0];
            String idT = ids[1];
            float totalQntd = entry.getValue();

            Task<DocumentSnapshot> taskL = dbFirestore.collection("lavouras").document(idL).get();
            Task<DocumentSnapshot> taskT = dbFirestore.collection("talhoes").document(idT).get();

            tasks.add(Tasks.whenAllSuccess(taskL, taskT).addOnSuccessListener(results -> {
                DocumentSnapshot snapL = (DocumentSnapshot) results.get(0);
                DocumentSnapshot snapT = (DocumentSnapshot) results.get(1);

                String nomeL = snapL.getString("nomeLavoura");
                String nomeT = snapT.getString("nomeTalhao");
                Long precoT = snapT.getLong("precoTalhao");
                if (precoT == null) precoT = 0L;

                float valorItem = totalQntd * precoT;
                totalGeral[0] += valorItem;

                View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_list, linearLayout, false);
                TextView titleView = itemView.findViewById(R.id.text_item_name);
                TextView descView = itemView.findViewById(R.id.text_item_description);

                titleView.setText(String.format("%s - %s", nomeL, nomeT));
                String details = String.format(java.util.Locale.getDefault(),
                        "Qtd: %.2f\n\n" + "Preço: R$ %d\n\n" + "Subtotal: R$ %.2f", totalQntd, precoT, valorItem);
                descView.setText(details);
                descView.setVisibility(View.VISIBLE);

                linearLayout.addView(itemView);
            }));
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(t -> {
            View totalView = LayoutInflater.from(requireContext()).inflate(R.layout.item_list, linearLayout, false);
            TextView titleView = totalView.findViewById(R.id.text_item_name);
            TextView descView = totalView.findViewById(R.id.text_item_description);

            totalView.setAlpha(0.95f);
            titleView.setText("Total a acertar:");
            titleView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            
            descView.setText(String.format(java.util.Locale.getDefault(), "R$ %.2f", totalGeral[0]));
            descView.setTextColor(requireContext().getColor(R.color.primary));
            descView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            descView.setTextSize(25);
            descView.setVisibility(View.VISIBLE);

            linearLayout.addView(totalView);
        });
    }
}