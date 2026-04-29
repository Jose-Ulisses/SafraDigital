package com.example.safradigital.views.lavouras;

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

public class AllLavourasFragment extends Fragment {
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutList);

        listarLavouras();

        return view;
    }

    private void listarLavouras() {
        dbFirestore.collection("lavouras")
                .addSnapshotListener((value, error) -> {
                    if (!isAdded()) return;

                    if (error != null) {
                        Log.e("Firestore", "Erro ao escutar lavouras", error);
                        Toast.makeText(getContext(), R.string.erro_carregar_dados, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        linearLayout.removeAllViews();
                        for (QueryDocumentSnapshot doc : value) {
                            String nome = doc.getString("nomeLavoura");
                            if (nome != null) {
                                TextView mTextView = new TextView(requireContext());
                                mTextView.setText(nome);
                                mTextView.setTextSize(45);
                                mTextView.setPadding(0, 70, 0, 70);
                                mTextView.setOnClickListener(v -> {
                                    InfoLavouraFragment fragment = InfoLavouraFragment.newInstance(doc.getId(), nome);
                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                });
                                linearLayout.addView(mTextView);
                            }
                        }
                    }
                });
    }
}
