package com.example.fullcarona.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface EnderecoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) long inserir(Endereco endereco);
    @Query("SELECT * FROM enderecos WHERE usuarioId = :usuarioId LIMIT 1") Endereco buscarPorUsuario(long usuarioId);
}
