package com.example.safradigital.views.colheitas;

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

public class AllColheitasFragment extends Fragment {
    Database db;
    TextView mTextView, tvTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        db = new Database(requireContext());
        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutList);

        tvTitle = new TextView(requireContext());
        String temp = "Colheitas anteriores";
        tvTitle.setText(temp);
        tvTitle.setTextSize(35);
        tvTitle.setGravity(1);

        linearLayout.addView(tvTitle);

        Cursor c = db.getAllColheitas();
        if(c != null){
            c.moveToFirst();
            while(!c.isAfterLast()){
                int lavouraId = c.getInt(c.getColumnIndexOrThrow(DbSchema.ColheitasTbl.Cols.ID_LAVOURA));
                int talhaoId = c.getInt(c.getColumnIndexOrThrow(DbSchema.ColheitasTbl.Cols.ID_TALHAO));
                int funcionarioId = c.getInt(c.getColumnIndexOrThrow(DbSchema.ColheitasTbl.Cols.ID_FUNCIONARIO));
                float qntd = c.getFloat(c.getColumnIndexOrThrow(DbSchema.ColheitasTbl.Cols.QNTD));
                String data = c.getString(c.getColumnIndexOrThrow(DbSchema.ColheitasTbl.Cols.DATA));

                String lavoura = db.getLavouraNameById(lavouraId);
                String talhao = db.getTalhaoNameById(talhaoId);
                String funcionario = db.getFuncionarioNameById(funcionarioId);

                String text = "Lavoura: " + lavoura + "\n" +
                        "Talhão: " + talhao + "\n" +
                        "Funcionário: " + funcionario + "\n" +
                        "Quantidade: " + qntd + "\n" +
                        "DATA: " + data;

                mTextView = new TextView(requireContext());
                mTextView.setText(text);
                mTextView.setTextSize(25);
                mTextView.setPadding(0, 70, 0, 70);

                linearLayout.addView(mTextView);

                c.moveToNext();
            }
            c.close();
        }

        return view;
    }
}