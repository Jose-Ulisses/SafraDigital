package com.example.safradigital.views.funcionarios;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoFuncionarioFragment extends Fragment {
    private static final String ARG_ID_FUNCIONARIO = "id_funcionario";
    private static final String ARG_NOME_FUNCIONARIO = "nome_funcionario";
    
    private String idFuncionario;
    private String nomeFuncionario;
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    private LinearLayout linearLayout;

    public static InfoFuncionarioFragment newInstance(String idFuncionario, String nomeFuncionario) {
        InfoFuncionarioFragment fragment = new InfoFuncionarioFragment();
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
        View view = inflater.inflate(R.layout.fragment_info_funcionario, container, false);

        linearLayout = view.findViewById(R.id.linearLayoutList);
        TextView tvNome = view.findViewById(R.id.textViewTitle);
        tvNome.setText(nomeFuncionario);

        carregarDetalhes();

        Button btnAcerto = view.findViewById(R.id.button_acerto);
        btnAcerto.setOnClickListener(v -> {
            AcertoFragment fragment = AcertoFragment.newInstance(idFuncionario, nomeFuncionario);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void carregarDetalhes() {
        dbFirestore.collection("funcionarios").document(idFuncionario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String cpf = documentSnapshot.getString("cpf");
                        String telefone = documentSnapshot.getString("telefone");
                        String chavePix = documentSnapshot.getString("chavePix");

                        linearLayout.removeAllViews();

                        adicionarTextView(getString(R.string.label_cpf, cpf));
                        adicionarTextView(getString(R.string.label_telefone, telefone));
                        adicionarTextView(getString(R.string.label_pix, chavePix));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erro ao buscar detalhes do funcionário", e));
    }

    private void adicionarTextView(String texto) {
        TextView textView = new TextView(requireContext());
        textView.setText(texto);
        textView.setTextSize(30);
        textView.setPadding(0, 70, 0, 70);
        linearLayout.addView(textView);
    }
}
