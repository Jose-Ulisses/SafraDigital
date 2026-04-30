package com.example.safradigital.views.funcionarios;

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

public class AllFuncionariosFragment extends Fragment {
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutList);

        listarFuncionarios();

        return view;
    }

    private void listarFuncionarios() {
        dbFirestore.collection("funcionarios")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Erro ao escutar funcionários", error);
                        Toast.makeText(getContext(), R.string.erro_carregar_dados, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && isAdded()) {
                        linearLayout.removeAllViews();
                        for (QueryDocumentSnapshot doc : value) {
                            String nome = doc.getString("nomeFuncionario");
                            if (nome != null) {
                                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, linearLayout, false);
                                TextView mTextView = itemView.findViewById(R.id.text_item_name);
                                View container = itemView.findViewById(R.id.item_container);

                                mTextView.setText(nome);
                                container.setOnClickListener(v -> {
                                    InfoFuncionarioFragment fragment = InfoFuncionarioFragment.newInstance(doc.getId(), nome);
                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                });
                                linearLayout.addView(itemView);
                            }
                        }
                    }
                });
    }
}
