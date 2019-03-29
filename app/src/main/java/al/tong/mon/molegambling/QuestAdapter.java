package al.tong.mon.molegambling;

import al.tong.mon.molegambling.databinding.ItemQuestBinding;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Vector;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    private Vector<QuestItem> questItems = new Vector<>();

    private Context context;

    public QuestAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuestBinding binding = ItemQuestBinding.inflate(LayoutInflater.from(context), parent, false);
        return new QuestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        ItemQuestBinding binding = holder.binding;
        QuestItem questItem = questItems.get(position);
        int colorBorder = questItem.getColor();
        binding.circleCardView.setImageResource(colorBorder);
    }

    @Override
    public int getItemCount() {
        return questItems.size();
    }
    public void update(Vector<QuestItem> questItems) {
        this.questItems = questItems;
        notifyDataSetChanged();
    }

    class QuestViewHolder extends RecyclerView.ViewHolder {
        ItemQuestBinding binding;

        QuestViewHolder(ItemQuestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
