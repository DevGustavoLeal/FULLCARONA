package com.example.fullcarona;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class CaronasPassageiroAdapter extends RecyclerView.Adapter<CaronasPassageiroAdapter.ViewHolder> {

    public static class ItemCarona {
        public final long id;
        public final String origem;
        public final String destino;
        public final String horario;
        public final int vagas;
        public final String status;

        public ItemCarona(long id, String origem, String destino, String horario, int vagas, String status) {
            this.id = id;
            this.origem = origem;
            this.destino = destino;
            this.horario = horario;
            this.vagas = vagas;
            this.status = status;
        }
    }

    public interface OnSolicitarListener {
        void onSolicitar(ItemCarona carona, int posicao);
    }

    private final List<ItemCarona> caronas;
    private final Map<Long, String> solicitacoes;
    private final OnSolicitarListener listener;

    public CaronasPassageiroAdapter(List<ItemCarona> caronas, Map<Long, String> solicitacoes,
                                    OnSolicitarListener listener) {
        this.caronas = caronas;
        this.solicitacoes = solicitacoes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carona_passageiro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemCarona carona = caronas.get(position);
        View item = holder.itemView;

        holder.txtTrajeto.setText(item.getContext().getString(R.string.home_trajeto, carona.origem, carona.destino));
        holder.txtHorario.setText(item.getContext().getString(R.string.home_horario, carona.horario));
        holder.txtVagas.setText(item.getResources().getQuantityString(R.plurals.home_vagas, carona.vagas, carona.vagas));

        boolean jaSolicitou = solicitacoes.containsKey(carona.id);
        holder.btnSolicitar.setBackgroundTintList(null);
        holder.btnSolicitar.setEnabled(!jaSolicitou);
        holder.btnSolicitar.setAlpha(jaSolicitou ? 0.5f : 1f);
        holder.btnSolicitar.setText(jaSolicitou ? R.string.home_btn_solicitado : R.string.home_btn_solicitar);
        holder.btnSolicitar.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onSolicitar(caronas.get(pos), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return caronas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView txtTrajeto;
        final TextView txtHorario;
        final TextView txtVagas;
        final AppCompatButton btnSolicitar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTrajeto = itemView.findViewById(R.id.txtTrajeto);
            txtHorario = itemView.findViewById(R.id.txtHorario);
            txtVagas = itemView.findViewById(R.id.txtVagas);
            btnSolicitar = itemView.findViewById(R.id.btnSolicitar);
        }
    }
}
