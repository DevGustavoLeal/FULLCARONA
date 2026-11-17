package com.example.fullcarona.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface VeiculoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT) long inserir(Veiculo veiculo);
    @Query("SELECT * FROM veiculos WHERE usuarioId = :usuarioId LIMIT 1") Veiculo buscarPorUsuario(long usuarioId);
}
