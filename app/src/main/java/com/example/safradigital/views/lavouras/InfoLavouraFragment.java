package com.example.safradigital.views.lavouras;

import android.annotation.SuppressLint;
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

    @SuppressLint("SetTextI18n")
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

                            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_list, linearLayout, false);
                            TextView titleView = itemView.findViewById(R.id.text_item_name);
                            TextView descView = itemView.findViewById(R.id.text_item_description);

                            titleView.setText(talhao);
                            descView.setText("Total Colhido: " + totalTalhao + " sacas");
                            descView.setVisibility(View.VISIBLE);

                            linearLayout.addView(itemView);
                        }

                        dbFirestore.collection("lavouras").document(idLavoura)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (!isAdded()) return;

                                    Double total = documentSnapshot.getDouble("totalLavoura");
                                    if (total == null) total = 0.0;

                                    View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_list, linearLayout, false);
                                    TextView titleView = itemView.findViewById(R.id.text_item_name);
                                    TextView descView = itemView.findViewById(R.id.text_item_description);

                                    titleView.setText("Total da Lavoura");
                                    descView.setText(total + " Sacas");
                                    descView.setVisibility(View.VISIBLE);

                                    linearLayout.addView(itemView);
                                });
                    }
                });
    }
}