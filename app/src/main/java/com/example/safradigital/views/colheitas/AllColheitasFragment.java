package com.example.safradigital.views.colheitas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AllColheitasFragment extends Fragment {
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutList);

        listarColheitas();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void listarColheitas() {
        dbFirestore.collection("colheitas")
                .addSnapshotListener((value, error) -> {
                    if (!isAdded()) return;

                    if (error != null) {
                        Log.e("Firestore", "Erro ao escutar colheitas", error);
                        Toast.makeText(getContext(), "Erro ao carregar colheitas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        linearLayout.removeAllViews();
                        for (QueryDocumentSnapshot doc : value) {
                            String idL = doc.getString("idLavoura");
                            String idT = doc.getString("idTalhao");
                            String idF = doc.getString("idFuncionario");
                            Double qntd = doc.getDouble("quantidade");
                            String data = doc.getString("data");

                            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_list, linearLayout, false);
                            TextView titleView = itemView.findViewById(R.id.text_item_name);
                            titleView.setVisibility(View.GONE);
                            TextView descView = itemView.findViewById(R.id.text_item_description);


                            // Inicia com valores padrão enquanto busca os nomes
                            titleView.setText("Carregando...");
                            descView.setText(String.format(java.util.Locale.getDefault(),
                                    "Qtd: %.1f | Data: %s", qntd != null ? qntd : 0.0, data));
                            descView.setVisibility(View.VISIBLE);
                            linearLayout.addView(itemView);

                            final String[] nomeT = {"..."};
                            final String[] nomeF = {"..."};
                            final String[] nomeL = {"..."};

                            // Busca nomes relacionados
                            if (idL != null) {
                                dbFirestore.collection("lavouras").document(idL).get().addOnSuccessListener(snapL -> {
                                    if (snapL.exists()) {
                                        nomeL[0] = snapL.getString("nomeLavoura");
                                    }
                                });
                            }

                            Runnable updateDetails = () -> {
                                descView.setText(String.format(java.util.Locale.getDefault(),
                                        "Lavoura: %s\n\nTalhão: %s\n\nFunc.: %s\n\nQtd: %.2f\n\nData: %s",
                                        nomeL[0], nomeT[0], nomeF[0], qntd != null ? qntd : 0.0, data));
                            };

                            if (idT != null) {
                                dbFirestore.collection("talhoes").document(idT).get().addOnSuccessListener(snapT -> {
                                    if (snapT.exists()) {
                                        nomeT[0] = snapT.getString("nomeTalhao");
                                        updateDetails.run();
                                    }
                                });
                            }

                            if (idF != null) {
                                dbFirestore.collection("funcionarios").document(idF).get().addOnSuccessListener(snapF -> {
                                    if (snapF.exists()) {
                                        nomeF[0] = snapF.getString("nomeFuncionario");
                                        updateDetails.run();
                                    }
                                });
                            }
                        }
                    }
                });
    }
}