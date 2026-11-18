package com.example.fullcarona;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.fullcarona.data.AppDatabase;
import com.example.fullcarona.data.Usuario;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class EscolhaPerfilActivityTest {
    @Test public void escolhePerfilSalvaEAbreFluxoDoMotorista() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AppDatabase db = AppDatabase.getInstance(context);
        Usuario motorista = db.usuarioDao().buscarPorEmail("motorista@fullcarona.app");
        assertNotNull("O banco deve criar a conta de demonstração", motorista);
        assertNotNull("O banco deve criar o veículo de demonstração", db.veiculoDao().buscarPorUsuario(motorista.id));
        assertEquals("O banco deve criar três caronas de demonstração", 3, db.caronaDao().listarAbertas().size());

        Sessao sessao = new Sessao(context);
        sessao.salvarUsuario(motorista.id);
        try (ActivityScenario<EscolhaPerfilActivity> ignored = ActivityScenario.launch(EscolhaPerfilActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.btnMotorista)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            Espresso.onView(ViewMatchers.withId(R.id.btnPassageiro)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            Espresso.onView(ViewMatchers.withId(R.id.btnMotorista)).perform(androidx.test.espresso.action.ViewActions.click());
            Espresso.onView(ViewMatchers.withText("Área do motorista")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            assertEquals("MOTORISTA", db.usuarioDao().buscarPorId(motorista.id).perfil);
        } finally {
            sessao.sair();
        }
    }
}
