package com.example.safradigital.views.lavouras;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class InfoLavouraFragment extends Fragment {

    private static final String ARG_ID_LAVOURA = "id_lavoura";
    private static final String ARG_NOME_LAVOURA = "nome_lavoura";
    
    private String idLavoura;
    private String nomeLavoura;
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    private LinearLayout linearLayout;

    public static InfoLavouraFragment newInstance(String idLavoura, String nomeLavoura) {
        InfoLavouraFragment fragment = new InfoLavouraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_LAVOURA, idLavoura);
        args.putString(ARG_NOME_LAVOURA, nomeLavoura);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idLavoura = getArguments().getString(ARG_ID_LAVOURA);
            nomeLavoura = getArguments().getString(ARG_NOME_LAVOURA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        linearLayout = view.findViewById(R.id.linearLayoutList);
        TextView tvNomeLavoura = view.findViewById(R.id.textViewTitle);
        tvNomeLavoura.setText(nomeLavoura);

        carregarDetalhesLavoura();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void carregarDetalhesLavoura() {
        dbFirestore.collection("talhoes")
                .whereEqualTo("idLavoura", idLavoura)
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((value, error) -> {
                    if (!isAdded()) return;

                    if (error != null) {
                        Log.e("Firestore", "Erro ao buscar talhões", error);
                        return;
                    }

                    if (value != null) {
                        linearLayout.removeAllViews();
                        for (QueryDocumentSnapshot doc : value) {
                            String talhaoId = doc.getId();
                            String talhaoNome = doc.getString("nomeTalhao");
                            Double totalTalhao = doc.getDouble("totalTalhao");
                            Long precoTalhao = doc.getLong("precoTalhao");
                            if (totalTalhao == null) totalTalhao = 0.0;
                            if (precoTalhao == null) precoTalhao = 0L;

                            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_list, linearLayout, false);
                            TextView titleView = itemView.findViewById(R.id.text_item_name);
                            TextView descView = itemView.findViewById(R.id.text_item_description);

                            titleView.setText(talhaoNome);
                            descView.setText("Total Colhido: " + totalTalhao + " sacas\n\nPreço por Saca: R$ " + precoTalhao);
                            descView.setVisibility(View.VISIBLE);

                            final Long precoFinal = precoTalhao;
                            // Clique no container interno para garantir que o clique seja registrado
                            View itemContainer = itemView.findViewById(R.id.item_container);
                            itemContainer.setOnClickListener(v -> {
                                Log.d("InfoLavoura", "Clicou no talhão: " + talhaoNome);
                                mostrarDialogoEditarPreco(talhaoId, talhaoNome, precoFinal);
                            });

                            linearLayout.addView(itemView);
                        }

                        dbFirestore.collection("lavouras").document(idLavoura)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (!isAdded()) return;

                                    Double totalL = documentSnapshot.getDouble("totalLavoura");
                                    if (totalL == null) totalL = 0.0;

                                    View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_list, linearLayout, false);
                                    TextView titleView = itemView.findViewById(R.id.text_item_name);
                                    TextView descView = itemView.findViewById(R.id.text_item_description);

                                    titleView.setText("Total da Lavoura");
                                    descView.setText(totalL + " Sacas");
                                    descView.setVisibility(View.VISIBLE);

                                    linearLayout.addView(itemView);
                                });
                    }
                });
    }

    private void mostrarDialogoEditarPreco(String talhaoId, String nome, Long precoAtual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Preço - " + nome);

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(precoAtual));
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String novoPrecoStr = input.getText().toString();
            if (!novoPrecoStr.isEmpty()) {
                try {
                    long novoPreco = Long.parseLong(novoPrecoStr);
                    // Atualiza no Firestore (persistência offline cuida do resto)
                    dbFirestore.collection("talhoes").document(talhaoId)
                            .update("precoTalhao", novoPreco);
                    
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Preço atualizado!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Valor inválido", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.setNeutralButton("Excluir", (dialog, which) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Excluir Talhão")
                    .setMessage("Deseja realmente excluir o talhão " + nome + "?")
                    .setPositiveButton("Excluir", (d, w) -> {
                        dbFirestore.collection("talhoes").document(talhaoId).delete();
                        if (isAdded()) Toast.makeText(getContext(), "Talhão excluído", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Voltar", null)
                    .show();
        });

        builder.show();
    }
}
