package com.example.safradigital.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {
    private SQLiteDatabase mDatabase;
    private final DbHelper mDbHelper;

    public Database(Context context) {
        mDbHelper = new DbHelper(context.getApplicationContext());
        open();
    }

    private void open() {
        if (mDatabase == null || !mDatabase.isOpen()) {
            mDatabase = mDbHelper.getWritableDatabase();
        }
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

    public Cursor getAcerto(int id){
        String sql = "SELECT " +
                DbSchema.ColheitasTbl.Cols.ID_LAVOURA + ", " +
                DbSchema.ColheitasTbl.Cols.ID_TALHAO + ", " +
                DbSchema.ColheitasTbl.Cols.QNTD + ", " +
                "SUM(" + DbSchema.ColheitasTbl.Cols.QNTD + ") AS total" +
                " FROM " + DbSchema.ColheitasTbl.NOME_TBL +
                " WHERE " + DbSchema.ColheitasTbl.Cols.ID_FUNCIONARIO + " = ?" +
                " GROUP BY " + DbSchema.ColheitasTbl.Cols.ID_LAVOURA + ", " +
                DbSchema.ColheitasTbl.Cols.ID_TALHAO;

        return mDatabase.rawQuery(sql, new String[]{String.valueOf(id)});
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

    public int getLavouraIdByName(String lavoura) {
        String sql =
                "SELECT " + DbSchema.LavourasTbl.Cols.ID_LAVOURA +
                        " FROM " + DbSchema.LavourasTbl.NOME_TBL +
                        " WHERE " + DbSchema.LavourasTbl.Cols.NOME_LAVOURA + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{lavoura});
        c.moveToFirst();
        int idLavoura = c.getInt(c.getColumnIndexOrThrow(DbSchema.LavourasTbl.Cols.ID_LAVOURA));
        c.close();
        return idLavoura;
    }

    public String getLavouraNameById(int id) {
        String sql =
                "SELECT " + DbSchema.LavourasTbl.Cols.NOME_LAVOURA +
                        " FROM " + DbSchema.LavourasTbl.NOME_TBL +
                        " WHERE " + DbSchema.LavourasTbl.Cols.ID_LAVOURA + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{String.valueOf(id)});
        c.moveToFirst();
        String nomeLavoura = c.getString(c.getColumnIndexOrThrow(DbSchema.LavourasTbl.Cols.NOME_LAVOURA));
        c.close();
        return nomeLavoura;
    }


    public float getTotalLavoura(int id){
        String sql = "SELECT " + DbSchema.LavourasTbl.Cols.TOTAL_LAVOURA +
                " FROM " + DbSchema.LavourasTbl.NOME_TBL +
                " WHERE " + DbSchema.LavourasTbl.Cols.ID_LAVOURA + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{String.valueOf(id)});
        c.moveToFirst();
        float total = c.getFloat(c.getColumnIndexOrThrow(DbSchema.LavourasTbl.Cols.TOTAL_LAVOURA));
        c.close();

        return total;
    }

    public void insertColheitaLavoura(int idLavoura, float qntd){
        ContentValues values = new ContentValues();
        float total = getTotalLavoura(idLavoura);
        total += qntd;

        values.put(DbSchema.LavourasTbl.Cols.TOTAL_LAVOURA, total);
        mDatabase.update(DbSchema.LavourasTbl.NOME_TBL, values,
                DbSchema.LavourasTbl.Cols.ID_LAVOURA + " = ?", new String[]{String.valueOf(idLavoura)});
    }


    //----TALHAO----
    public void addTalhao(String nomeTalhao, int preco, int idLavoura){
        ContentValues talhaoValues = new ContentValues();
        talhaoValues.put(DbSchema.TalhaoTbl.Cols.ID_LAVOURA_TALHAO, idLavoura);
        talhaoValues.put(DbSchema.TalhaoTbl.Cols.NOME_TALHAO, nomeTalhao);
        talhaoValues.put(DbSchema.TalhaoTbl.Cols.PRECO_TALHAO, preco);
        talhaoValues.put(DbSchema.TalhaoTbl.Cols.TOTAL_TALHAO, 0.0);

        mDatabase.insert(DbSchema.TalhaoTbl.NOME_TBL, null, talhaoValues);
    }

    public int getTalhaoIdByName(String talhao) {
        String sql = "SELECT " + DbSchema.TalhaoTbl.Cols.ID_TALHAO +
                        " FROM " + DbSchema.TalhaoTbl.NOME_TBL +
                        " WHERE " + DbSchema.TalhaoTbl.Cols.NOME_TALHAO + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{talhao});
        c.moveToFirst();
        int idTalhao = c.getInt(c.getColumnIndexOrThrow(DbSchema.TalhaoTbl.Cols.ID_TALHAO));
        c.close();
        return idTalhao;
    }

    public String getTalhaoNameById(int id) {

        String sql = "SELECT " + DbSchema.TalhaoTbl.Cols.NOME_TALHAO +
                " FROM " + DbSchema.TalhaoTbl.NOME_TBL +
                " WHERE " + DbSchema.TalhaoTbl.Cols.ID_TALHAO + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{String.valueOf(id)});
        c.moveToFirst();
        String talhao = c.getString(c.getColumnIndexOrThrow(DbSchema.TalhaoTbl.Cols.NOME_TALHAO));
        c.close();

        return talhao;
    }

    public Cursor getAllTalhoesByLavouraId(int idLavoura){
        String sql = "SELECT " + "* " +
                " FROM " + DbSchema.TalhaoTbl.NOME_TBL +
                " WHERE " + DbSchema.TalhaoTbl.Cols.ID_LAVOURA_TALHAO + " = ?";

        return mDatabase.rawQuery(sql, new String[]{String.valueOf(idLavoura)});
    }

    public float getTotalTalhao(int idTalhao){
        String sql = "SELECT " + DbSchema.TalhaoTbl.Cols.TOTAL_TALHAO +
                " FROM " + DbSchema.TalhaoTbl.NOME_TBL +
                " WHERE " + DbSchema.TalhaoTbl.Cols.ID_TALHAO + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{String.valueOf(idTalhao)});
        c.moveToFirst();
        float total = c.getFloat(c.getColumnIndexOrThrow(DbSchema.TalhaoTbl.Cols.TOTAL_TALHAO));
        c.close();

        return total;
    }

    public int getPrecoTalhao(int id){
        String sql = "SELECT " +
                DbSchema.TalhaoTbl.Cols.PRECO_TALHAO +
                " FROM " + DbSchema.TalhaoTbl.NOME_TBL +
                " WHERE " + DbSchema.TalhaoTbl.Cols.ID_TALHAO + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{String.valueOf(id)});
        c.moveToFirst();
        int preco = c.getInt(c.getColumnIndexOrThrow(DbSchema.TalhaoTbl.Cols.PRECO_TALHAO));
        c.close();

        return preco;
    }

    public void insertColheitaTalhao(int idTalhao, float qntd){
        ContentValues values = new ContentValues();
        float total = getTotalTalhao(idTalhao);
        total += qntd;

        values.put(DbSchema.TalhaoTbl.Cols.TOTAL_TALHAO, total);
        mDatabase.update(DbSchema.TalhaoTbl.NOME_TBL, values,
                DbSchema.TalhaoTbl.Cols.ID_TALHAO + " = ?", new String[]{String.valueOf(idTalhao)});
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

    public Cursor getFuncionario(int id){
        String sql = "SELECT " + "* " +
                " FROM " + DbSchema.FuncionariosTbl.NOME_TBL +
                " WHERE " + DbSchema.FuncionariosTbl.Cols.ID_FUNCIONARIO + " = ?";

        return mDatabase.rawQuery(sql, new String[]{String.valueOf(id)});
    }

    public Cursor getAllFuncionarios(){
        String sql = "SELECT * FROM " + DbSchema.FuncionariosTbl.NOME_TBL;

        return mDatabase.rawQuery(sql, null);
    }

    public Cursor getAllFuncionariosName(){
        String sql = "SELECT " + DbSchema.FuncionariosTbl.Cols.NOME_FUNCIONARIO +
                " FROM " + DbSchema.FuncionariosTbl.NOME_TBL;

        return mDatabase.rawQuery(sql, null);
    }

    public int getFuncionarioIdByName(String funcionario) {
        int idFuncionario;

        String sql =
                "SELECT " + DbSchema.FuncionariosTbl.Cols.ID_FUNCIONARIO +
                        " FROM " + DbSchema.FuncionariosTbl.NOME_TBL +
                        " WHERE " + DbSchema.FuncionariosTbl.Cols.NOME_FUNCIONARIO + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{funcionario});
        c.moveToFirst();
        idFuncionario = c.getInt(c.getColumnIndexOrThrow(DbSchema.FuncionariosTbl.Cols.ID_FUNCIONARIO));
        c.close();
        return idFuncionario;
    }

    public String getFuncionarioNameById(int id) {
        String sql =
                "SELECT " + DbSchema.FuncionariosTbl.Cols.NOME_FUNCIONARIO +
                        " FROM " + DbSchema.FuncionariosTbl.NOME_TBL +
                        " WHERE " + DbSchema.FuncionariosTbl.Cols.ID_FUNCIONARIO + " = ?";

        Cursor c = mDatabase.rawQuery(sql, new String[]{String.valueOf(id)});
        c.moveToFirst();
        String nomeFuncionario = c.getString(c.getColumnIndexOrThrow(DbSchema.FuncionariosTbl.Cols.NOME_FUNCIONARIO));
        c.close();
        return nomeFuncionario;
    }
}