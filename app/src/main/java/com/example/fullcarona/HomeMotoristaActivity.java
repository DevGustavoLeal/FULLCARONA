// Vai em: app/src/main/java/com/example/fullcarona/HomeMotoristaActivity.java
// (SUBSTITUI o arquivo que já existe na main). A lógica é a mesma de antes;
// só a parte visual saiu do código e foi para o activity_home_motorista.xml.
package com.example.fullcarona;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Carona;
import com.example.fullcarona.data.CaronaRepository;
import com.example.fullcarona.data.Solicitacao;
import com.example.fullcarona.data.SolicitacaoRepository;
import com.example.fullcarona.data.Usuario;

import java.util.Calendar;
import java.util.Locale;

public class HomeMotoristaActivity extends AppCompatActivity {
    private long usuarioId;
    private AppDatabase db;
    private CaronaRepository caronas;
    private SolicitacaoRepository solicitacoes;
    private EditText origem, destino, dataHora, vagas, preco;
    private CheckBox cbMoto, cbGratuita;
    private LinearLayout lista;
    private TextView tvVazio;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) { finish(); return; }

        db = AppDatabase.getInstance(this);
        caronas = new CaronaRepository(db.caronaDao());
        solicitacoes = new SolicitacaoRepository(db);

        configurarBarras();
        setContentView(R.layout.activity_home_motorista);

        origem = findViewById(R.id.etOrigem);
        destino = findViewById(R.id.etDestino);
        dataHora = findViewById(R.id.etDataHora);
        vagas = findViewById(R.id.etVagas);
        preco = findViewById(R.id.etPreco);
        cbMoto = findViewById(R.id.cbMoto);
        cbGratuita = findViewById(R.id.cbGratuita);
        lista = findViewById(R.id.lista);
        tvVazio = findViewById(R.id.tvVazio);

        dataHora.setOnClickListener(v -> mostrarSeletorDataHora());

        // Cálculo econômico voltado para alunos: R$ 1,50 base + R$ 1,50 por vaga
        vagas.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (!cbGratuita.isChecked() && s.length() > 0) {
                    try {
                        int qtdVagas = Integer.parseInt(s.toString());
                        double tarifaBase = cbMoto.isChecked() ? 1.00 : 1.50;
                        double custoPorVaga = qtdVagas * 1.50;
                        double valorSugerido = tarifaBase + custoPorVaga;
                        preco.setText(String.format(Locale.getDefault(), "%.2f", valorSugerido).replace(",", "."));
                    } catch (NumberFormatException ignored) {}
                }
            }
        });

        cbMoto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                vagas.setText("1");
                preco.setText("2.50");
            } else {
                preco.setText("");
            }
        });

        cbGratuita.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                preco.setText("0.00");
                preco.setEnabled(false);
            } else {
                preco.setEnabled(true);
            }
        });

        configurarBotao(R.id.btnOferecer, this::criarCarona);
        configurarBotao(R.id.btnMinhasCaronas,
                () -> startActivity(new Intent(this, MinhasCaronasActivity.class)));
        configurarBotao(R.id.btnSair, this::sair);

        carregarCaronas();
    }

    private void configurarBotao(int id, Runnable acao) {
        AppCompatButton botao = findViewById(id);
        botao.setBackgroundResource(R.drawable.bg_button);
        botao.setBackgroundTintList(null);
        botao.setAllCaps(false);
        botao.setOnClickListener(v -> acao.run());
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

    private void mostrarSeletorDataHora() {
        Calendar calendario = Calendar.getInstance();
        new DatePickerDialog(this, (view, ano, mes, dia) -> {
            new TimePickerDialog(this, (timeView, hora, minuto) -> {
                String dataFormatada = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d", ano, mes + 1, dia, hora, minuto);
                dataHora.setText(dataFormatada);
            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true).show();
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void criarCarona() {
        String origemTexto = origem.getText().toString().trim();
        if (origemTexto.isEmpty()) {
            origem.setError("Informe a origem da carona");
            Toast.makeText(this, "Digite a origem da carona.", Toast.LENGTH_SHORT).show();
            return;
        }

        String horario = DataHoraUtil.normalizar(dataHora.getText().toString());
        int quantidade;
        try {
            quantidade = Integer.parseInt(vagas.getText().toString().trim());
        } catch (NumberFormatException e) {
            quantidade = cbMoto.isChecked() ? 1 : 0;
        }

        if (horario == null) {
            dataHora.setError("Selecione uma data e hora válidas");
            Toast.makeText(this, "Por favor, selecione a data e hora da carona.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (quantidade <= 0) {
            vagas.setError("Informe o número de vagas");
            Toast.makeText(this, "Informe o número de vagas.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            caronas.criar(new Carona(usuarioId, origemTexto,
                    destino.getText().toString().trim(), horario, quantidade, "ABERTA"));

            origem.setText(""); destino.setText(""); dataHora.setText(""); vagas.setText(""); preco.setText("");
            cbMoto.setChecked(false); cbGratuita.setChecked(false); preco.setEnabled(true);

            Toast.makeText(this, "Carona publicada com sucesso!", Toast.LENGTH_SHORT).show();
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
        boolean temCaronas = false;
        for (Carona carona : caronas.listarDoMotorista(usuarioId)) {
            temCaronas = true;
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
                    TelaUi.botao(this, lista, "Aceitar", () -> {
                        if (solicitacoes.atualizarStatus(pedido.id, "ACEITA"))
                            Toast.makeText(this, "Solicitação aceita.", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(this, "Sem vagas disponíveis ou solicitação já atualizada.", Toast.LENGTH_LONG).show();
                        carregarCaronas();
                    });
                    TelaUi.botao(this, lista, "Recusar", () -> {
                        solicitacoes.atualizarStatus(pedido.id, "RECUSADA");
                        carregarCaronas();
                    });
                }
            }
        }
        tvVazio.setVisibility(temCaronas ? View.GONE : View.VISIBLE);
    }

    private void sair() {
        new Sessao(this).sair();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}