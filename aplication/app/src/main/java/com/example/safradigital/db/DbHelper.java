package com.example.safradigital.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final int VERSAO = 1;
    private static final String DATABASE_NAME = "SafraDigitalDB";

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + DbSchema.ColheitasTbl.NOME_TBL + "(" +
                DbSchema.ColheitasTbl.Cols.ID_COLHEITA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSchema.ColheitasTbl.Cols.ID_LAVOURA + " INTEGER, " +
                DbSchema.ColheitasTbl.Cols.ID_TALHAO + " INTEGER, " +
                DbSchema.ColheitasTbl.Cols.ID_FUNCIONARIO + " INTEGER, " +
                DbSchema.ColheitasTbl.Cols.QNTD + " REAL, " +
                DbSchema.ColheitasTbl.Cols.DATA + " DATE, " +
                "FOREIGN KEY(" + DbSchema.ColheitasTbl.Cols.ID_LAVOURA + ") " +
                "REFERENCES " + DbSchema.LavourasTbl.NOME_TBL + "(" + DbSchema.LavourasTbl.Cols.ID_LAVOURA + "), " +
                "FOREIGN KEY(" + DbSchema.ColheitasTbl.Cols.ID_TALHAO + ") " +
                "REFERENCES " + DbSchema.TalhaoTbl.NOME_TBL + "(" + DbSchema.TalhaoTbl.Cols.ID_TALHAO + "), " +
                "FOREIGN KEY(" + DbSchema.ColheitasTbl.Cols.ID_FUNCIONARIO + ") " +
                "REFERENCES " + DbSchema.FuncionariosTbl.NOME_TBL + "(" + DbSchema.FuncionariosTbl.Cols.ID_FUNCIONARIO + ")" + ")"
        );


        db.execSQL("CREATE TABLE " + DbSchema.LavourasTbl.NOME_TBL + "(" +
                DbSchema.LavourasTbl.Cols.ID_LAVOURA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSchema.LavourasTbl.Cols.NOME_LAVOURA + " TEXT, " +
                DbSchema.LavourasTbl.Cols.TOTAL_LAVOURA + " REAL" + ")"
        );


        db.execSQL("CREATE TABLE " + DbSchema.TalhaoTbl.NOME_TBL + "(" +
                DbSchema.TalhaoTbl.Cols.ID_TALHAO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSchema.TalhaoTbl.Cols.ID_LAVOURA_TALHAO + " INTEGER, " +
                DbSchema.TalhaoTbl.Cols.NOME_TALHAO + " TEXT, " +
                DbSchema.TalhaoTbl.Cols.PRECO_TALHAO + " INTEGER, " +
                DbSchema.TalhaoTbl.Cols.TOTAL_TALHAO + " REAL, " +
                "FOREIGN KEY(" + DbSchema.TalhaoTbl.Cols.ID_LAVOURA_TALHAO + ") " +
                "REFERENCES " + DbSchema.LavourasTbl.NOME_TBL + "(" + DbSchema.LavourasTbl.Cols.ID_LAVOURA + ")" + ")"
        );


        db.execSQL("CREATE TABLE " + DbSchema.FuncionariosTbl.NOME_TBL + "(" +
                DbSchema.FuncionariosTbl.Cols.ID_FUNCIONARIO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSchema.FuncionariosTbl.Cols.NOME_FUNCIONARIO + " TEXT, " +
                DbSchema.FuncionariosTbl.Cols.CPF_FUNCIONARIO + " TEXT, " +
                DbSchema.FuncionariosTbl.Cols.TELEFONE_FUNCIONARIO + " TEXT, " +
                DbSchema.FuncionariosTbl.Cols.PIX_FUNCIONARIO + " TEXT" + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int olderVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + DbSchema.ColheitasTbl.NOME_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + DbSchema.LavourasTbl.NOME_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + DbSchema.TalhaoTbl.NOME_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + DbSchema.FuncionariosTbl.NOME_TBL);
        onCreate(db);
    }
}
