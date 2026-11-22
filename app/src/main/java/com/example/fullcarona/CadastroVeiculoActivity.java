package com.example.fullcarona;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Veiculo;
import com.example.fullcarona.data.VeiculoRepository;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CadastroVeiculoActivity extends AppCompatActivity {
    private long usuarioId;
    private EditText marca;
    private EditText placa;
    private EditText modeloAno;

    private static final Pattern PADRAO_PLACA =
            Pattern.compile("^[A-Z]{3}-?[0-9]{4}$|^[A-Z]{3}[0-9][A-Z][0-9]{2}$");

    private static final Map<String, String[]> MODELOS_POR_MARCA = new LinkedHashMap<>();

    static {
        MODELOS_POR_MARCA.put("Chevrolet", new String[]{"Onix", "Onix Plus", "Prisma", "Tracker", "Spin", "Cruze", "S10", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Fiat", new String[]{"Mobi", "Argo", "Cronos", "Uno", "Pulse", "Fastback", "Strada", "Toro", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Volkswagen", new String[]{"Gol", "Polo", "Virtus", "Voyage", "T-Cross", "Nivus", "Fox", "Saveiro", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Hyundai", new String[]{"HB20", "HB20S", "Creta", "Tucson", "ix35", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Toyota", new String[]{"Corolla", "Yaris", "Etios", "Corolla Cross", "Hilux", "SW4", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Honda", new String[]{"Civic", "City", "HR-V", "Fit", "WR-V", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Renault", new String[]{"Kwid", "Sandero", "Logan", "Duster", "Captur", "Oroch", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Nissan", new String[]{"Kicks", "Versa", "March", "Sentra", "Frontier", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Ford", new String[]{"Ka", "Fiesta", "EcoSport", "Focus", "Ranger", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Jeep", new String[]{"Renegade", "Compass", "Commander", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Honda Motos", new String[]{"CG 160 Titan", "CG 160 Fan", "Biz 125", "Bros 160", "CB 300F Twister", "XRE 300", "PCX 160", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Yamaha", new String[]{"Fazer FZ25", "Fazer FZ15", "Factor 150", "Crosser 150", "Lander 250", "NMax 160", "Outro (digitar)..."});
        MODELOS_POR_MARCA.put("Outra (digitar)...", new String[]{"Outro (digitar)..."});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) {
            finish();
            return;
        }

        LinearLayout root = TelaUi.criar(this, "Seu veículo", "Cadastre o veículo usado para oferecer caronas.");

        marca = TelaUi.campo(this, root, "Marca (Toque para selecionar)", InputType.TYPE_NULL);
        marca.setFocusable(false);
        marca.setClickable(true);
        marca.setOnClickListener(v -> selecionarMarca());

        placa = TelaUi.campo(this, root, "Placa (ABC-1234 ou ABC1D23)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        placa.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(8)
        });

        modeloAno = TelaUi.campo(this, root, "Modelo / Ano (Toque para selecionar)", InputType.TYPE_NULL);
        modeloAno.setFocusable(false);
        modeloAno.setClickable(true);
        modeloAno.setOnClickListener(v -> selecionarModeloEAno());

        TelaUi.botao(this, root, "Cadastrar veículo", () -> {
            String marcaTexto = marca.getText().toString().trim();
            String placaTexto = placa.getText().toString().trim().toUpperCase();
            String modeloTexto = modeloAno.getText().toString().trim();

            if (marcaTexto.isEmpty()) {
                Toast.makeText(this, "Selecione a marca do veículo.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!PADRAO_PLACA.matcher(placaTexto).matches()) {
                placa.setError("Placa inválida. Use ABC-1234 ou ABC1D23");
                placa.requestFocus();
                return;
            }
            if (modeloTexto.isEmpty()) {
                Toast.makeText(this, "Selecione o modelo e o ano.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                new VeiculoRepository(AppDatabase.getInstance(this).veiculoDao())
                        .salvar(new Veiculo(usuarioId, marcaTexto, placaTexto, modeloTexto));
                startActivity(new Intent(this, HomeMotoristaActivity.class));
                finish();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (RuntimeException e) {
                Toast.makeText(this, "Confira os dados. A placa pode já estar cadastrada.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selecionarMarca() {
        String[] marcas = MODELOS_POR_MARCA.keySet().toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Selecione a marca")
                .setItems(marcas, (dialog, which) -> {
                    String escolhida = marcas[which];
                    if (escolhida.startsWith("Outra")) {
                        pedirTextoManual("Digite a marca", texto -> {
                            marca.setText(texto);
                            modeloAno.setText("");
                        });
                    } else {
                        marca.setText(escolhida);
                        modeloAno.setText("");
                        selecionarModeloEAno();
                    }
                })
                .show();
    }

    private void selecionarModeloEAno() {
        String marcaAtual = marca.getText().toString().trim();
        if (marcaAtual.isEmpty()) {
            Toast.makeText(this, "Selecione a marca primeiro.", Toast.LENGTH_SHORT).show();
            selecionarMarca();
            return;
        }

        String[] modelos = MODELOS_POR_MARCA.getOrDefault(marcaAtual, new String[]{"Outro (digitar)..."});
        new AlertDialog.Builder(this)
                .setTitle("Selecione o modelo (" + marcaAtual + ")")
                .setItems(modelos, (dialog, which) -> {
                    String modeloEscolhido = modelos[which];
                    if (modeloEscolhido.startsWith("Outro")) {
                        pedirTextoManual("Digite o modelo (ex: Corolla)", this::selecionarAno);
                    } else {
                        selecionarAno(modeloEscolhido);
                    }
                })
                .show();
    }

    private void selecionarAno(String modelo) {
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR) + 1;
        int totalAnos = 22;
        String[] anos = new String[totalAnos];
        for (int i = 0; i < totalAnos; i++) {
            anos[i] = String.valueOf(anoAtual - i);
        }

        new AlertDialog.Builder(this)
                .setTitle("Ano do " + modelo)
                .setItems(anos, (dialog, which) -> modeloAno.setText(modelo + "/" + anos[which]))
                .show();
    }

    private void pedirTextoManual(String titulo, OnTextoInformado callback) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setView(input)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String valor = input.getText().toString().trim();
                    if (!valor.isEmpty()) {
                        callback.onConfirmar(valor);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private interface OnTextoInformado {
        void onConfirmar(String texto);
    }
}