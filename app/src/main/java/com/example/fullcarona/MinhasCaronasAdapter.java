package com.example.fullcarona;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MinhasCaronasAdapter extends RecyclerView.Adapter<MinhasCaronasAdapter.CaronaViewHolder> {

    private List<String> listaCaronas;

    public MinhasCaronasAdapter(List<String> listaCaronas) {
        this.listaCaronas = listaCaronas;
    }

    @NonNull
    @Override
    public CaronaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minha_carona, parent, false);
        return new CaronaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CaronaViewHolder holder, int position) {
        String carona = listaCaronas.get(position);
        holder.txtTrajeto.setText(carona);
        holder.txtDataHora.setText("Data: A definir | Horário: --:--");
        holder.txtStatus.setText("Status: Ativa / Pendente");
    }

    @Override
    public int getItemCount() {
        return listaCaronas != null ? listaCaronas.size() : 0;
    }

    public static class CaronaViewHolder extends RecyclerView.ViewHolder {
        TextView txtTrajeto, txtDataHora, txtStatus;

        public CaronaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTrajeto = itemView.findViewById(R.id.txtTrajetoCarona);
            txtDataHora = itemView.findViewById(R.id.txtDataHoraCarona);
            txtStatus = itemView.findViewById(R.id.txtStatusCarona);
        }
    }
}