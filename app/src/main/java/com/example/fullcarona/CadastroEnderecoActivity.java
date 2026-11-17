package com.example.fullcarona;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Endereco;
import com.example.fullcarona.data.EnderecoRepository;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class CadastroEnderecoActivity extends AppCompatActivity {

    private EditText etRua;
    private EditText etNumero;
    private EditText etComplemento;
    private long usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarBarras();
        usuarioId = new Sessao(this).getUsuarioId();
        if (usuarioId <= 0) { finish(); return; }
        setContentView(R.layout.activity_endereco);

        etRua = findViewById(R.id.etRua);
        etNumero = findViewById(R.id.etNumero);
        etComplemento = findViewById(R.id.etComplemento);

        AppCompatButton btnCadastrar = findViewById(R.id.btnCadastrar);
        btnCadastrar.setBackgroundResource(R.drawable.bg_button);
        btnCadastrar.setBackgroundTintList(null);
        btnCadastrar.setAllCaps(false);
        btnCadastrar.setOnClickListener(v -> cadastrar());

        etComplemento.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                cadastrar();
                return true;
            }
            return false;
        });
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

    private void cadastrar() {
        String rua = etRua.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String complemento = etComplemento.getText().toString().trim();

        if (TextUtils.isEmpty(rua)) {
            etRua.requestFocus();
            Toast.makeText(this, R.string.erro_rua, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(numero)) {
            etNumero.requestFocus();
            Toast.makeText(this, R.string.erro_numero, Toast.LENGTH_SHORT).show();
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        String endereco = rua + ", " + numero;
        if (!complemento.isEmpty()) {
            endereco = endereco + " - " + complemento;
        }
        try {
            new EnderecoRepository(AppDatabase.getInstance(this).enderecoDao())
                    .salvar(new Endereco(usuarioId, rua, numero, complemento));
            Toast.makeText(this, "Endereço salvo!", Toast.LENGTH_SHORT).show();
            startActivity(new android.content.Intent(this, HomePassageiroActivity.class));
            finish();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Não foi possível salvar o endereço.", Toast.LENGTH_LONG).show();
        }
    }
}
