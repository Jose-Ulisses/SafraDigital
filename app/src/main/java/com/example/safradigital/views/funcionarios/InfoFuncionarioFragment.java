package com.example.safradigital.views.funcionarios;

import android.database.Cursor;
import android.os.Bundle;
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
import com.example.safradigital.db.Database;
import com.example.safradigital.db.DbSchema;

public class InfoFuncionarioFragment extends Fragment {
    private static final String ARG_FUNCIONARIO = "nomeFuncionario";
    private String nomeFuncionario;
    Database db;
    TextView tvnomeFuncionario, mTextView;

    public static InfoFuncionarioFragment newInstance(String nomeFuncionario) {
        InfoFuncionarioFragment fragment = new InfoFuncionarioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FUNCIONARIO, nomeFuncionario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nomeFuncionario = getArguments().getString(ARG_FUNCIONARIO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_funcionario, container, false);

        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutList);

        db = new Database(requireContext());
        tvnomeFuncionario = view.findViewById(R.id.textViewTitle);
        tvnomeFuncionario.setText(nomeFuncionario);

        int idFuncionario = db.getFuncionarioIdByName(nomeFuncionario);
        Cursor c = db.getFuncionario(idFuncionario);
        if(c != null){
            c.moveToFirst();

            String cpf = c.getString(c.getColumnIndexOrThrow(DbSchema.FuncionariosTbl.Cols.CPF_FUNCIONARIO));
            String telefone = c.getString(c.getColumnIndexOrThrow(DbSchema.FuncionariosTbl.Cols.TELEFONE_FUNCIONARIO));
            String chavePix = c.getString(c.getColumnIndexOrThrow(DbSchema.FuncionariosTbl.Cols.PIX_FUNCIONARIO));

            mTextView = new TextView(requireContext());
            String tempCpf = "CPF: " + cpf;
            mTextView.setText(tempCpf);
            mTextView.setTextSize(30);
            mTextView.setPadding(0, 70, 0, 70);
            linearLayout.addView(mTextView);

            mTextView = new TextView(requireContext());
            String tempTelefone = "Número: " + telefone;

            mTextView.setText(tempTelefone);
            mTextView.setTextSize(30);
            mTextView.setPadding(0, 70, 0, 70);
            linearLayout.addView(mTextView);

            mTextView = new TextView(requireContext());
            String tempPix = "Chave Pix: " + chavePix;
            mTextView.setText(tempPix);
            mTextView.setTextSize(30);
            mTextView.setPadding(0, 70, 0, 70);
            linearLayout.addView(mTextView);

            c.close();
        }

        Button btnAcerto = view.findViewById(R.id.button_acerto);
        btnAcerto.setOnClickListener(v -> {
            AcertoFragment fragment = AcertoFragment.newInstance(nomeFuncionario);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}