package com.example.fullcarona;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class DataHoraUtil {
    private DataHoraUtil() {}
    static String normalizar(String digitada) {
        SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        entrada.setLenient(false);
        ParsePosition posicao = new ParsePosition(0);
        Date data = entrada.parse(digitada.trim(), posicao);
        if (data == null || posicao.getIndex() != digitada.trim().length()) return null;
        if (data.before(new Date())) return null;
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).format(data);
    }
    static String exibir(String iso) {
        if (iso == null) return "";
        return iso.replace('T', ' ');
    }
}
