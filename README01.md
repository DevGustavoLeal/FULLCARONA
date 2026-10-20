🚗 Full Carona

O Full Carona é uma aplicação mobile nativa para Android desenvolvida em Java para facilitar a mobilidade urbana, conectando motoristas com assentos disponíveis a passageiros que buscam caronas seguras, econômicas e acessíveis.

📌 Conteúdo

Sobre o Projeto

Funcionalidades

Tecnologias Utilizadas

Estrutura do Projeto

Pré-requisitos

Como Executar o Projeto

Próximos Passos & Roadmap

Licença

📖 Sobre o Projeto

A proposta do Full Carona é oferecer uma alternativa eficiente e sustentável ao transporte individual e público. Com a aplicação, motoristas compartilham trajetos diários para dividir os custos da viagem, enquanto os passageiros encontram rotas convenientes com preços flexíveis.

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
