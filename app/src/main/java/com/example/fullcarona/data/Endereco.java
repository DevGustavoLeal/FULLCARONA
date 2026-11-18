package com.example.fullcarona.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "enderecos",
        foreignKeys = @ForeignKey(entity = Usuario.class, parentColumns = "id", childColumns = "usuarioId", onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"usuarioId"}, unique = true)})
public class Endereco {
    @PrimaryKey(autoGenerate = true) public long id;
    public long usuarioId;
    public String rua;
    public String numero;
    public String complemento;

    public Endereco() {}
    @Ignore
    public Endereco(long usuarioId, String rua, String numero, String complemento) {
        this.usuarioId = usuarioId; this.rua = rua; this.numero = numero; this.complemento = complemento;
    }
}
