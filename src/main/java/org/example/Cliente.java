package org.example;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static java.lang.System.exit;

public class Cliente {

    public static void main(String[] args) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(true){
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())){

                System.out.println("Escolha uma operação:");
                System.out.println("1) Criar Conta");
                System.out.println("2) Deposito");
                System.out.println("3) Saque");
                System.out.println("4) Checar Histórico de Transações");
                System.out.println("5) Sair");

                int opcao = Integer.parseInt(reader.readLine());

                // Verifica se o socket está conectado antes de enviar dados
                if (socket.isConnected() && !socket.isClosed()) {
                    // Envie a opção escolhida para o servidor
                    outputStream.writeInt(opcao);
                    outputStream.flush();
                } else {
                    System.out.println("Conexão perdida. Saindo...");
                    break;
                }
                switch (opcao) {
                    case 1 -> {
                        // Para inserção na tabela, solicite os dados do usuário
                        System.out.println("Digite o ID: ");
                        int id = Integer.parseInt(reader.readLine());
                        System.out.println("Digite o nome: ");
                        String nome = reader.readLine();
                        System.out.println("Digite a senha: ");
                        String senha = reader.readLine();

                        // Envie os dados para o servidor
                        outputStream.writeInt(id);
                        outputStream.writeObject(nome);
                        outputStream.writeObject(senha);
                        outputStream.flush();
                    }
                    case 2, 3 -> {
                        // Para depósito e saque, solicite o ID do usuário e o valor da transação
                        System.out.println("Digite seu ID: ");
                        int idUsuario = Integer.parseInt(reader.readLine());
                        System.out.println("Digite sua senha: ");
                        String senha = reader.readLine(); // Solicita a senha ao usuário
                        System.out.println("Digite o valor da transação: ");
                        int valorTransacao = Integer.parseInt(reader.readLine());

                        // Envie o ID do usuário e o valor da transação para o servidor
                        outputStream.writeInt(idUsuario);
                        outputStream.writeObject(senha); // Envia a senha para o servidor
                        outputStream.writeInt(valorTransacao);
                        outputStream.flush();
                        // Receber a resposta do servidor
                        String resposta = (String) inputStream.readObject();
                        System.out.println("Resposta do servidor: " + resposta);
                    }
                    case 4 -> {
                        // Para verificar o histórico, solicite o ID do usuário
                        System.out.println("Digite seu ID: ");
                        int idHistorico = Integer.parseInt(reader.readLine());

                        // Envie o ID do usuário para o servidor
                        outputStream.writeInt(idHistorico);
                        outputStream.flush();

                        // Receber a lista de transações do servidor
                        List<List<Transacao>> historicos = (List<List<Transacao>>)inputStream.readObject();

                        for (List<Transacao> transacoes : historicos) {
                            for (Transacao transacao : transacoes) {
                                System.out.println("Tipo de Transação: " + transacao.getTipoTransacao());
                                System.out.println("Valor da Transação: " + transacao.getValorTransacao());
                                System.out.println("Data da Transação: " + transacao.getDataTransacao());
                                System.out.println("------");
                            }
                        }
                    }
                    case 5 -> exit(0);
                    default -> System.out.println("Opção inválida.");
            }
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
    }
    }
}
}