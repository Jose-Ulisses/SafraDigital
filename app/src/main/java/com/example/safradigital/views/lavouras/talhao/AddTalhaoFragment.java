package com.example.safradigital.views.lavouras.talhao;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safradigital.R;
import com.example.safradigital.db.Database;
import com.example.safradigital.db.DbSchema;

import java.util.ArrayList;
import java.util.List;

public class AddTalhaoFragment extends Fragment {
    private Database db;
    EditText inputNomeTalhao, inputPrecoTalhao;
    Button btnSalvarTalhao;
    ArrayAdapter<String> arrayAdapter;
    AutoCompleteTextView autoCompleteLavoura;
    int idLavoura;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_talhao, container, false);

        inputNomeTalhao = view.findViewById(R.id.input_nome_talhao);
        inputPrecoTalhao = view.findViewById(R.id.input_preco);
        btnSalvarTalhao = view.findViewById(R.id.button_salvar_Lavoura);
        autoCompleteLavoura = view.findViewById(R.id.autoCompleteLavouraTalhao);

        //AutoComplete lavouras
        List<String> lavouras = new ArrayList<>();
        db = new Database(requireContext());
        Cursor c = db.getAllLavouras();
        if(c != null){
            if (c.moveToFirst()) {
                int indexNome = c.getColumnIndex(DbSchema.LavourasTbl.Cols.NOME_LAVOURA);
                while(!c.isAfterLast()){
                    lavouras.add(c.getString(indexNome));
                    c.moveToNext();
                }
            }
            c.close();
        }

        arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, R.id.textView, lavouras);
        if (autoCompleteLavoura != null) {
            autoCompleteLavoura.setAdapter(arrayAdapter);

            autoCompleteLavoura.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view1, int i, long l) {
                    String nomeLavoura = adapterView.getItemAtPosition(i).toString();
                    idLavoura = db.getIdLavouraByName(nomeLavoura);
                }
            });
        }

        return view;
    }
}