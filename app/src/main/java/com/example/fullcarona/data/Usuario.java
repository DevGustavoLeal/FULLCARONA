package com.example.fullcarona.data;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios", indices = {@Index(value = {"email"}, unique = true)})
public class Usuario {
    @PrimaryKey(autoGenerate = true) public long id;
    public String nome;
    public String email;
    public String senhaHash;
    @Ignore public String senha;
    public String curso;
    /** MOTORISTA, PASSAGEIRO ou null enquanto não escolheu. */
    public String perfil;

    public Usuario() {}
    @Ignore
    public Usuario(String nome, String email, String senhaHash, String curso, String perfil) {
        this.nome = nome; this.email = email; this.senhaHash = senhaHash;
        this.curso = curso; this.perfil = perfil;
    }
}
