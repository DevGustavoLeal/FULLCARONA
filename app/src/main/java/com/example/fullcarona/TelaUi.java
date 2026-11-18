package com.example.fullcarona;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

final class TelaUi {
    private TelaUi() {}
    static LinearLayout criar(AppCompatActivity activity, String titulo, String subtitulo) {
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(245, 247, 251));
        LinearLayout coluna = new LinearLayout(activity);
        coluna.setOrientation(LinearLayout.VERTICAL);
        int margem = dp(activity, 24);
        coluna.setPadding(margem, margem, margem, margem);
        scroll.addView(coluna, new ScrollView.LayoutParams(-1, -2));
        TextView heading = texto(activity, titulo, 28, true);
        heading.setTextColor(Color.rgb(8, 24, 58));
        coluna.addView(heading);
        if (subtitulo != null && !subtitulo.isEmpty()) {
            TextView sub = texto(activity, subtitulo, 14, false);
            sub.setTextColor(Color.rgb(80, 90, 108));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
            p.bottomMargin = dp(activity, 18);
            coluna.addView(sub, p);
        }
        activity.setContentView(scroll);
        return coluna;
    }
    static TextView texto(AppCompatActivity activity, String valor, int tamanho, boolean negrito) {
        TextView view = new TextView(activity);
        view.setText(valor);
        view.setTextSize(tamanho);
        view.setTextColor(Color.rgb(20, 32, 54));
        if (negrito) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setPadding(0, dp(activity, 8), 0, dp(activity, 8));
        return view;
    }
    static EditText campo(AppCompatActivity activity, LinearLayout coluna, String dica, int tipo) {
        EditText input = new EditText(activity);
        input.setSingleLine((tipo & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0);
        input.setHint(dica);
        input.setInputType(tipo);
        input.setTextSize(16);
        input.setPadding(dp(activity, 12), dp(activity, 10), dp(activity, 12), dp(activity, 10));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(activity, 8));
        bg.setStroke(dp(activity, 1), Color.rgb(213, 219, 229));
        input.setBackground(bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(activity, 10);
        coluna.addView(input, p);
        return input;
    }
    static AppCompatButton botao(AppCompatActivity activity, LinearLayout coluna, String titulo, Runnable acao) {
        AppCompatButton button = new AppCompatButton(activity);
        button.setText(titulo);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(null);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(8, 24, 58));
        bg.setCornerRadius(dp(activity, 10));
        button.setBackground(bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(activity, 52));
        p.topMargin = dp(activity, 12);
        coluna.addView(button, p);
        button.setOnClickListener(v -> acao.run());
        return button;
    }
    static void adicionarLinha(AppCompatActivity activity, LinearLayout coluna, String conteudo) {
        TextView line = texto(activity, conteudo, 15, false);
        line.setBackgroundColor(Color.WHITE);
        int padding = dp(activity, 12);
        line.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -2);
        p.topMargin = dp(activity, 8);
        coluna.addView(line, p);
    }
    static int dp(AppCompatActivity activity, int value) {
        return Math.round(value * activity.getResources().getDisplayMetrics().density);
    }
}
