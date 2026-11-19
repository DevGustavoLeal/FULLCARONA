# FullCarona

Aplicativo Android nativo em Java com banco de dados local Room/SQLite voltado para caronas universitárias acessíveis. O fluxo implementado cobre criação de conta, autenticação segura, escolha de perfil, endereço do passageiro, veículo do motorista, oferta e solicitação de caronas, decisão do motorista e acompanhamento de rotas.

## Funcionalidades Implementadas

### Para Passageiros
- **Busca de Caronas:** Visualização de caronas abertas com origem, destino, data/hora e vagas disponíveis.
- **Filtro e Solicitação:** Filtragem por origem/destino e reserva de vaga com atualização em tempo real.
- **Minhas Caronas:** Acompanhamento do status das solicitações e atalho direto para abrir a rota de origem até o destino no mapa.

### Para Motoristas
- **Oferta de Carona:** Cadastro de trajetos com origem livre, destino, seletor nativo de data e hora (`DatePickerDialog` e `TimePickerDialog`) e controle de vagas.
- **Modalidades e Custo Estudantil:** Opções para **Moto** (1 vaga, sugestão de R$ 2,50), **Carona Gratuita** (R$ 0,00) ou **Carro** com sugestão acessível (R$ 1,50 base + R$ 1,50 por vaga).
- **Gestão de Solicitações:** Aceite ou recusa de pedidos pendentes e encerramento automático como `LOTADA` ao preencher a última vaga.
- **Minhas Caronas:** Visualização unificada de solicitações pendentes, caronas aceitas e botão de atalho para rota origem -> destino.

## Tecnologias Utilizadas

- **Linguagem:** Java (JDK 17+)
- **IDE:** Android Studio
- **Persistência Local:** Room / SQLite (`Usuario`, `Endereco`, `Veiculo`, `Carona`, `Solicitacao`) com DAOs e Repositories
- **Sessão e Segurança:** `SharedPreferences` (`Sessao`) e hash de senha com `PBKDF2-HMAC-SHA256` com salt
- **Interface:** Telas programáticas com componentes nativos Android (`TelaUi`, `AppCompatActivity`, `DatePickerDialog`, `TimePickerDialog`)

## Dados de Demonstração

Na primeira criação do banco local, o app inclui contas e três caronas futuras para facilitar os testes:

- **Motorista:** `motorista@fullcarona.app` / `Motorista123`
- **Passageiro:** `passageiro@fullcarona.app` / `Passageiro123`

Contas novas podem ser criadas pela tela de login. Os dados ficam armazenados localmente no dispositivo.

## Fluxos do Aplicativo

1. **Início -> Entrar:** Login ou criação de nova conta.
2. **Seleção de Perfil:** Contas novas escolhem entre Passageiro ou Motorista.
3. **Fluxo Passageiro:** Cadastra endereço, filtra caronas abertas, solicita vaga e acompanha em **Minhas Caronas**.
4. **Fluxo Motorista:** Cadastra veículo, publica carona (carro, moto ou gratuita), aceita/recusa solicitações e abre a rota em **Minhas Caronas**.

## Como Executar

1. Clone o repositório e abra a pasta do projeto no Android Studio (Hedgehog ou superior).
2. Aguarde a sincronização automática do Gradle.
3. Selecione um emulador Android ou dispositivo físico com Depuração USB ativada.
4. Execute a configuração `app` clicando em **Run (`▶`)** ou pressionando `Shift + F10`.

## Status do Desenvolvimento

- [x] Estrutura de Sessão (`SharedPreferences`) e autenticação com hash
- [x] Tela de Login e Cadastro (`LoginActivity`)
- [x] Banco de Dados Local Room, DAOs e Repositories
- [x] Área do Motorista com seletor de data/hora, moto, carona gratuita e cálculo estudantil (`HomeMotoristaActivity`)
- [x] Gestão de solicitações pendentes, aceitas e atalho de rota (`MinhasCaronasActivity`)
- [ ] Integração com API REST externa e sincronização em nuvem (próxima etapa)

## Licença

Este projeto está sob a licença MIT. Consulte o arquivo `LICENSE` para mais detalhes.