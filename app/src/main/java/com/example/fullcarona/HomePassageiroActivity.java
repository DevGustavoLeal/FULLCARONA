package com.example.fullcarona;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Carona;
import com.example.fullcarona.data.CaronaRepository;
import com.example.fullcarona.data.SolicitacaoRepository;
import com.example.fullcarona.data.Usuario;

import java.util.Locale;

public class HomePassageiroActivity extends AppCompatActivity {
    private long usuarioId;
    private AppDatabase db;
    private CaronaRepository caronas;
    private SolicitacaoRepository solicitacoes;
    private EditText origemFiltro, destinoFiltro;
    private LinearLayout lista;

    private static final String[] LOCAIS_FILTRO = {
            "Todos os locais",
            "FUCAPI - Unidade I",
            "UFAM - Setor Norte",
            "UEA - EST",
            "Armando Mendes",
            "São José",
            "Terminal 1 (T1)",
            "Terminal 3 (T3)",
            "Terminal 4 (T4)",
            "Amazonas Shopping",
            "Manauara Shopping",
            "Ponta Negra",
            "Outro local (digitar)..."
    };

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
        caronas = new CaronaRepository(db.caronaDao());
        solicitacoes = new SolicitacaoRepository(db);

        LinearLayout root = TelaUi.criar(this, "Caronas disponíveis", "Encontre uma carona e solicite sua vaga.");
        origemFiltro = TelaUi.campo(this, root, "Filtrar por origem (Toque para selecionar)", InputType.TYPE_NULL);
        configurarSeletorFiltro(origemFiltro, "Filtrar por origem");

        destinoFiltro = TelaUi.campo(this, root, "Filtrar por destino (Toque para selecionar)", InputType.TYPE_NULL);
        configurarSeletorFiltro(destinoFiltro, "Filtrar por destino");

        TelaUi.botao(this, root, "Buscar", this::carregarCaronas);
        TelaUi.botao(this, root, "Limpar filtros", () -> {
            origemFiltro.setText("");
            destinoFiltro.setText("");
            carregarCaronas();
        });
        TelaUi.botao(this, root, "Minhas caronas", () -> startActivity(new Intent(this, MinhasCaronasActivity.class)));
        TelaUi.botao(this, root, "Sair", this::sair);

        lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        root.addView(TelaUi.texto(this, "Resultados", 20, true));
        root.addView(lista);

        carregarCaronas();
    }

    private void configurarSeletorFiltro(EditText campo, String titulo) {
        campo.setFocusable(false);
        campo.setClickable(true);
        campo.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setItems(LOCAIS_FILTRO, (dialog, which) -> {
                    String escolhido = LOCAIS_FILTRO[which];
                    if ("Todos os locais".equals(escolhido)) {
                        campo.setText("");
                        carregarCaronas();
                    } else if (escolhido.startsWith("Outro local")) {
                        pedirLocalManual(titulo, campo);
                    } else {
                        campo.setText(escolhido);
                        carregarCaronas();
                    }
                })
                .show());
    }

    private void pedirLocalManual(String titulo, EditText destinoCampo) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setView(input)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String texto = input.getText().toString().trim();
                    destinoCampo.setText(texto);
                    carregarCaronas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void carregarCaronas() {
        if (lista == null) return;
        lista.removeAllViews();
        String origem = origemFiltro.getText().toString().trim().toLowerCase(Locale.ROOT);
        String destino = destinoFiltro.getText().toString().trim().toLowerCase(Locale.ROOT);
        int count = 0;
        for (Carona carona : caronas.listarAbertas()) {
            if (carona.motoristaId == usuarioId
                    || !carona.origem.toLowerCase(Locale.ROOT).contains(origem)
                    || !carona.destino.toLowerCase(Locale.ROOT).contains(destino)) {
                continue;
            }
            Usuario motorista = db.usuarioDao().buscarPorId(carona.motoristaId);
            String nome = motorista == null ? "Motorista" : motorista.nome;
            String valorTexto = carona.valor <= 0.0
                    ? "Grátis"
                    : String.format(Locale.getDefault(), "R$ %.2f", carona.valor);
            String texto = carona.origem + " → " + carona.destino + "\n"
                    + DataHoraUtil.exibir(carona.dataHora) + " · " + carona.vagas + " vaga(s) · " + valorTexto
                    + "\nMotorista: " + nome;
            TelaUi.adicionarLinha(this, lista, texto);
            boolean jaSolicitou = solicitacoes.jaSolicitou(carona.id, usuarioId);
            TelaUi.botao(this, lista, jaSolicitou ? "Solicitação enviada" : "Solicitar carona", () -> {
                if (jaSolicitou) return;
                if (solicitacoes.solicitar(carona.id, usuarioId)) {
                    Toast.makeText(this, "Solicitação enviada ao motorista.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Não foi possível solicitar. Verifique vagas e status.", Toast.LENGTH_LONG).show();
                }
                carregarCaronas();
            }).setEnabled(!jaSolicitou);
            count++;
        }
        if (count == 0) {
            TelaUi.adicionarLinha(this, lista, "Nenhuma carona disponível para esses filtros.");
        }
    }

    private void sair() {
        new Sessao(this).sair();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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