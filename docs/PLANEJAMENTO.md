\# FullCarona - Planejamento e Guia da Equipe



Aplicativo Android desenvolvido nativamente em Java com XML (Android Studio) para caronas solidárias entre estudantes da faculdade\[cite: 8]. Utiliza banco de dados local SQLite via Room para funcionar offline na demonstração\[cite: 8].



\*\*Prazo final de entrega:\*\* 25/11/2026\[cite: 8]



\---



\## Escopo e Fluxo do App



O projeto conta com 9 telas principais\[cite: 8]:

1\. \*\*Início (`MainActivity`):\*\* Logo do app e botão Entrar que redireciona para o Login\[cite: 1, 8].

2\. \*\*Login (`LoginActivity`):\*\* Autenticação do usuário (com opção de lembrar e-mail e redefinir senha)\[cite: 1, 3]. Leva para a escolha de perfil ou direto para a Home caso o perfil já esteja definido\[cite: 3, 8].

3\. \*\*Escolha de Perfil (`EscolhaPerfilActivity`):\*\* Define se o usuário atuará como Motorista ou Passageiro\[cite: 1, 8].

4\. \*\*Cadastro de Endereço (`CadastroEnderecoActivity`):\*\* Fluxo do passageiro (rua, número e complemento opcional)\[cite: 5, 8].

5\. \*\*Cadastro de Veículo (`CadastroVeiculoActivity`):\*\* Fluxo do motorista (marca, placa nos formatos antigo ou Mercosul e modelo/ano)\[cite: 6, 8].

6\. \*\*Criar Conta (`CriarContaActivity`):\*\* Cadastro de novo estudante (nome, e-mail, curso e senha salva com hash SHA-256)\[cite: 2, 4, 8].

7\. \*\*Home do Passageiro (`HomePassageiroActivity`):\*\* Lista caronas com status `ABERTA` e permite solicitar vaga (evitando solicitações duplicadas)\[cite: 5, 8].

8\. \*\*Home do Motorista (`HomeMotoristaActivity`):\*\* Permite oferecer novas caronas e aceitar ou recusar solicitações recebidas (atualizando vagas e mudando status para `LOTADA` quando zerar)\[cite: 6, 8].

9\. \*\*Minhas Caronas (`MinhasCaronasActivity`):\*\* Histórico de caronas oferecidas (motorista) ou solicitadas com status `PENDENTE`, `ACEITA` ou `RECUSADA` (passageiro)\[cite: 3, 8].



\*Fora do escopo inicial (apenas se sobrar tempo): mapa/GPS, chat, notificações, avaliação de motorista e pagamento\[cite: 8].\*



\---



\## Divisão de Responsabilidades



\* \*\*Bia:\*\* Tela 1 (Início), Tela 6 (Criar conta), arquivo de tema/cores comum em `res/values`, gestão do Trello e checklist de testes\[cite: 2].

\* \*\*Gustavo:\*\* Tela 2 (Login), Tela 9 (Minhas caronas), classe `Sessao` (com `SharedPreferences` guardando apenas o ID do usuário), `README.md` e geração do APK final\[cite: 3].

\* \*\*Pedro:\*\* Configuração do Room (`AppDatabase`), todas as entidades, DAOs, os 5 Repositories, `SenhaUtil` (SHA-256) e Tela 3 (Escolha de perfil)\[cite: 4].

\* \*\*Wervley:\*\* Tela 4 (Cadastro de endereço) e Tela 7 (Home do passageiro + Adapter de caronas)\[cite: 5].

\* \*\*Lívia:\*\* Tela 5 (Cadastro de veículo) e Tela 8 (Home do motorista + Adapter de solicitações)\[cite: 6].



\---



\## Arquitetura de Dados (Room)



O banco usa `allowMainThreadQueries()` para simplificar o desenvolvimento dentro do prazo\[cite: 4, 9].



\### Entidades e Campos

\* \*\*Usuario:\*\* `id`, `nome`, `email`, `senhaHash`, `curso`, `perfil` (`MOTORISTA`, `PASSAGEIRO` ou vazio)\[cite: 9].

\* \*\*Endereco:\*\* `id`, `usuarioId`, `rua`, `numero`, `complemento` (opcional)\[cite: 9].

\* \*\*Veiculo:\*\* `id`, `usuarioId`, `marca`, `placa`, `modeloAno`\[cite: 9].

\* \*\*Carona:\*\* `id`, `motoristaId`, `origem`, `destino`, `dataHora`, `vagas`, `status` (`ABERTA`, `LOTADA`, `ENCERRADA`)\[cite: 9].

\* \*\*Solicitacao:\*\* `id`, `caronaId`, `passageiroId`, `status` (`PENDENTE`, `ACEITA`, `RECUSADA`)\[cite: 9].



\### Contrato dos Repositories

\* \*\*`UsuarioRepository`:\*\* `cadastrar(Usuario)`, `emailExiste(email)`, `login(email, senha)`, `buscarPorId(id)`, `definirPerfil(id, perfil)`, `redefinirSenha(email, novaSenha)`\[cite: 9].

\* \*\*`EnderecoRepository`:\*\* `salvar(Endereco)`, `buscarPorUsuario(usuarioId)`\[cite: 9].

\* \*\*`VeiculoRepository`:\*\* `salvar(Veiculo)`, `buscarPorUsuario(usuarioId)`\[cite: 9].

\* \*\*`CaronaRepository`:\*\* `criar(Carona)`, `listarAbertas()`, `listarDoMotorista(motoristaId)`, `buscarPorId(id)`, `diminuirVaga(caronaId)`\[cite: 9].

\* \*\*`SolicitacaoRepository`:\*\* `solicitar(caronaId, passageiroId)`, `jaSolicitou(caronaId, passageiroId)`, `listarDaCarona(caronaId)`, `listarDoPassageiro(passageiroId)`, `atualizarStatus(id, status)`\[cite: 9].



\---



\## Cronograma e Marcos



\* \*\*15/11:\*\* Estrutura base das Activities criadas e PR inicial para garantir compilação\[cite: 7, 10].

\* \*\*16/11:\*\* Tema comum, Room instalado e classe `Sessao` na `main`\[cite: 7, 10].

\* \*\*17/11:\*\* Banco de dados completo (Entidades, DAOs e Repositories) mesclado na `main`\[cite: 7, 10].

\* \*\*19/11:\*\* Fluxo de cadastro, login e escolha de perfil funcionando de ponta a ponta\[cite: 7, 10].

\* \*\*22/11:\*\* Fluxo de oferecer, solicitar e aceitar/recusar carona concluído\[cite: 7, 10].

\* \*\*22 e 23/11:\*\* Testes de integração cruzada entre os integrantes e abertura de bugs\[cite: 7, 10].

\* \*\*23/11:\*\* Congelamento de código (apenas correção de bugs a partir desta data)\[cite: 7, 10].

\* \*\*24/11:\*\* Testes finais, documentação fechada, geração do APK e ensaio da apresentação\[cite: 7, 10].

\* \*\*25/11:\*\* Entrega final\[cite: 7, 10].



\---



\## Regras de Git e Organização (Trello)



\* \*\*Fluxo do Trello:\*\* `Backlog > A fazer > Fazendo > Em revisão (PR) > Feito`\[cite: 10].

\* \*\*Branches e PRs:\*\* 1 cartão = 1 branch (ex: `tela-4-validacao`) = 1 Pull Request\[cite: 10].

\* \*\*Antes de abrir PR:\*\* Rodar `git pull origin main` e garantir que o app compila sem erros\[cite: 1, 10].

\* \*\*Conflitos e arquivos:\*\* Cada integrante altera apenas seus próprios arquivos\[cite: 1, 10]. No `AndroidManifest.xml`, adicionar apenas a tag `<activity>` correspondente\[cite: 1, 10].

\* \*\*Higiene do repositório:\*\* Nunca comitar a pasta `.idea` ou arquivos locais do Android Studio (sempre conferir com `git status`)\[cite: 1, 10].

\* \*\*Senhas:\*\* Nunca armazenar senhas em texto puro no banco ou no `SharedPreferences`\[cite: 3, 4].



\### Checklist para Entrega Final

\- \[ ] Todas as telas abrem pelo fluxo normal do app sem configurações manuais na IDE\[cite: 7].

\- \[ ] Campos obrigatórios exibem mensagens claras de validação quando vazios\[cite: 7].

\- \[ ] Dados gravados no banco refletem corretamente nas telas dependentes\[cite: 7].

\- \[ ] Repositório limpo (sem `.idea`) e `main` compilando sem erros após o último merge\[cite: 7].

