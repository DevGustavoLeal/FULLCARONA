package com.example.fullcarona.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Sessao {
    private static final String PREF_NAME = "FullCaronaPrefs";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public Sessao(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void salvarUsuarioId(long usuarioId) {
        editor.putLong("usuarioId", usuarioId);
        editor.apply();
    }

    public long getUsuarioId() {
        return prefs.getLong("usuarioId", -1);
    }

    public void deslogar() {
        editor.clear();
        editor.apply();
    }
}