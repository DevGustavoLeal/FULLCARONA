package com.example.fullcarona.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@Database(entities = {Usuario.class, Endereco.class, Veiculo.class, Carona.class, Solicitacao.class},
        version = 3, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UsuarioDao usuarioDao();
    public abstract EnderecoDao enderecoDao();
    public abstract VeiculoDao veiculoDao();
    public abstract CaronaDao caronaDao();
    public abstract SolicitacaoDao solicitacaoDao();

    public static AppDatabase getInstance(android.content.Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "fullcarona.db")
                            // Banco local de demonstração; alterações de esquema em desenvolvimento descartam dados de teste.
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .addCallback(new Callback() {
                                @Override public void onCreate(SupportSQLiteDatabase database) {
                                    super.onCreate(database);
                                    inserirDadosDeDemonstracao(database);
                                }
                                @Override public void onOpen(SupportSQLiteDatabase database) {
                                    super.onOpen(database);
                                    garantirDadosDeDemonstracao(database);
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void garantirDadosDeDemonstracao(SupportSQLiteDatabase db) {
        try (android.database.Cursor cursor = db.query("SELECT COUNT(*) FROM usuarios")) {
            if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                inserirDadosDeDemonstracao(db);
            }
        } catch (RuntimeException e) {
            Log.e("FullCarona", "Falha ao verificar dados de demonstração", e);
        }
    }

    private static void inserirDadosDeDemonstracao(SupportSQLiteDatabase db) {
        db.beginTransaction();
        try {
            ContentValues motorista = new ContentValues();
            motorista.put("nome", "Motorista Demonstração");
            motorista.put("email", "motorista@fullcarona.app");
            motorista.put("senhaHash", SenhaUtil.gerarHash("Motorista123"));
            motorista.put("instituicao", "Fucapi");
            motorista.put("curso", "Engenharia");
            motorista.put("ra", "2026001");
            motorista.put("perfil", "MOTORISTA");
            long motoristaId = db.insert("usuarios", SQLiteDatabase.CONFLICT_IGNORE, motorista);

            ContentValues passageiro = new ContentValues();
            passageiro.put("nome", "Passageiro Demonstração");
            passageiro.put("email", "passageiro@fullcarona.app");
            passageiro.put("senhaHash", SenhaUtil.gerarHash("Passageiro123"));
            passageiro.put("instituicao", "Fucapi");
            passageiro.put("curso", "Computação");
            passageiro.put("ra", "2026002");
            passageiro.put("perfil", "PASSAGEIRO");
            db.insert("usuarios", SQLiteDatabase.CONFLICT_IGNORE, passageiro);

            if (motoristaId > 0) {
                ContentValues veiculo = new ContentValues();
                veiculo.put("usuarioId", motoristaId);
                veiculo.put("marca", "Fiat");
                veiculo.put("placa", "ABC-1234");
                veiculo.put("modeloAno", "Uno 2020");
                db.insert("veiculos", SQLiteDatabase.CONFLICT_IGNORE, veiculo);

                String[] destinos = {"Campus Central", "Centro", "Biblioteca"};
                String[] origens = {"Avenida Brasil, 100", "Bairro Novo, 25", "Terminal Norte, 1"};
                for (int i = 0; i < destinos.length; i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, i + 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 8 + i);
                    calendar.set(Calendar.MINUTE, 0);
                    String horario = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).format(calendar.getTime());
                    ContentValues carona = new ContentValues();
                    carona.put("motoristaId", motoristaId);
                    carona.put("origem", origens[i]);
                    carona.put("destino", destinos[i]);
                    carona.put("dataHora", horario);
                    carona.put("vagas", 3 - (i == 2 ? 1 : 0));
                    carona.put("status", "ABERTA");
                    db.insert("caronas", SQLiteDatabase.CONFLICT_IGNORE, carona);
                }
            }
            db.setTransactionSuccessful();
        } catch (RuntimeException e) {
            Log.e("FullCarona", "Falha ao criar dados demonstrativos", e);
        } finally {
            db.endTransaction();
        }
    }
}
