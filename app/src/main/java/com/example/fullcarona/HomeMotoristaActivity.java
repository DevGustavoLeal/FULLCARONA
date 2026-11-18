package com.example.fullcarona;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Carona;
import com.example.fullcarona.data.CaronaRepository;
import com.example.fullcarona.data.Solicitacao;
import com.example.fullcarona.data.SolicitacaoRepository;
import com.example.fullcarona.data.Usuario;

import java.util.Calendar;
import java.util.Locale;

public class HomeMotoristaActivity extends AppCompatActivity {
    private static final int PERMISSAO_GPS_CODE = 100;
    private long usuarioId;
    private AppDatabase db;
    private CaronaRepository caronas;
    private SolicitacaoRepository solicitacoes;
    private EditText origem, destino, dataHora, vagas, preco;
    private CheckBox cbMoto, cbGratuita;
    private LinearLayout lista;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) { finish(); return; }

        db = AppDatabase.getInstance(this);
        caronas = new CaronaRepository(db.caronaDao());
        solicitacoes = new SolicitacaoRepository(db);

        LinearLayout root = TelaUi.criar(this, "Área do motorista", "Publique caronas com GPS nativo e precificação inteligente.");

        origem = TelaUi.campo(this, root, "Origem (ex: Minha Residência)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        AppCompatButton btnLocalizacao = TelaUi.botao(this, root, "📍 Obter Localização Atual (GPS)", this::obterLocalizacaoGPSNativa);
        btnLocalizacao.setTextSize(14);

        destino = TelaUi.campo(this, root, "Destino (ex: Armando Mendes)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        dataHora = TelaUi.campo(this, root, "Data e Hora (Toque para selecionar)", InputType.TYPE_NULL);
        dataHora.setFocusable(false);
        dataHora.setClickable(true);
        dataHora.setOnClickListener(v -> mostrarSeletorDataHora());

        vagas = TelaUi.campo(this, root, "Número de vagas", InputType.TYPE_CLASS_NUMBER);
        preco = TelaUi.campo(this, root, "Valor sugerido (R$)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        vagas.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (!cbGratuita.isChecked() && s.length() > 0) {
                    try {
                        int qtdVagas = Integer.parseInt(s.toString());
                        double tarifaBase = cbMoto.isChecked() ? 2.00 : 3.00;
                        double custoPorVaga = qtdVagas * 4.50;
                        double valorSugerido = tarifaBase + custoPorVaga;
                        preco.setText(String.format(Locale.getDefault(), "%.2f", valorSugerido).replace(",", "."));
                    } catch (NumberFormatException ignored) {}
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
                preco.setText("6.00");
            } else {
                preco.setText("");
            }
        });

        cbGratuita = new CheckBox(this);
        cbGratuita.setText(" Carona Gratuita 🤝");
        cbGratuita.setTextColor(Color.rgb(20, 32, 54));
        cbGratuita.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                preco.setText("0.00");
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

    private void obterLocalizacaoGPSNativa() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSAO_GPS_CODE);
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            Location locGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location melhorLoc = (locGPS != null) ? locGPS : locNetwork;

            if (melhorLoc != null) {
                String coordText = String.format(Locale.getDefault(), "Lat: %.4f, Lon: %.4f", melhorLoc.getLatitude(), melhorLoc.getLongitude());
                origem.setText(coordText);
                Toast.makeText(this, "Localização GPS obtida com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS sem fix no emulador. Digite sua rua/bairro atual.", Toast.LENGTH_LONG).show();
                origem.requestFocus();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSAO_GPS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obterLocalizacaoGPSNativa();
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
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
            caronas.criar(new Carona(usuarioId, origem.getText().toString().trim(),
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
                    AppCompatButton aceitarBtn = TelaUi.botao(this, lista, "Aceitar", () -> {
                        if (solicitacoes.atualizarStatus(pedido.id, "ACEITA"))
                            Toast.makeText(this, "Solicitação aceita.", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(this, "Sem vagas disponíveis ou solicitação já atualizada.", Toast.LENGTH_LONG).show();
                        carregarCaronas();
                    });
                    AppCompatButton recusarBtn = TelaUi.botao(this, lista, "Recusar", () -> {
                        solicitacoes.atualizarStatus(pedido.id, "RECUSADA");
                        carregarCaronas();
                    });
                    if (aceitarBtn == null || recusarBtn == null) {
                        // Evita warning de variáveis não usadas
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