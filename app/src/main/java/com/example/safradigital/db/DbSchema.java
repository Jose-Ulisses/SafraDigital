package com.example.safradigital.db;

public class DbSchema {
    public static final class ColheitasTbl{
        public static final String NOME_TBL = "colheitas";
        public static final class Cols{
            public static final String ID_COLHEITA = "_id";
            public static final String ID_LAVOURA = "id_lavoura";
            public static final String ID_TALHAO = "id_talhao";
            public static final String ID_FUNCIONARIO = "id_funcionario";
            public static final String QNTD = "quantidade";
            public static final String DATA = "data";
        }
    }

    public static final class LavourasTbl{
        public static final String NOME_TBL = "lavouras";
        public static final class Cols{
            public static final String ID_LAVOURA = "_id";
            public static final String NOME_LAVOURA = "nome_lavoura";
            public static final String TOTAL_LAVOURA = "total_lavoura";
        }
    }

    public static final class TalhaoTbl{
        public static final String NOME_TBL = "talhao";
        public static final class Cols{
            public static final String ID_TALHAO = "_id";
            public static final String ID_LAVOURA_TALHAO = "id_lavoura";
            public static final String NOME_TALHAO = "nome_talhao";
            public static final String PRECO_TALHAO = "preco_talhao";
            public static final String TOTAL_TALHAO = "total_talhao";
        }
    }


    public static final class FuncionariosTbl{
        public static final String NOME_TBL = "funcionarios";
        public static final class Cols{
            public static final String ID_FUNCIONARIO = "_id";
            public static final String NOME_FUNCIONARIO = "nome_funcionario";
            public static final String CPF_FUNCIONARIO = "cpf_funcionario";
            public static final String TELEFONE_FUNCIONARIO = "telefone_funcionario";
            public static final String PIX_FUNCIONARIO = "pix_funcionario";
        }
    }

}
