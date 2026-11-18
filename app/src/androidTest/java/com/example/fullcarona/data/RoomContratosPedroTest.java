package com.example.fullcarona.data;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.*;

/** Exercita o contrato de dados definido para a parte do Pedro no projeto. */
@RunWith(AndroidJUnit4.class)
public class RoomContratosPedroTest {
    @Test public void repositoriesGravamLeemEAplicamRegrasDoFluxo() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AppDatabase db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        try {
            UsuarioRepository usuarios = new UsuarioRepository(db);
            Usuario motorista = novoUsuario("Motorista Teste", "motorista-teste@example.com", "MOTORISTA");
            motorista.senha = "senha123";
            long motoristaId = usuarios.cadastrar(motorista);
            Usuario passageiro = novoUsuario("Passageiro Teste", "passageiro-teste@example.com", "PASSAGEIRO");
            passageiro.senha = "senha456";
            long passageiroId = usuarios.cadastrar(passageiro);

            assertTrue(usuarios.emailExiste("MOTORISTA-TESTE@example.com"));
            assertNotNull(usuarios.login("motorista-teste@example.com", "senha123"));
            assertNull(usuarios.login("motorista-teste@example.com", "senha-errada"));
            assertNotEquals("senha123", motorista.senhaHash);
            assertNull(motorista.senha);
            assertTrue(usuarios.definirPerfil(passageiroId, "PASSAGEIRO"));
            assertEquals("PASSAGEIRO", usuarios.buscarPorId(passageiroId).perfil);
            assertTrue(usuarios.redefinirSenha("passageiro-teste@example.com", "nova123"));
            assertNotNull(usuarios.login("passageiro-teste@example.com", "nova123"));

            EnderecoRepository enderecos = new EnderecoRepository(db.enderecoDao());
            Endereco endereco = new Endereco(passageiroId, "Rua das Flores", "42", "Bloco A");
            assertTrue(enderecos.salvar(endereco) > 0);
            assertEquals("Rua das Flores", enderecos.buscarPorUsuario(passageiroId).rua);

            VeiculoRepository veiculos = new VeiculoRepository(db.veiculoDao());
            Veiculo veiculo = new Veiculo(motoristaId, "Fiat", "ABC1D23", "Uno 2020");
            assertTrue(veiculos.salvar(veiculo) > 0);
            assertEquals("ABC1D23", veiculos.buscarPorUsuario(motoristaId).placa);

            Calendar horario = Calendar.getInstance();
            horario.add(Calendar.DAY_OF_YEAR, 2);
            String dataHora = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).format(horario.getTime());
            CaronaRepository caronas = new CaronaRepository(db.caronaDao());
            Carona carona = new Carona(motoristaId, "Centro", "Campus", dataHora, 2, "ABERTA");
            long caronaId = caronas.criar(carona);
            assertEquals(1, caronas.listarAbertas().size());
            assertEquals("Centro", caronas.buscarPorId(caronaId).origem);
            assertEquals(1, caronas.listarDoMotorista(motoristaId).size());

            SolicitacaoRepository solicitacoes = new SolicitacaoRepository(db);
            assertTrue(solicitacoes.solicitar(caronaId, passageiroId));
            assertTrue(solicitacoes.jaSolicitou(caronaId, passageiroId));
            assertEquals(1, solicitacoes.listarDaCarona(caronaId).size());
            assertEquals(1, solicitacoes.listarDoPassageiro(passageiroId).size());
            long solicitacaoId = solicitacoes.listarDaCarona(caronaId).get(0).id;
            assertTrue(solicitacoes.atualizarStatus(solicitacaoId, "ACEITA"));
            assertEquals("ACEITA", db.solicitacaoDao().buscarPorId(solicitacaoId).status);
            assertEquals(1, caronas.buscarPorId(caronaId).vagas);
        } finally {
            db.close();
        }
    }

    private Usuario novoUsuario(String nome, String email, String perfil) {
        return new Usuario(nome, email, null, "Computação", perfil);
    }
}
