package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.List;

public class Servidor {

    public static void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(12345); // Porta do servidor

            System.out.println("Servidor esperando por conexões na porta 12345...");

            while (true) {
                Socket socket = serverSocket.accept(); // Aguarda um cliente se conectar
                System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());

                Thread t = new Thread(new ServidorThread(socket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ServidorThread implements Runnable {

    private final Socket socket;
    private Connection conexao;

    public ServidorThread(Socket socket) {
        this.socket = socket;

        try {
            // Cria a conexão com o banco de dados
            conexao = DriverManager.getConnection("jdbc:sqlite:base.db");
            Base.criarTabela(conexao);
            Base.criarTabelaTransacoes(conexao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {

            int opcao = inputStream.readInt();

            switch (opcao) {
                case 1 -> {
                    // Recebe os dados do cliente
                    int id = inputStream.readInt();
                    String nome = (String) inputStream.readObject();
                    String senha = (String) inputStream.readObject();

                    // Executa a operação de inserção na tabela
                    inserirNaTabela(id, nome, senha, conexao);
                }
                case 2 -> {
                    // Recebe dados do cliente para deposito
                    // Execute operações de depósito no banco de dados
                    int idDeposito = inputStream.readInt();
                    String senhaDeposito = (String) inputStream.readObject(); // Recebe a senha do cliente
                    int valorDeposito = inputStream.readInt();
                    String resposta = Base.depositoCliente(idDeposito, senhaDeposito,valorDeposito, conexao);

                    // Envie a resposta para o cliente
                    outputStream.writeObject(resposta);
                    outputStream.flush();
                }
                case 3 -> {
                    // Recebe dados do cliente para saque
                    // Execute operações de saque no banco de dados
                    int idSaque = inputStream.readInt();
                    String senhaSaque = (String) inputStream.readObject(); // Recebe a senha do cliente
                    int valorSaque = inputStream.readInt();
                    String resposta = Base.saqueCliente(idSaque,senhaSaque,valorSaque, conexao);
                    // Envie uma resposta para o cliente
                    outputStream.writeObject(resposta);
                    outputStream.flush();
                }
                case 4 -> {
                    // Recebe dados do cliente para verificar histórico
                    // Execute operações de histórico no banco de dados
                    int idHistorico = inputStream.readInt();
                    // Envie uma resposta para o cliente
                    List<List<Transacao>> transacoes = Base.mostrarHistorico(idHistorico, conexao);
                    outputStream.writeObject(transacoes);
                    outputStream.flush();
                }
                case 5 -> {
                    // Recebe dados do cliente para verificar histórico
                    // Execute operações de histórico no banco de dados
                    int idSaldo = inputStream.readInt();
                    // Envie uma resposta para o cliente
                    String saldo = Base.mostrarSaldo(idSaldo, conexao);
                    outputStream.writeObject(saldo);
                    outputStream.flush();
                }
                default -> System.out.println("Opção inválida.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
                conexao.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void inserirNaTabela(int id, String nome, String senha, Connection conexao) throws SQLException {
        String sql = "INSERT INTO usuarios (id, name, dinheiro, senha) VALUES (?, ?, ?, ?)";
        try (conexao; PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, nome);
            pstmt.setInt(3, 0);
            pstmt.setString(4, senha);
            pstmt.executeUpdate();
        }
    }
}
