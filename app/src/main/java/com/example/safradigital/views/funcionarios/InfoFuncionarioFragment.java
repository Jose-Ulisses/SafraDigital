package com.example.safradigital.views.funcionarios;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
    private String cpfFuncionario, telefoneFuncionario, pixFuncionario;
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    private LinearLayout linearLayout;
    private TextView tvNome;

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
        tvNome = view.findViewById(R.id.textViewTitle);
        tvNome.setText(nomeFuncionario);

        carregarDetalhes();

        Button btnEditar = view.findViewById(R.id.button_editar_funcionario);
        btnEditar.setOnClickListener(v -> mostrarDialogoEditar());

        Button btnExcluir = view.findViewById(R.id.button_excluir_funcionario);
        btnExcluir.setOnClickListener(v -> confirmarExclusao());

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
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Erro ao buscar detalhes", error);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists() && isAdded()) {
                        nomeFuncionario = documentSnapshot.getString("nomeFuncionario");
                        cpfFuncionario = documentSnapshot.getString("cpf");
                        telefoneFuncionario = documentSnapshot.getString("telefone");
                        pixFuncionario = documentSnapshot.getString("chavePix");

                        tvNome.setText(nomeFuncionario);
                        linearLayout.removeAllViews();

                        adicionarItemInfo("CPF", cpfFuncionario);
                        adicionarItemInfo("Telefone", telefoneFuncionario);
                        adicionarItemInfo("Chave PIX", pixFuncionario);
                    }
                });
    }

    private void confirmarExclusao() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Excluir")
                .setMessage("Tem certeza que deseja excluir " + nomeFuncionario + "?")
                .setPositiveButton("Sim, Excluir", (dialog, which) -> {
                    dbFirestore.collection("funcionarios").document(idFuncionario).delete();
                    Toast.makeText(getContext(), "Funcionário removido", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void mostrarDialogoEditar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_add_funcionario, null);
        builder.setView(view);

        EditText inputNome = view.findViewById(R.id.input_nome_Funcionario);
        EditText inputCpf = view.findViewById(R.id.input_cpf);
        EditText inputTelefone = view.findViewById(R.id.input_telefone);
        EditText inputPix = view.findViewById(R.id.input_chave_pix);
        Button btnSalvar = view.findViewById(R.id.button_salvar_Funcionario);

        inputNome.setText(nomeFuncionario);
        inputCpf.setText(cpfFuncionario);
        inputTelefone.setText(telefoneFuncionario);
        inputPix.setText(pixFuncionario);
        btnSalvar.setText("Atualizar");

        AlertDialog dialog = builder.create();

        btnSalvar.setOnClickListener(v -> {
            String novoNome = inputNome.getText().toString().trim();
            String novoCpf = inputCpf.getText().toString().trim();
            String novoTel = inputTelefone.getText().toString().trim();
            String novoPix = inputPix.getText().toString().trim();

            if (novoNome.isEmpty()) {
                inputNome.setError("Insira o nome");
                return;
            }

            dbFirestore.collection("funcionarios").document(idFuncionario)
                    .update("nomeFuncionario", novoNome,
                            "cpf", novoCpf,
                            "telefone", novoTel,
                            "chavePix", novoPix);

            Toast.makeText(getContext(), "Dados atualizados!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        builder.setNeutralButton("Excluir Funcionário", (d, w) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Excluir")
                    .setMessage("Tem certeza que deseja excluir " + nomeFuncionario + "?")
                    .setPositiveButton("Sim, Excluir", (d2, w2) -> {
                        dbFirestore.collection("funcionarios").document(idFuncionario).delete();
                        Toast.makeText(getContext(), "Funcionário removido", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });

        dialog.show();
    }

    private void adicionarItemInfo(String label, String valor) {
        View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_list, linearLayout, false);
        TextView titleView = itemView.findViewById(R.id.text_item_name);
        TextView descView = itemView.findViewById(R.id.text_item_description);

        titleView.setText(label);
        descView.setText(valor);
        descView.setVisibility(View.VISIBLE);

        linearLayout.addView(itemView);
    }
}
