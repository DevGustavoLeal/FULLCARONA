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
