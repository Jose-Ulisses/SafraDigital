package com.example.safradigital.views.colheitas;

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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.example.safradigital.db.Database;
import com.example.safradigital.db.DbSchema;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddColheitaFragment extends Fragment {
    Database db;
    ArrayAdapter<String> arrayAdapterLavoura, arrayAdapterTalhao, arrayAdapterFuncionario;
    AutoCompleteTextView autoCompleteLavoura, autoCompleteTalhao, autoCompleteFuncionario;
    EditText inputQntd;
    DateTimeFormatter format;
    LocalDateTime dateHour;
    int idLavoura, idTalhao, idFuncionario;
    Cursor c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = new Database(requireContext());
        View view = inflater.inflate(R.layout.fragment_add_colheita, container, false);

        //DROPDOWN MENU FOR LAVOURAS
        List<String> lavouras = new ArrayList<>();
        c = db.getAllLavouras();
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

        arrayAdapterLavoura = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, R.id.textView, lavouras);
        autoCompleteLavoura = view.findViewById(R.id.autoCompleteLavouraColheita);
        if (autoCompleteLavoura != null) {
            autoCompleteLavoura.setAdapter(arrayAdapterLavoura);

            autoCompleteLavoura.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view1, int i, long l) {
                    String nomeLavoura = adapterView.getItemAtPosition(i).toString();
                    idLavoura = db.getLavouraIdByName(nomeLavoura);

                    //DROPDOWN MENU FOR TALHÕES BY LAVOURA ID
                    List<String> talhoes = new ArrayList<>();
                    c = db.getAllTalhoesByLavouraId(idLavoura);
                    if(c != null){
                        if (c.moveToFirst()) {
                            int indexNome = c.getColumnIndex(DbSchema.TalhaoTbl.Cols.NOME_TALHAO);
                            while(!c.isAfterLast()){
                                talhoes.add(c.getString(indexNome));
                                c.moveToNext();
                            }
                        }
                        c.close();
                    }
                    arrayAdapterTalhao = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, R.id.textView, talhoes);
                    autoCompleteTalhao = view.findViewById(R.id.autoCompleteTalhao);
                    if (autoCompleteTalhao != null) {
                        autoCompleteTalhao.setAdapter(arrayAdapterTalhao);
                        autoCompleteTalhao.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view1, int i, long l) {
                                String nomeTalhao = adapterView.getItemAtPosition(i).toString();
                                idTalhao = db.getTalhaoIdByName(nomeTalhao);
                            }
                        });
                    }
                }
            });

            //DROPDOWN MENU FOR FUNCIONÁRIOS
            List<String> funcionarios = new ArrayList<>();
            c = db.getAllFuncionarios();
            if(c != null){
                if (c.moveToFirst()) {
                    int indexNome = c.getColumnIndex(DbSchema.FuncionariosTbl.Cols.NOME_FUNCIONARIO);
                    while(!c.isAfterLast()){
                        funcionarios.add(c.getString(indexNome));
                        c.moveToNext();
                    }
                }
                c.close();
            }
            arrayAdapterFuncionario = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, R.id.textView, funcionarios);
            autoCompleteFuncionario = view.findViewById(R.id.autoCompleteFuncionario);
            if (autoCompleteFuncionario != null) {
                autoCompleteFuncionario.setAdapter(arrayAdapterFuncionario);
                autoCompleteFuncionario.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view1, int i, long l) {
                        String nomeFuncionario = adapterView.getItemAtPosition(i).toString();
                        idFuncionario = db.getFuncionarioIdByName(nomeFuncionario);
                    }
                });
            }
        }

        Button btnSalvarColheita = view.findViewById(R.id.button_salvar_colheita);
        btnSalvarColheita.setOnClickListener(v -> {
            inputQntd = view.findViewById(R.id.input_qntd);
            String qntdTemp = inputQntd.getText().toString().trim();
            float qntd = Float.parseFloat(qntdTemp);

            dateHour = LocalDateTime.now();
            format = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
            String data = dateHour.format(format);

            if(!(qntd == 0)){
                db.addColheita(idLavoura, idTalhao, idFuncionario, qntd, data);
                db.insertColheitaLavoura(idLavoura, qntd);
                db.insertColheitaTalhao(idTalhao, qntd);

                Toast.makeText(getContext(), "Colheita salva com sucesso!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }else{
                inputQntd.setError("Informe a quantiade colhida!");
            }
        });
        return view;
    }
}