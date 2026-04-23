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

public class AllLavourasFragment extends Fragment {
    Database db;
    TextView mTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        db = new Database(requireContext());

        Cursor c = db.getAllLavouras();
        if(c != null){
            c.moveToFirst();
            while(!c.isAfterLast()){
                String lavoura = c.getString(c.getColumnIndexOrThrow(DbSchema.LavourasTbl.Cols.NOME_LAVOURA));

                mTextView = new TextView(requireContext());
                mTextView.setText(lavoura);
                mTextView.setTextSize(45);
                mTextView.setPadding(0, 70, 0, 70);
                mTextView.setOnClickListener(v -> {
                    InfoLavouraFragment fragment = InfoLavouraFragment.newInstance(lavoura);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                });

                LinearLayout linearLayout = view.findViewById(R.id.linearLayoutList);
                linearLayout.addView(mTextView);

                c.moveToNext();
            }
            c.close();
        }

        return view;
    }
}
