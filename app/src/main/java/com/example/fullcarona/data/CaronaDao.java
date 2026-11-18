package com.example.fullcarona.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CaronaDao {
    @Insert(onConflict = OnConflictStrategy.ABORT) long inserir(Carona carona);
    @Query("SELECT * FROM caronas WHERE status = 'ABERTA' AND vagas > 0 ORDER BY dataHora") List<Carona> listarAbertas();
    @Query("SELECT * FROM caronas WHERE motoristaId = :motoristaId ORDER BY dataHora DESC") List<Carona> listarDoMotorista(long motoristaId);
    @Query("SELECT * FROM caronas WHERE id = :id LIMIT 1") Carona buscarPorId(long id);
    @Query("UPDATE caronas SET vagas = vagas - 1, status = CASE WHEN vagas - 1 <= 0 THEN 'LOTADA' ELSE 'ABERTA' END WHERE id = :id AND status = 'ABERTA' AND vagas > 0") int diminuirVaga(long id);
    @Query("UPDATE caronas SET status = 'ENCERRADA' WHERE id = :id AND motoristaId = :motoristaId") int encerrar(long id, long motoristaId);
}
