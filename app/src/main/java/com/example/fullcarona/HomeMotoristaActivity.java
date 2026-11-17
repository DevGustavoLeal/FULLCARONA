package com.example.fullcarona;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Carona;
import com.example.fullcarona.data.CaronaRepository;
import com.example.fullcarona.data.Solicitacao;
import com.example.fullcarona.data.SolicitacaoRepository;
import com.example.fullcarona.data.Usuario;

public class HomeMotoristaActivity extends AppCompatActivity {
    private long usuarioId;
    private AppDatabase db;
    private CaronaRepository caronas;
    private SolicitacaoRepository solicitacoes;
    private EditText origem, destino, dataHora, vagas;
    private LinearLayout lista;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) { finish(); return; }
        db = AppDatabase.getInstance(this);
        caronas = new CaronaRepository(db.caronaDao());
        solicitacoes = new SolicitacaoRepository(db);
        LinearLayout root = TelaUi.criar(this, "Área do motorista", "Publique uma carona e gerencie as solicitações.");
        origem = TelaUi.campo(this, root, "Origem", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        destino = TelaUi.campo(this, root, "Destino", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        dataHora = TelaUi.campo(this, root, "Data e hora (AAAA-MM-DD HH:MM)", InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_NORMAL);
        vagas = TelaUi.campo(this, root, "Número de vagas", InputType.TYPE_CLASS_NUMBER);
        TelaUi.botao(this, root, "Oferecer carona", this::criarCarona);
        TelaUi.botao(this, root, "Minhas caronas", () -> startActivity(new Intent(this, MinhasCaronasActivity.class)));
        TelaUi.botao(this, root, "Sair", this::sair);
        root.addView(TelaUi.texto(this, "Minhas ofertas e solicitações", 20, true));
        lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        root.addView(lista);
        carregarCaronas();
    }

    private void criarCarona() {
        String horario = DataHoraUtil.normalizar(dataHora.getText().toString());
        int quantidade;
        try { quantidade = Integer.parseInt(vagas.getText().toString().trim()); }
        catch (NumberFormatException e) { quantidade = 0; }
        if (horario == null) { dataHora.setError("Use data futura no formato AAAA-MM-DD HH:MM"); return; }
        try {
            caronas.criar(new Carona(usuarioId, origem.getText().toString().trim(),
                    destino.getText().toString().trim(), horario, quantidade, "ABERTA"));
            origem.setText(""); destino.setText(""); dataHora.setText(""); vagas.setText("");
            Toast.makeText(this, "Carona publicada.", Toast.LENGTH_SHORT).show();
            carregarCaronas();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Não foi possível salvar a carona.", Toast.LENGTH_LONG).show();
        }
    }

    private void carregarCaronas() {
        if (lista == null) return;
        lista.removeAllViews();
        for (Carona carona : caronas.listarDoMotorista(usuarioId)) {
            TelaUi.adicionarLinha(this, lista, carona.origem + " → " + carona.destino + "\n"
                    + DataHoraUtil.exibir(carona.dataHora) + " · " + carona.vagas + " vaga(s) · " + carona.status);
            if (!"ENCERRADA".equals(carona.status)) {
                TelaUi.botao(this, lista, "Encerrar carona", () -> {
                    caronas.encerrar(carona.id, usuarioId);
                    Toast.makeText(this, "Carona encerrada.", Toast.LENGTH_SHORT).show();
                    carregarCaronas();
                });
            }
            for (Solicitacao pedido : solicitacoes.listarDaCarona(carona.id)) {
                Usuario passageiro = db.usuarioDao().buscarPorId(pedido.passageiroId);
                String nome = passageiro == null ? "Passageiro" : passageiro.nome;
                TelaUi.adicionarLinha(this, lista, nome + " · solicitação " + pedido.status);
                if ("PENDENTE".equals(pedido.status)) {
                    AppCompatButton aceitar = TelaUi.botao(this, lista, "Aceitar", () -> {
                        if (solicitacoes.atualizarStatus(pedido.id, "ACEITA"))
                            Toast.makeText(this, "Solicitação aceita.", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(this, "Sem vagas disponíveis ou solicitação já atualizada.", Toast.LENGTH_LONG).show();
                        carregarCaronas();
                    });
                    AppCompatButton recusar = TelaUi.botao(this, lista, "Recusar", () -> {
                        solicitacoes.atualizarStatus(pedido.id, "RECUSADA");
                        carregarCaronas();
                    });
                }
            }
        }
    }

    private void sair() {
        new Sessao(this).sair();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
