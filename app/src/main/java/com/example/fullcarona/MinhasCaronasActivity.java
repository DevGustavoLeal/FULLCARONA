package com.example.fullcarona;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Carona;
import com.example.fullcarona.data.CaronaRepository;
import com.example.fullcarona.data.Solicitacao;
import com.example.fullcarona.data.SolicitacaoRepository;
import com.example.fullcarona.data.Usuario;

public class MinhasCaronasActivity extends AppCompatActivity {
    private long usuarioId;
    private AppDatabase db;
    private CaronaRepository caronaRepository;
    private SolicitacaoRepository solicitacaoRepository;
    private LinearLayout lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarBarras();

        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) {
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);
        caronaRepository = new CaronaRepository(db.caronaDao());
        solicitacaoRepository = new SolicitacaoRepository(db);

        LinearLayout root = TelaUi.criar(this, "Minhas Caronas", "Acompanhe solicitações pendentes, aceitas e suas rotas.");

        TelaUi.botao(this, root, "Voltar", this::finish);

        lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        root.addView(lista);

        carregarTudo();
    }

    private void carregarTudo() {
        if (lista == null) return;
        lista.removeAllViews();

        int totalItens = 0;

        for (Carona carona : caronaRepository.listarDoMotorista(usuarioId)) {
            for (Solicitacao pedido : solicitacaoRepository.listarDaCarona(carona.id)) {
                if ("PENDENTE".equals(pedido.status)) {
                    Usuario passageiro = db.usuarioDao().buscarPorId(pedido.passageiroId);
                    String nomePassageiro = passageiro == null ? "Passageiro" : passageiro.nome;

                    String resumo = "[PENDENTE] " + nomePassageiro + "\n"
                            + carona.origem + " -> " + carona.destino + "\n"
                            + "Data/Hora: " + DataHoraUtil.exibir(carona.dataHora)
                            + " · " + carona.vagas + " vaga(s)";

                    TelaUi.adicionarLinha(this, lista, resumo);
                    TelaUi.botao(this, lista, "Aceitar", () -> {
                        if (solicitacaoRepository.atualizarStatus(pedido.id, "ACEITA")) {
                            Toast.makeText(this, "Solicitação aceita.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Sem vagas disponíveis ou solicitação já atualizada.", Toast.LENGTH_LONG).show();
                        }
                        carregarTudo();
                    });
                    TelaUi.botao(this, lista, "Recusar", () -> {
                        solicitacaoRepository.atualizarStatus(pedido.id, "RECUSADA");
                        carregarTudo();
                    });
                    totalItens++;
                }
            }
        }

        for (Carona carona : caronaRepository.listarDoMotorista(usuarioId)) {
            for (Solicitacao pedido : solicitacaoRepository.listarDaCarona(carona.id)) {
                if ("ACEITA".equals(pedido.status)) {
                    Usuario passageiro = db.usuarioDao().buscarPorId(pedido.passageiroId);
                    String nomePassageiro = passageiro == null ? "Passageiro" : passageiro.nome;

                    String resumo = "[ACEITA] " + nomePassageiro + "\n"
                            + "Origem: " + carona.origem + "\n"
                            + "Destino: " + carona.destino + "\n"
                            + "Data/Hora: " + DataHoraUtil.exibir(carona.dataHora);

                    TelaUi.adicionarLinha(this, lista, resumo);
                    TelaUi.botao(this, lista, "Abrir rota origem -> destino", () -> abrirRota(carona.origem, carona.destino));
                    totalItens++;
                }
            }
        }

        for (Solicitacao sol : solicitacaoRepository.listarDoPassageiro(usuarioId)) {
            Carona carona = db.caronaDao().buscarPorId(sol.caronaId);
            if (carona == null) continue;

            Usuario motorista = db.usuarioDao().buscarPorId(carona.motoristaId);
            String nomeMotorista = motorista == null ? "Motorista" : motorista.nome;

            String texto = "[PASSAGEIRO · " + sol.status + "]\n"
                    + "Origem: " + carona.origem + "\n"
                    + "Destino: " + carona.destino + "\n"
                    + "Data/Hora: " + DataHoraUtil.exibir(carona.dataHora) + "\n"
                    + "Motorista: " + nomeMotorista;

            TelaUi.adicionarLinha(this, lista, texto);
            TelaUi.botao(this, lista, "Abrir rota origem -> destino", () -> abrirRota(carona.origem, carona.destino));
            totalItens++;
        }

        if (totalItens == 0) {
            TelaUi.adicionarLinha(this, lista, "Nenhuma solicitação pendente, aceita ou carona encontrada.");
        }
    }

    private void abrirRota(String origem, String destino) {
        String rotaTexto = origem + " -> " + destino;
        Toast.makeText(this, "Rota: " + rotaTexto, Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1"
                + "&origin=" + Uri.encode(origem)
                + "&destination=" + Uri.encode(destino));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir o mapa neste dispositivo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarBarras() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        int navy = Color.parseColor("#041531");
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);
    }
}