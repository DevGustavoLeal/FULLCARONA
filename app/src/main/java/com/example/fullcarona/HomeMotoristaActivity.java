package com.example.fullcarona;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
    private boolean atualizandoPrecoProgramaticamente = false;

    private static final String[] LOCAIS_REFERENCIA = {
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
        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) {
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);
        caronas = new CaronaRepository(db.caronaDao());
        solicitacoes = new SolicitacaoRepository(db);

        LinearLayout root = TelaUi.criar(this, "Área do motorista", "Publique caronas acessíveis para alunos e gerencie pedidos.");

        origem = TelaUi.campo(this, root, "Origem (Toque para selecionar)", InputType.TYPE_NULL);
        configurarCampoSeletorLocal(origem, "Selecione a origem");

        destino = TelaUi.campo(this, root, "Destino (Toque para selecionar)", InputType.TYPE_NULL);
        configurarCampoSeletorLocal(destino, "Selecione o destino");

        dataHora = TelaUi.campo(this, root, "Data e Hora (Toque para selecionar)", InputType.TYPE_NULL);
        dataHora.setFocusable(false);
        dataHora.setClickable(true);
        dataHora.setOnClickListener(v -> mostrarSeletorDataHora());

        vagas = TelaUi.campo(this, root, "Número de vagas", InputType.TYPE_CLASS_NUMBER);
        preco = TelaUi.campo(this, root, "Ajuda de custo sugerida (R$)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        adicionarAtalhosPreco(root);

        // Cálculo econômico voltado para alunos: tarifa base + R$ 1,50 por vaga
        vagas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!cbGratuita.isChecked() && s.length() > 0) {
                    try {
                        int qtdVagas = Integer.parseInt(s.toString());
                        double tarifaBase = cbMoto.isChecked() ? 1.00 : 1.50;
                        double custoPorVaga = qtdVagas * 1.50;
                        double valorSugerido = tarifaBase + custoPorVaga;
                        definirPrecoTexto(String.format(Locale.US, "%.2f", valorSugerido));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        });

        preco.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!atualizandoPrecoProgramaticamente && cbGratuita.isChecked() && !"0.00".equals(s.toString())) {
                    cbGratuita.setChecked(false);
                }
            }
        });

        LinearLayout layoutOpcoes = new LinearLayout(this);
        layoutOpcoes.setOrientation(LinearLayout.HORIZONTAL);
        layoutOpcoes.setGravity(Gravity.CENTER_VERTICAL);

        cbMoto = new CheckBox(this);
        cbMoto.setText(" É de Moto 🏍️");
        cbMoto.setTextColor(Color.rgb(20, 32, 54));
        cbMoto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                vagas.setText("1");
                if (!cbGratuita.isChecked()) {
                    definirPrecoTexto("2.50");
                }
            } else if (!cbGratuita.isChecked()) {
                definirPrecoTexto("");
            }
        });

        cbGratuita = new CheckBox(this);
        cbGratuita.setText(" Carona Gratuita 🤝");
        cbGratuita.setTextColor(Color.rgb(20, 32, 54));
        cbGratuita.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                definirPrecoTexto("0.00");
                preco.setEnabled(false);
            } else {
                preco.setEnabled(true);
            }
        });

        layoutOpcoes.addView(cbMoto);
        layoutOpcoes.addView(cbGratuita);

        LinearLayout.LayoutParams paramsOpcoes = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsOpcoes.topMargin = TelaUi.dp(this, 10);
        root.addView(layoutOpcoes, paramsOpcoes);

        TelaUi.botao(this, root, "Oferecer carona", this::criarCarona);
        TelaUi.botao(this, root, "Minhas caronas", () -> startActivity(new Intent(this, MinhasCaronasActivity.class)));
        TelaUi.botao(this, root, "Sair", this::sair);

        TextView tituloHistorico = TelaUi.texto(this, "Minhas ofertas e solicitações", 20, true);
        LinearLayout.LayoutParams paramsTitulo = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsTitulo.topMargin = TelaUi.dp(this, 20);
        root.addView(tituloHistorico, paramsTitulo);

        lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        root.addView(lista);

        carregarCaronas();
    }

    private void configurarCampoSeletorLocal(EditText campo, String tituloDialogo) {
        campo.setFocusable(false);
        campo.setClickable(true);
        campo.setOnClickListener(v -> mostrarSeletorLocal(tituloDialogo, campo));
    }

    private void mostrarSeletorLocal(String titulo, EditText destinoCampo) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setItems(LOCAIS_REFERENCIA, (dialog, which) -> {
                    String escolhido = LOCAIS_REFERENCIA[which];
                    if (escolhido.startsWith("Outro local")) {
                        pedirLocalManual(titulo, destinoCampo);
                    } else {
                        destinoCampo.setError(null);
                        destinoCampo.setText(escolhido);
                    }
                })
                .show();
    }

    private void pedirLocalManual(String titulo, EditText destinoCampo) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setView(input)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String texto = input.getText().toString().trim();
                    if (!texto.isEmpty()) {
                        destinoCampo.setError(null);
                        destinoCampo.setText(texto);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void adicionarAtalhosPreco(LinearLayout root) {
        LinearLayout linhaAtalhos = new LinearLayout(this);
        linhaAtalhos.setOrientation(LinearLayout.HORIZONTAL);
        linhaAtalhos.setGravity(Gravity.CENTER_VERTICAL);

        String[] rotulos = {"Grátis", "R$ 3", "R$ 5", "R$ 8"};
        String[] valores = {"0.00", "3.00", "5.00", "8.00"};

        for (int i = 0; i < rotulos.length; i++) {
            final String valorAtalho = valores[i];
            final boolean gratis = i == 0;
            AppCompatButton chip = new AppCompatButton(this);
            chip.setText(rotulos[i]);
            chip.setAllCaps(false);
            chip.setTextSize(12f);
            chip.setBackgroundResource(R.drawable.bg_button);
            chip.setBackgroundTintList(null);
            chip.setTextColor(Color.WHITE);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0,
                    TelaUi.dp(this, 36),
                    1f
            );
            if (i > 0) {
                lp.leftMargin = TelaUi.dp(this, 6);
            }
            chip.setLayoutParams(lp);
            chip.setOnClickListener(v -> {
                if (gratis) {
                    cbGratuita.setChecked(true);
                    definirPrecoTexto("0.00");
                    preco.setEnabled(false);
                } else {
                    cbGratuita.setChecked(false);
                    preco.setEnabled(true);
                    definirPrecoTexto(valorAtalho);
                }
            });
            linhaAtalhos.addView(chip);
        }

        LinearLayout.LayoutParams paramsLinha = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsLinha.topMargin = TelaUi.dp(this, 8);
        root.addView(linhaAtalhos, paramsLinha);
    }

    private void definirPrecoTexto(String valor) {
        atualizandoPrecoProgramaticamente = true;
        preco.setText(valor);
        atualizandoPrecoProgramaticamente = false;
    }

    private double obterValorCarona() {
        if (cbGratuita.isChecked()) {
            return 0.0;
        }
        String textoPreco = preco.getText().toString().trim().replace(",", ".");
        if (textoPreco.isEmpty()) {
            return 0.0;
        }
        try {
            return Math.max(0.0, Double.parseDouble(textoPreco));
        } catch (NumberFormatException e) {
            return 0.0;
        }
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
            origem.setError("Selecione ou informe a origem da carona");
            Toast.makeText(this, "Informe a origem da carona.", Toast.LENGTH_SHORT).show();
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

        double valorCarona = obterValorCarona();
        String modalidade = cbMoto.isChecked() ? "Moto" : "Carro";

        try {
            caronas.criar(new Carona(
                    usuarioId,
                    origemTexto,
                    destino.getText().toString().trim(),
                    horario,
                    quantidade,
                    "ABERTA",
                    valorCarona,
                    modalidade
            ));

            origem.setText("");
            destino.setText("");
            dataHora.setText("");
            vagas.setText("");
            definirPrecoTexto("");
            cbMoto.setChecked(false);
            cbGratuita.setChecked(false);
            preco.setEnabled(true);

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
        for (Carona carona : caronas.listarDoMotorista(usuarioId)) {
            String precoFormatado = carona.valor <= 0.0
                    ? "Grátis"
                    : String.format(Locale.getDefault(), "R$ %.2f", carona.valor);
            TelaUi.adicionarLinha(this, lista, carona.origem + " → " + carona.destino + "\n"
                    + DataHoraUtil.exibir(carona.dataHora) + " · " + carona.vagas + " vaga(s) · "
                    + precoFormatado + " · " + carona.status);
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
                    AppCompatButton aceitarBtn = TelaUi.botao(this, lista, "Aceitar", () -> {
                        if (solicitacoes.atualizarStatus(pedido.id, "ACEITA"))
                            Toast.makeText(this, "Solicitação aceita.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(this, "Sem vagas disponíveis ou solicitação já atualizada.", Toast.LENGTH_LONG).show();
                        carregarCaronas();
                    });
                    AppCompatButton recusarBtn = TelaUi.botao(this, lista, "Recusar", () -> {
                        solicitacoes.atualizarStatus(pedido.id, "RECUSADA");
                        carregarCaronas();
                    });
                    if (aceitarBtn == null || recusarBtn == null) {
                        // Mantém referência limpa
                    }
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