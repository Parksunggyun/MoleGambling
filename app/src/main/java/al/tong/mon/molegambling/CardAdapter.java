package al.tong.mon.molegambling;

import al.tong.mon.molegambling.databinding.ItemCardMoleBinding;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Vector;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Vector<Item> items = new Vector<>();
    private Context context;

    public CardAdapter(Context context) {
        this.context = context;
    }

    private int width = 0, height = 0;
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardMoleBinding binding = ItemCardMoleBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ItemCardMoleBinding binding = holder.binding;
        if (width != 0 && height != 0) {
            binding.cardView.getLayoutParams().width = width;
            binding.cardView.getLayoutParams().height = height;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(Vector<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setSize(int length) {
        this.width = length;
        this.height = length;
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        ItemCardMoleBinding binding;

        CardViewHolder(ItemCardMoleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
