package io.dkozak.house.control.client.view.rulelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.model.Rule;

public class RuleRecyclerAdapter extends RecyclerView.Adapter<RuleRecyclerAdapter.RuleViewHolder> {


    private List<Rule> rules = Collections.emptyList();
    private RuleOnClickListener listener;

    public RuleRecyclerAdapter(RuleOnClickListener listener) {
        this.listener = listener;
    }

    public void update(List<Rule> newRules) {
        this.rules = newRules;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rule_item_view, parent, false);
        return new RuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleViewHolder holder, int position) {
        final Rule rule = rules.get(position);
        holder.index.setText(rule.getOffset() + "");
        holder.comparison.setText(rule.getComparison().toString());
        holder.value.setText(rule.getThreshold() + "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(rule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public static class RuleViewHolder extends RecyclerView.ViewHolder {

        public final TextView index;
        public final TextView comparison;
        public final TextView value;

        public RuleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.index = itemView.findViewById(R.id.index);
            this.comparison = itemView.findViewById(R.id.comparison);
            this.value = itemView.findViewById(R.id.value);
        }
    }
}
