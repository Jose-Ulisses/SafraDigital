package com.example.safradigital.views.lavouras;

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

public class InfoLavouraFragment extends Fragment {

    private static final String ARG_LAVOURA = "lavoura_name";
    private String nomeLavoura;
    Database db;
    TextView mTextView, tvNomeLavoura, tvTotal;

    public static InfoLavouraFragment newInstance(String lavouraName) {
        InfoLavouraFragment fragment = new InfoLavouraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LAVOURA, lavouraName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nomeLavoura = getArguments().getString(ARG_LAVOURA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutList);

        db = new Database(requireContext());
        tvNomeLavoura = view.findViewById(R.id.textViewTitle);
        tvNomeLavoura.setText(nomeLavoura);

        int lavouraId = db.getLavouraIdByName(nomeLavoura);
        Cursor c = db.getAllTalhoesByLavouraId(lavouraId);
        if(c != null){
            c.moveToFirst();
            while(!c.isAfterLast()){
                String talhao = c.getString(c.getColumnIndexOrThrow(DbSchema.TalhaoTbl.Cols.NOME_TALHAO));
                float totalTalhao = c.getFloat(c.getColumnIndexOrThrow(DbSchema.TalhaoTbl.Cols.TOTAL_TALHAO));
                String text = talhao + " = " + totalTalhao;

                mTextView = new TextView(requireContext());
                mTextView.setText(text);
                mTextView.setTextSize(30);
                mTextView.setPadding(0, 70, 0, 70);

                linearLayout.addView(mTextView);

                c.moveToNext();
            }
            c.close();
        }

        float total = db.getTotalLavoura(lavouraId);
        String temp = "Total da Lavoura: " + total;

        tvTotal = new TextView(requireContext());
        tvTotal.setText(temp);
        tvTotal.setTextSize(30);
        tvTotal.setPadding(0, 140, 0, 0);
        linearLayout.addView(tvTotal);

        return view;
    }
}