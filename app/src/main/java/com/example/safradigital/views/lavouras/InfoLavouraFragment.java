package com.example.safradigital.views.lavouras;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
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

    private void carregarDetalhesLavoura() {
        // Carregar Talhões
        dbFirestore.collection("talhoes")
                .whereEqualTo("idLavoura", idLavoura)
                .addSnapshotListener((value, error) -> {
                    if (!isAdded()) return;

                    if (error != null) {
                        Log.e("Firestore", "Erro ao buscar talhões", error);
                        return;
                    }

                    if (value != null) {
                        linearLayout.removeAllViews();
                        for (QueryDocumentSnapshot doc : value) {
                            String talhao = doc.getString("nomeTalhao");
                            Double totalTalhao = doc.getDouble("totalTalhao");
                            if (totalTalhao == null) totalTalhao = 0.0;
                            
                            String text = talhao + " = " + totalTalhao;

                            TextView mTextView = new TextView(requireContext());
                            mTextView.setText(text);
                            mTextView.setTextSize(30);
                            mTextView.setPadding(0, 70, 0, 70);

                            linearLayout.addView(mTextView);
                        }
                        
                        // Após listar talhões, buscar total da lavoura
                        dbFirestore.collection("lavouras").document(idLavoura)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (!isAdded()) return;

                                    Double total = documentSnapshot.getDouble("totalLavoura");
                                    if (total == null) total = 0.0;

                                    TextView tvTotal = new TextView(requireContext());
                                    tvTotal.setText(getString(R.string.total_lavoura_label, total));
                                    tvTotal.setTextSize(30);
                                    tvTotal.setPadding(0, 140, 0, 0);
                                    linearLayout.addView(tvTotal);
                                });
                    }
                });
    }
}