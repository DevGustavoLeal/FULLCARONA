# Checklist de Testes do Fluxo Completo - FullCarona

Este documento contém o roteiro de testes passo a passo para validação completa do fluxo do aplicativo FullCarona (integração entre os módulos desenvolvidos pela equipe).

---

## 📋 1. Tela Inicial (`MainActivity`)
- [ ] **Abrir o aplicativo:** Verificar se o logo, título "FullCarona" e o botão "Entrar" são exibidos corretamente.
- [ ] **Navegação:** Clicar no botão **Entrar**.
  - **Resultado esperado:** Abre a tela de Login (`LoginActivity`).

---

## 📋 2. Criar Conta (`CriarContaActivity`)
- [ ] **Acesso:** Na tela de Login, clicar no link **"Criar conta"**.
  - **Resultado esperado:** Abre a tela de cadastro com os campos: Nome completo, E-mail, Curso, Senha e Confirme a senha.
- [ ] **Validação de campos vazios:** Clicar em "Criar conta" sem preencher nada.
  - **Resultado esperado:** Exibe mensagem de erro solicitando o preenchimento dos campos.
- [ ] **Validação de e-mail inválido:** Digitar um e-mail sem formato válido (ex: `usuario`).
  - **Resultado esperado:** Exibe erro "Informe um e-mail válido".
- [ ] **Validação de senha curta:** Digitar uma senha com menos de 6 caracteres (ex: `12345`).
  - **Resultado esperado:** Exibe erro "Use pelo menos 6 caracteres".
- [ ] **Validação de senhas divergentes:** Digitar senhas diferentes nos dois campos.
  - **Resultado esperado:** Exibe erro "As senhas não conferem".
- [ ] **Cadastro com sucesso:** Preencher dados válidos (ex: `Bia`, `bia@faculdade.edu.br`, `Engenharia`, `123456`).
  - **Resultado esperado:** Exibe Toast de confirmação, salva a senha com hash SHA-256 no banco e retorna à tela de Login.
- [ ] **Validação de e-mail duplicado:** Tentar cadastrar novamente usando o e-mail `bia@faculdade.edu.br`.
  - **Resultado esperado:** Exibe mensagem informando que o e-mail já está cadastrado.

---

## 📋 3. Login (`LoginActivity`)
- [ ] **Login incorreto:** Tentar entrar com e-mail ou senha errados.
  - **Resultado esperado:** Exibe erro "Confira o e-mail e a senha" ou "E-mail ou senha inválidos".
- [ ] **Redefinição de senha:** Clicar em **"Esqueci a senha"**, preencher e-mail e nova senha (min 6 caracteres).
  - **Resultado esperado:** Atualiza a senha no banco e exibe Toast de sucesso.
- [ ] **Login com sucesso:** Digitar o e-mail e a senha cadastrados e clicar em **Entrar**.
  - **Resultado esperado:** Salva o ID do usuário na `Sessao` e redireciona para a **Escolha de Perfil** ou direto para a **Home** (caso o perfil já esteja definido).

---

## 📋 4. Escolha de Perfil (`EscolhaPerfilActivity`)
- [ ] **Seleção de Perfil:** Escolher a opção **Passageiro** ou **Motorista**.
  - **Resultado esperado:** 
    - Se **Passageiro**: Redireciona para `CadastroEnderecoActivity` (ou `HomePassageiroActivity` se já tiver endereço).
    - Se **Motorista**: Redireciona para `CadastroVeiculoActivity` (ou `HomeMotoristaActivity` se já tiver veículo).

---

## 📋 5. Cadastros Complementares
- [ ] **Cadastro de Endereço (`CadastroEnderecoActivity`):** Preencher Rua e Número (Complemento opcional) e clicar em Cadastrar.
  - **Resultado esperado:** Salva o endereço no banco e redireciona para a `HomePassageiroActivity`.
- [ ] **Cadastro de Veículo (`CadastroVeiculoActivity`):** Preencher Marca, Placa e Modelo/Ano e clicar em Cadastrar.
  - **Resultado esperado:** Salva o veículo no banco e redireciona para a `HomeMotoristaActivity`.

---

## 📋 6. Fluxo de Caronas (Motorista e Passageiro)
- [ ] **Oferecer Carona (Motorista):** Na `HomeMotoristaActivity`, cadastrar uma nova carona (origem, destino, horário e vagas).
  - **Resultado esperado:** Carona criada com status `ABERTA` e exibida na lista.
- [ ] **Solicitar Carona (Passageiro):** Na `HomePassageiroActivity`, visualizar a carona criada e clicar em **Solicitar**.
  - **Resultado esperado:** Status da solicitação passa para `PENDENTE` e impede solicitação duplicada.
- [ ] **Aceitar/Recusar Solicitação (Motorista):** Na tela do motorista, aceitar a solicitação do passageiro.
  - **Resultado esperado:** O número de vagas diminui. Quando as vagas chegam a 0, o status da carona passa para `LOTADA`.

---

## 📋 7. Histórico (`MinhasCaronasActivity`)
- [ ] **Visualizar Histórico:** Abrir a tela **Minhas Caronas**.
  - **Resultado esperado:** Exibe as caronas do usuário com seus respectivos status atualizados (`PENDENTE`, `ACEITA`, `RECUSADA`, `LOTADA`).
