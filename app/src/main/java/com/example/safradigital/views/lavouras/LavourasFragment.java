package com.example.safradigital.views.lavouras;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.safradigital.R;
import com.example.safradigital.views.lavouras.talhao.AddTalhaoFragment;

public class LavourasFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lavouras, container, false);

        Button btnAddLavoura = view.findViewById(R.id.button_add_lavoura);
        btnAddLavoura.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddLavouraFragment())
                    .addToBackStack(null)
                    .commit();
        });

        Button btnViewLavouras = view.findViewById(R.id.button_view_lavouras);
        btnViewLavouras.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new InfoLavourasFragment())
                    .addToBackStack(null)
                    .commit();
        });

        Button btnAddTalhao = view.findViewById(R.id.button_add_talhao);
        btnAddTalhao.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddTalhaoFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}