package com.example.fullcarona.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "veiculos",
        foreignKeys = @ForeignKey(entity = Usuario.class, parentColumns = "id", childColumns = "usuarioId", onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"usuarioId"}, unique = true), @Index(value = {"placa"}, unique = true)})
public class Veiculo {
    @PrimaryKey(autoGenerate = true) public long id;
    public long usuarioId;
    public String marca;
    public String placa;
    public String modeloAno;

    public Veiculo() {}
    @Ignore
    public Veiculo(long usuarioId, String marca, String placa, String modeloAno) {
        this.usuarioId = usuarioId; this.marca = marca; this.placa = placa; this.modeloAno = modeloAno;
    }
}
