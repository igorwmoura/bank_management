package org.example;

import java.sql.*;
import java.util.*;

public class Base {
    private static final Object lock = new Object();
    public static void criarTabela(Connection conexao) throws SQLException {
        Statement statement = conexao.createStatement();

        // Utilize IF NOT EXISTS para criar a tabela somente se ela ainda não existir
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS usuarios (id INTEGER, name STRING, dinheiro INTEGER, senha STRING)");
    }
    public static void criarTabelaTransacoes(Connection conexao) throws SQLException {
        Statement statement = conexao.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS transacoes (id_usuario INTEGER, tipo_transacao STRING, valor_transacao INTEGER, data_transacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
    }
    public static List<List<Transacao>> mostrarHistorico(int idHistorico, Connection conexao) throws SQLException {

        synchronized (lock) {
            Map<Integer, List<Transacao>> historicos = new HashMap<>();
            String sql = "SELECT * FROM transacoes WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
                pstmt.setInt(1, idHistorico);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    int idUsuario = rs.getInt("id_usuario");
                    String tipoTransacao = rs.getString("tipo_transacao");
                    int valorTransacao = rs.getInt("valor_transacao");
                    String dataTransacao = rs.getString("data_transacao");

                    Transacao transacao = new Transacao(tipoTransacao, valorTransacao, dataTransacao);

                    // Verifica se já existe uma lista de transações para o usuário
                    historicos.computeIfAbsent(idUsuario, k -> new ArrayList<>()).add(transacao);

                    System.out.println("Tipo de Transação: " + tipoTransacao);
                    System.out.println("Valor da Transação: " + valorTransacao);
                    System.out.println("Data da Transação: " + dataTransacao);
                    System.out.println("------");
            }return new ArrayList<>(historicos.values());
        }
    }
}
    public static void registrarTransacao(int idUsuario, String tipoTransacao, int valorTransacao, Connection conexao) throws SQLException {
        String sql = "INSERT INTO transacoes (id_usuario, tipo_transacao, valor_transacao) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.setString(2, tipoTransacao);
            pstmt.setInt(3, valorTransacao);
            pstmt.executeUpdate();
        }
    }
    public static String saqueCliente(int idSaque,String senha, int valorSaque,Connection conexao) throws SQLException {

        synchronized (lock) {
            // Verificar se o ID e a senha correspondem no banco de dados
            if (verificarCredenciais(idSaque, senha, conexao)) {
                // Consulta para verificar se o usuário existe e possui saldo suficiente para o saque
                PreparedStatement pstmtVerificarSaque = conexao.prepareStatement("SELECT dinheiro FROM usuarios WHERE id = ? AND dinheiro >= ?");
                pstmtVerificarSaque.setInt(1, idSaque);
                pstmtVerificarSaque.setInt(2, valorSaque);
                ResultSet rs = pstmtVerificarSaque.executeQuery();

                if (rs.next()) {
                    // Consulta para atualizar o saldo do usuário existente após o saque
                    PreparedStatement pstmtAtualizarSaque = conexao.prepareStatement("UPDATE usuarios SET dinheiro = dinheiro - ? WHERE id = ?");
                    pstmtAtualizarSaque.setInt(1, valorSaque);
                    pstmtAtualizarSaque.setInt(2, idSaque);
                    pstmtAtualizarSaque.executeUpdate();

                    // Registrar a transação na tabela de transações
                    registrarTransacao(idSaque, "saque", valorSaque, conexao);

                    System.out.println("Saque de " + valorSaque + " realizado com sucesso.");
                    return "Saque de " + valorSaque + " realizado com sucesso.";
            }else {
                System.out.println("Usuário não encontrado ou saldo insuficiente para realizar o saque.");
                return "Usuário não encontrado ou saldo insuficiente para realizar o saque.";
            }
        }else {
                System.out.println("Credenciais inválidas. O saque não pôde ser realizado.");
                return "Credenciais inválidas. O saque não pôde ser realizado.";
            }
    }
}
    public static String depositoCliente(int idDeposito,String senha, int valorDeposito, Connection conexao) throws SQLException {

        synchronized (lock) {
            // Verificar se o ID e a senha correspondem no banco de dados
            if (verificarCredenciais(idDeposito, senha, conexao)) {
                // Consulta para verificar se o usuário existe
                PreparedStatement pstmtVerificarDeposito = conexao.prepareStatement("SELECT dinheiro FROM usuarios WHERE id = ?");
                pstmtVerificarDeposito.setInt(1, idDeposito);
                ResultSet rs = pstmtVerificarDeposito.executeQuery();

                if (rs.next()) {
                    // Consulta para atualizar o saldo do usuário existente após o depósito
                    PreparedStatement pstmtAtualizarDeposito = conexao.prepareStatement("UPDATE usuarios SET dinheiro = dinheiro + ? WHERE id = ?");
                    pstmtAtualizarDeposito.setInt(1, valorDeposito);
                    pstmtAtualizarDeposito.setInt(2, idDeposito);
                    pstmtAtualizarDeposito.executeUpdate();

                    // Registrar a transação na tabela de transações
                    registrarTransacao(idDeposito, "deposito", valorDeposito, conexao);
                    System.out.println("Depósito de " + valorDeposito + " realizado com sucesso.");
                    return "Depósito de " + valorDeposito + " realizado com sucesso para o usuário com ID " + idDeposito + ".";
                }else {
                    System.out.println("Usuário não encontrado. O depósito não pôde ser realizado.");}
                    return "Usuário não encontrado. O depósito não pôde ser realizado.";
            }else {
                System.out.println("Credenciais inválidas. O depósito não pôde ser realizado.");
                return "Credenciais inválidas. O depósito não pôde ser realizado.";
            }
        }
    }

    // Função para verificar as credenciais (ID e senha) no banco de dados
    private static boolean verificarCredenciais(int id, String senha, Connection conexao) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE id = ? AND senha = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, senha);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Se count for maior que 0, as credenciais são válidas
            }
        }
        return false;
    }
}