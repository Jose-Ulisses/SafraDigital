package com.example.safradigital.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public Database(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DbHelper(mContext).getWritableDatabase();
    }


    //----COLHEITA----
    public void addColheita(int idLavoura, int idTalhao, int idFuncionario, float qntd, String data){
        ContentValues colheitaValues = new ContentValues();

        colheitaValues.put(DbSchema.ColheitasTbl.Cols.ID_LAVOURA, idLavoura);
        colheitaValues.put(DbSchema.ColheitasTbl.Cols.ID_TALHAO, idTalhao);
        colheitaValues.put(DbSchema.ColheitasTbl.Cols.ID_FUNCIONARIO, idFuncionario);
        colheitaValues.put(DbSchema.ColheitasTbl.Cols.QNTD, qntd);
        colheitaValues.put(DbSchema.ColheitasTbl.Cols.DATA, data);

        mDatabase.insert(DbSchema.ColheitasTbl.NOME_TBL, null, colheitaValues);
    }

    public Cursor getAllColheitas(){
        String sql = "SELECT * FROM " + DbSchema.ColheitasTbl.NOME_TBL;

        return mDatabase.rawQuery(sql, null);
    }


    //----LAVOURA----
    public void addLavoura(String nomeLavoura){
        ContentValues lavouraValues = new ContentValues();
        lavouraValues.put(DbSchema.LavourasTbl.Cols.NOME_LAVOURA, nomeLavoura);
        lavouraValues.put(DbSchema.LavourasTbl.Cols.TOTAL_LAVOURA, 0);

        mDatabase.insert(DbSchema.LavourasTbl.NOME_TBL, null, lavouraValues);
    }

    public Cursor getAllLavouras(){
        String sql = "SELECT * FROM " + DbSchema.LavourasTbl.NOME_TBL;

        return mDatabase.rawQuery(sql, null);
    }

    public int getIdLavouraByName(String lavoura) {
        int idLavoura = -1;

        String sql =
                "SELECT " + DbSchema.LavourasTbl.Cols.ID_LAVOURA +
                        " FROM " + DbSchema.LavourasTbl.NOME_TBL +
                        " WHERE " + DbSchema.LavourasTbl.Cols.NOME_LAVOURA + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{lavoura});
        c.moveToFirst();
        idLavoura = c.getInt(c.getColumnIndexOrThrow(DbSchema.LavourasTbl.Cols.ID_LAVOURA));
        c.close();
        return idLavoura;
    }


    //----TALHAO----
    public void addTalhao(String nomeTalhao, int idLavoura){
        ContentValues talhaoValues = new ContentValues();
        talhaoValues.put(DbSchema.TalhaoTbl.Cols.NOME_TALHAO, nomeTalhao);
        talhaoValues.put(DbSchema.TalhaoTbl.Cols.ID_LAVOURA_TALHAO, idLavoura);

        mDatabase.insert(DbSchema.TalhaoTbl.NOME_TBL, null, talhaoValues);
    }

    public Cursor getAllTalhoes(){
        String sql = "SELECT * FROM " + DbSchema.TalhaoTbl.NOME_TBL;

        return mDatabase.rawQuery(sql, null);
    }


    //----FUNCIONARIO----
    public void addFuncionario(String nomeFuncionario, String cpf, String telefone, String pix){
        ContentValues funcionarioValues = new ContentValues();
        funcionarioValues.put(DbSchema.FuncionariosTbl.Cols.NOME_FUNCIONARIO, nomeFuncionario);
        funcionarioValues.put(DbSchema.FuncionariosTbl.Cols.CPF_FUNCIONARIO, cpf);
        funcionarioValues.put(DbSchema.FuncionariosTbl.Cols.TELEFONE_FUNCIONARIO, telefone);
        funcionarioValues.put(DbSchema.FuncionariosTbl.Cols.PIX_FUNCIONARIO, pix);

        mDatabase.insert(DbSchema.FuncionariosTbl.NOME_TBL, null, funcionarioValues);
    }

    public Cursor getAllFuncionarios(){
        String sql = "SELECT * FROM " + DbSchema.FuncionariosTbl.NOME_TBL;

        return mDatabase.rawQuery(sql, null);
    }
}