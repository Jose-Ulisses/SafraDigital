package com.example.safradigital.views.lavouras;

import static android.content.ContentValues.TAG;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddLavouraFragment extends Fragment {
    FirebaseFirestore dbfirestore = FirebaseFirestore.getInstance();
    private EditText inputNomeLavoura;
    Button btnSalvarLavoura;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_lavoura, container, false);
        
        inputNomeLavoura = view.findViewById(R.id.input_nome_lavoura);
        
        btnSalvarLavoura = view.findViewById(R.id.button_salvar_Lavoura);
        btnSalvarLavoura.setOnClickListener(v -> {
            String nomeLavoura = inputNomeLavoura.getText().toString().trim();

            if (!TextUtils.isEmpty(nomeLavoura)) {
                btnSalvarLavoura.setEnabled(false);

                Map<String, Object> lavoura = new HashMap<>();
                lavoura.put("nomeLavoura", nomeLavoura);
                lavoura.put("totalLavoura", 0);

                dbfirestore.collection("lavouras")
                        .add(lavoura)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Lavoura salva com sucesso!", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e ->
                                Log.w(TAG, "Erro adicionando lavouradocument", e));
            } else {
                inputNomeLavoura.setError("Por favor, insira o nome da lavoura");
            }
        });

        return view;
    }
}