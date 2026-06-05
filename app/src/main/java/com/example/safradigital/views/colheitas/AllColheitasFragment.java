package com.example.safradigital.views.colheitas;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AllColheitasFragment extends Fragment {
    private final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    private LinearLayout linearLayout;
    private Spinner spinnerLavoura, spinnerPanhador;
    private TextView textDate;
    private Button btnClear;
    private View cardFilter;
    private View btnToggleFilter;

    private String selectedLavouraId = null;
    private String selectedPanhadorId = null;
    private String selectedDate = null;

    private final List<String> lavouraIds = new ArrayList<>();
    private final List<String> panhadorIds = new ArrayList<>();
    
    private ListenerRegistration colheitasListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_colheitas, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutList);
        spinnerLavoura = view.findViewById(R.id.spinnerFilterLavoura);
        spinnerPanhador = view.findViewById(R.id.spinnerFilterPanhador);
        textDate = view.findViewById(R.id.textFilterDate);
        btnClear = view.findViewById(R.id.btnClearFilters);
        cardFilter = view.findViewById(R.id.cardFilter);
        btnToggleFilter = view.findViewById(R.id.btnToggleFilter);

        btnToggleFilter.setOnClickListener(v -> {
            if (cardFilter.getVisibility() == View.GONE) {
                cardFilter.setVisibility(View.VISIBLE);
            } else {
                cardFilter.setVisibility(View.GONE);
            }
        });

        setupFilters();
        listarColheitas();
        return view;
    }

    private void setupFilters() {
        // Setup Date Picker
        textDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                // Formato yyyy-MM-dd para ordenação correta no Firestore
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                // Exibe no formato brasileiro para o usuário
                String displayDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                textDate.setText(displayDate);
                listarColheitas();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Setup Spinners Data
        loadSpinnerData();

        btnClear.setOnClickListener(v -> {
            spinnerLavoura.setSelection(0);
            spinnerPanhador.setSelection(0);
            String text = "Selecionar Data";
            textDate.setText(text);
            selectedLavouraId = null;
            selectedPanhadorId = null;
            selectedDate = null;
            listarColheitas();
        });

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == R.id.spinnerFilterLavoura) {
                    selectedLavouraId = position == 0 ? null : lavouraIds.get(position - 1);
                } else if (parent.getId() == R.id.spinnerFilterPanhador) {
                    selectedPanhadorId = position == 0 ? null : panhadorIds.get(position - 1);
                }
                listarColheitas();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerLavoura.setOnItemSelectedListener(filterListener);
        spinnerPanhador.setOnItemSelectedListener(filterListener);
    }

    private void loadSpinnerData() {
        String uid = FirebaseAuth.getInstance().getUid();
        
        // Load Lavouras
        dbFirestore.collection("lavouras").whereEqualTo("userId", uid).get().addOnSuccessListener(querySnapshot -> {
            List<String> names = new ArrayList<>();
            names.add("Todas as Lavouras");
            lavouraIds.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                names.add(doc.getString("nomeLavoura"));
                lavouraIds.add(doc.getId());
            }
            if (isAdded()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLavoura.setAdapter(adapter);
            }
        });

        // Load Funcionarios (Panhadores)
        dbFirestore.collection("funcionarios").whereEqualTo("userId", uid).get().addOnSuccessListener(querySnapshot -> {
            List<String> names = new ArrayList<>();
            names.add("Todos os Panhadores");
            panhadorIds.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                names.add(doc.getString("nomeFuncionario"));
                panhadorIds.add(doc.getId());
            }
            if (isAdded()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPanhador.setAdapter(adapter);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void listarColheitas() {
        if (colheitasListener != null) {
            colheitasListener.remove();
        }

        Query query = dbFirestore.collection("colheitas")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid());

        if (selectedLavouraId != null) {
            query = query.whereEqualTo("idLavoura", selectedLavouraId);
        }
        if (selectedPanhadorId != null) {
            query = query.whereEqualTo("idFuncionario", selectedPanhadorId);
        }
        if (selectedDate != null) {
            // Usa o filtro 'greaterThanOrEqualTo' e 'lessThan' para filtrar pelo dia, 
            // já que o campo 'data' no banco contém também a hora (HH:mm)
            query = query.whereGreaterThanOrEqualTo("data", selectedDate + " 00:00")
                         .whereLessThanOrEqualTo("data", selectedDate + " 23:59");
        }

        // Ordena da mais antiga para a mais recente
        query = query.orderBy("data", Query.Direction.ASCENDING);

        colheitasListener = query.addSnapshotListener((value, error) -> {
            if (!isAdded()) return;

            if (error != null) {
                Log.e("Firestore", "Erro ao escutar colheitas", error);
                Toast.makeText(getContext(), "Erro ao carregar colheitas", Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null) {
                linearLayout.removeAllViews();
                for (QueryDocumentSnapshot doc : value) {
                    String idL = doc.getString("idLavoura");
                    String idT = doc.getString("idTalhao");
                    String idF = doc.getString("idFuncionario");
                    Double qntd = doc.getDouble("quantidade");
                    String dataOriginal = doc.getString("data");
                    
                    // Formata a data de yyyy-MM-dd HH:mm para dd/MM/yyyy HH:mm para exibição
                    String dataFormatada = dataOriginal;
                    if (dataOriginal != null && dataOriginal.contains("-")) {
                        try {
                            String[] partes = dataOriginal.split(" ");
                            String[] dataPartes = partes[0].split("-");
                            dataFormatada = dataPartes[2] + "/" + dataPartes[1] + "/" + dataPartes[0] + (partes.length > 1 ? " " + partes[1] : "");
                        } catch (Exception e) {
                            dataFormatada = dataOriginal;
                        }
                    }

                    View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_list, linearLayout, false);
                    TextView titleView = itemView.findViewById(R.id.text_item_name);
                    titleView.setVisibility(View.GONE);
                    TextView descView = itemView.findViewById(R.id.text_item_description);

                    // Inicia com valores padrão enquanto busca os nomes
                    titleView.setText("Carregando...");
                    String finalDataFormatada = dataFormatada;
                    descView.setText(String.format(java.util.Locale.getDefault(),
                            "Qtd: %.1f | Data: %s", qntd != null ? qntd : 0.0, finalDataFormatada));
                    descView.setVisibility(View.VISIBLE);
                    linearLayout.addView(itemView);

                    final String[] nomeT = {"..."};
                    final String[] nomeF = {"..."};
                    final String[] nomeL = {"..."};

                    Runnable updateDetails = () -> descView.setText(String.format(java.util.Locale.getDefault(),
                            "Lavoura: %s\n\nTalhão: %s\n\nFunc.: %s\n\nQtd: %.2f\n\nData: %s",
                            nomeL[0], nomeT[0], nomeF[0], qntd != null ? qntd : 0.0, finalDataFormatada));

                    // Busca nomes relacionados
                    if (idL != null) {
                        dbFirestore.collection("lavouras").document(idL).get().addOnSuccessListener(snapL -> {
                            if (snapL.exists()) {
                                nomeL[0] = snapL.getString("nomeLavoura");
                                updateDetails.run();
                            }
                        });
                    }

                    if (idT != null) {
                        dbFirestore.collection("talhoes").document(idT).get().addOnSuccessListener(snapT -> {
                            if (snapT.exists()) {
                                nomeT[0] = snapT.getString("nomeTalhao");
                                updateDetails.run();
                            }
                        });
                    }

                    if (idF != null) {
                        dbFirestore.collection("funcionarios").document(idF).get().addOnSuccessListener(snapF -> {
                            if (snapF.exists()) {
                                nomeF[0] = snapF.getString("nomeFuncionario");
                                updateDetails.run();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (colheitasListener != null) {
            colheitasListener.remove();
        }
    }
}