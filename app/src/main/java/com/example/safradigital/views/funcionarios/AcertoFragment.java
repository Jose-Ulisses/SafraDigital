package com.example.safradigital.views.funcionarios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safradigital.R;
import com.example.safradigital.db.Database;

public class AcertoFragment extends Fragment {

    private static final String ARG_FUNCIONARIO = "nomeFuncionario";
    private String nomeFuncionario;
    Database db;
    TextView mTextView;

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
        View view = inflater.inflate(R.layout.fragment_info, container, false);



        return view;
    }

}
