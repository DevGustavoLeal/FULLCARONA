# FullCarona

Aplicativo Android Java com Room/SQLite local. O fluxo implementado cobre criação de conta, login, escolha de perfil, endereço do passageiro, veículo do motorista, oferta e solicitação de caronas, decisão do motorista e histórico dos dois perfis.

## Executar

Abra a pasta do projeto no Android Studio, sincronize o Gradle e execute a configuração `app` em um emulador ou dispositivo.

## Dados de demonstração

Na primeira criação do banco local, o app inclui contas e três caronas futuras para facilitar a demonstração:

- Motorista: `motorista@fullcarona.app` / `Motorista123`
- Passageiro: `passageiro@fullcarona.app` / `Passageiro123`

Contas novas podem ser criadas pela tela de login. A senha é armazenada usando PBKDF2-HMAC-SHA256 com salt. A sessão guarda apenas o ID do usuário. Dados ficam no dispositivo e não são sincronizados entre aparelhos.

## Fluxos

- Início → Entrar → Login ou Criar conta.
- Após login, contas sem perfil escolhem Passageiro ou Motorista.
- Passageiro cadastra endereço, vê caronas abertas, filtra por origem/destino e solicita uma vaga.
- Motorista cadastra veículo, publica carona e aceita ou recusa solicitações. A última vaga aceita fecha a carona como `LOTADA`.
- Minhas caronas mostra ofertas do motorista ou solicitações do passageiro com status.

## Banco

Room contém `Usuario`, `Endereco`, `Veiculo`, `Carona` e `Solicitacao`, com DAOs e repositories separados. Operações são locais e síncronas para esta demonstração; o Room permite chamadas na thread principal. O banco usa fallback destrutivo durante mudanças de esquema de desenvolvimento, então a atualização do esquema pode apagar os dados locais existentes.

## Licença

Este projeto está sob a licença MIT. Consulte o arquivo LICENSE para os termos completos.
✨ Funcionalidades

🔹 Para Passageiros

Busca de Caronas: Visualização de caronas disponíveis com origem, destino, horário e preço.

Detalhes da Viagem: Consulta de vagas restantes e valor individual por assento.

Reserva de Vagas: Solicitação de confirmação para entrar na carona.

🔹 Para Motoristas

Oferecer Carona: Cadastro de rotas informando local de partida, destino, horário e assentos livres.

Gerenciamento de Vagas: Controle em tempo real do número de assentos disponíveis.

🛠️ Tecnologias Utilizadas

Linguagem: Java (JDK 17+)

IDE: Android Studio

Interface de Usuário: Layouts nativos em XML (Material Design 3)

Componentes Nativos:

RecyclerView + CardView (Listagem otimizada de caronas)

ExtendedFloatingActionButton (Ações rápidas)

AppCompatActivity

📂 Estrutura do Projeto

app/src/main/
├── java/com/exemplo/fullcarona/
│   ├── MainActivity.java      # Activity principal com a listagem de caronas
│   ├── CaronaAdapter.java     # Adaptador para o RecyclerView
│   └── Carona.java            # Modelo de Dados (POJO)
└── res/layout/
    ├── activity_main.xml      # Layout principal da tela de caronas
    └── item_carona.xml        # Layout individual dos cartões da lista


⚙️ Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

Android Studio (Versão Hedgehog ou superior recomendada)

Java Development Kit (JDK 17 ou superior)

Um emulador Android ou dispositivo físico com Depuração USB ativada.

🚀 Como Executar o Projeto

Clonar o Repositório:

git clone https://github.com/seu-usuario/full-carona.git


Abrir no Android Studio:

Abra o Android Studio.

Selecione Open e escolha a pasta clonada do projeto.

Sincronizar Dependências:

Aguarde o Gradle sincronizar as dependências do projeto automaticamente.

Executar a Aplicação:

Selecione o dispositivo ou emulador desejado.

Clique em Run (Shift + F10 ou no botão ▶️).

🗺️ Próximos Passos & Roadmap

[ ] Tela de Login e Cadastro de Usuários.

[ ] Integração com Google Maps API para exibição de rotas no mapa.

[ ] Conexão com backend via Retrofit (API REST em Node.js / Firebase).

[ ] Sistema de avaliações entre passageiros e motoristas.

[ ] Chat interno em tempo real.

📄 Licença

Este projeto está sob a licença MIT. Consulte o arquivo LICENSE para obter mais detalhes.


## Status do Desenvolvimento - Gustavo
- [x] Estrutura da Sessao (SharedPreferences)
- [x] Tela de Login (LoginActivity)
- [x] Tela de Minhas Caronas (MinhasCaronasActivity + Adapter + Item Layout)
- [ ] Integracao com o Banco de Dados Room (Aguardando Repositories do Pedro)
