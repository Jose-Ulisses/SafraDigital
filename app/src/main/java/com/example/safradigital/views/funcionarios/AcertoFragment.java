package com.example.safradigital.views.funcionarios;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safradigital.R;
import com.example.safradigital.db.Database;
import com.example.safradigital.db.DbSchema;

public class AcertoFragment extends Fragment {
    private static final String ARG_FUNCIONARIO = "nomeFuncionario";
    private String nomeFuncionario;
    Database db;
    TextView mTextView, tvnomeFuncionario;

    public static AcertoFragment newInstance(String nomeFuncionario) {
        AcertoFragment fragment = new AcertoFragment();
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
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        float totalAcerto = 0;

        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutList);

        db = new Database(requireContext());
        tvnomeFuncionario = new TextView(requireContext());
        tvnomeFuncionario.setText(nomeFuncionario);
        tvnomeFuncionario.setTextSize(45);
        tvnomeFuncionario.setPadding(0, 0, 0, 70);
        tvnomeFuncionario.setGravity(1);
        linearLayout.addView(tvnomeFuncionario);

        int idFuncionario = db.getFuncionarioIdByName(nomeFuncionario);
        Cursor c = db.getAcerto(idFuncionario);
        if(c != null){
            c.moveToFirst();
            while(!c.isAfterLast()){
                int lavouraId = c.getInt(c.getColumnIndexOrThrow(DbSchema.ColheitasTbl.Cols.ID_LAVOURA));
                int talhaoId = c.getInt(c.getColumnIndexOrThrow(DbSchema.ColheitasTbl.Cols.ID_TALHAO));
                int preco = db.getPrecoTalhao(talhaoId);
                float qntd = c.getFloat(c.getColumnIndexOrThrow("total"));

                totalAcerto += (qntd * preco);

                String lavoura = db.getLavouraNameById(lavouraId);
                String talhao = db.getTalhaoNameById(talhaoId);

                mTextView = new TextView(requireContext());
                String text = "Lavoura: " + lavoura + "\n" +
                        "Talhão: " + talhao + "\n" +
                        "Quantidade: " + qntd;
                mTextView.setText(text);
                mTextView.setTextSize(30);
                mTextView.setPadding(0, 70, 0, 70);
                linearLayout.addView(mTextView);

                c.moveToNext();
            }
            c.close();
        }

        mTextView = new TextView(requireContext());
        String text = "Total a acertar: R$" + totalAcerto;
        mTextView.setText(text);
        mTextView.setTextSize(40);
        mTextView.setPadding(0, 70, 0, 70);
        mTextView.setGravity(1);
        linearLayout.addView(mTextView);

        return view;
    }
}