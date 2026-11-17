package com.example.fullcarona.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.ABORT) long inserir(Usuario usuario);
    @Query("SELECT COUNT(*) > 0 FROM usuarios WHERE lower(email) = lower(:email)") boolean emailExiste(String email);
    @Query("SELECT * FROM usuarios WHERE lower(email) = lower(:email) LIMIT 1") Usuario buscarPorEmail(String email);
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1") Usuario buscarPorId(long id);
    @Query("UPDATE usuarios SET perfil = :perfil WHERE id = :id") int definirPerfil(long id, String perfil);
    @Query("UPDATE usuarios SET senhaHash = :senhaHash WHERE lower(email) = lower(:email)") int redefinirSenha(String email, String senhaHash);
}
