# bank_management
## Visão Geral do Sistema

Este sistema consiste em uma aplicação cliente-servidor para gerenciar contas bancárias básicas. Ele permite aos usuários criar contas, realizar depósitos, saques, verificar o saldo e consultar o histórico de transações. O sistema utiliza Sockets para a comunicação entre o cliente e o servidor e um banco de dados SQLite para persistir os dados.

## Componentes do Sistema

  `Servidor.java`: O servidor é a parte central da aplicação. Ele aguarda a conexão de clientes na porta 12345. Para cada cliente que se conecta, ele cria uma nova thread (ServidorThread) para lidar com as solicitações. O servidor se conecta a um banco de dados SQLite (base.db) e cria duas tabelas, usuarios e transacoes, se elas ainda não existirem.

  `ServidorThread.java`: Esta classe implementa a lógica do servidor para cada cliente conectado. Ela lê a opção escolhida pelo cliente e, com base nessa opção, realiza a operação correspondente, como criar uma conta, fazer um depósito, saque, ou consultar o saldo/histórico.

  `Base.java`: Contém os métodos estáticos que interagem diretamente com o banco de dados.

    criarTabela(): Cria a tabela usuarios com colunas para id, name, dinheiro e senha.

    criarTabelaTransacoes(): Cria a tabela transacoes com colunas para id_usuario, tipo_transacao, valor_transacao e data_transacao.

    depositoCliente(): Realiza um depósito na conta de um usuário, verificando as credenciais e atualizando o saldo. Também registra a transação na tabela transacoes.

    saqueCliente(): Permite o saque de um valor, verificando se o usuário tem saldo suficiente e credenciais válidas. Ele atualiza o saldo e registra a transação.

    mostrarSaldo(): Retorna o saldo atual de um usuário com base no ID.

    mostrarHistorico(): Consulta e retorna o histórico de transações de um usuário específico.

  `Cliente.java`: A interface de linha de comando para o usuário. O cliente se conecta ao servidor, apresenta um menu de opções e envia as informações necessárias para o servidor, como ID, senha e valores de transação. Ele recebe e exibe as respostas do servidor.

  `Transacao.java`: Uma classe simples que representa uma transação, contendo o tipo, valor e data da transação. É serializável para ser enviada entre o cliente e o servidor.

## Funcionalidades

O sistema suporta as seguintes operações, acessíveis através do menu do cliente:

    Criar Conta: Solicita ID, nome e senha para criar um novo usuário no banco de dados. A conta é criada com um saldo inicial de 0.

    Depósito: Permite ao usuário depositar dinheiro em sua conta. O sistema verifica as credenciais do usuário antes de realizar a operação.

    Saque: Permite ao usuário sacar dinheiro. O sistema verifica as credenciais e se o saldo é suficiente antes de processar o saque.

    Checar Histórico de Transações: Exibe uma lista de todas as transações (saques e depósitos) de um usuário, com tipo, valor e data.

    Checar Saldo da Conta: Mostra o saldo atual da conta de um usuário específico.

    Sair: Encerra a aplicação do cliente.

## Estrutura do Banco de Dados

O sistema utiliza duas tabelas no banco de dados SQLite:

    usuarios:

        id (INTEGER)

        name (STRING)

        dinheiro (INTEGER)

        senha (STRING)

    transacoes:

        id_usuario (INTEGER)

        tipo_transacao (STRING)

        valor_transacao (INTEGER)

        data_transacao (TIMESTAMP)

  A coluna data_transacao é preenchida automaticamente com o horário atual quando uma transação é registrada.
