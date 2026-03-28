package com.unixverso.anotai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SuggestionAdapter extends BaseAdapter {

    private Context context;
    private List<Suggestion> listaPessoas;
    private String[] tipos;

    private static class SuggestionHolder{
        public TextView textViewNameValue;
        public TextView textViewCategoryValue;
        public TextView textViewPriorityValue;
        public TextView textViewIsFriendValue;
        public TextView textViewIsUrgentValue;
        public TextView textViewIsSerie;
        public TextView textViewObs;
    }

    public SuggestionAdapter(Context context, List<Suggestion> listaPessoas) {
        this.context = context;
        this.listaPessoas = listaPessoas;
        tipos = context.getResources().getStringArray(R.array.categories_array);
    }

    @Override
    public int getCount() {
        return listaPessoas.size();
    }

    @Override
    public Object getItem(int i) {
        return listaPessoas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SuggestionHolder holder;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.linha_personalizadas,viewGroup,false);

            holder = new SuggestionHolder();
            holder.textViewNameValue = view.findViewById(R.id.textViewNameValue);
            holder.textViewCategoryValue = view.findViewById(R.id.textViewCategoriaValue);
            holder.textViewPriorityValue = view.findViewById(R.id.textViewPriorValue);
            holder.textViewIsFriendValue = view.findViewById(R.id.textViewIsFriendSuggestionValue);
            holder.textViewIsUrgentValue = view.findViewById(R.id.textViewIsUrgentValue);
            holder.textViewIsSerie = view.findViewById(R.id.textViewIsSerieValue);
            holder.textViewObs = view.findViewById(R.id.textViewObsValue);

            view.setTag(holder);
        }else{
            holder = (SuggestionHolder) view.getTag();
        }
        Suggestion suggestion = listaPessoas.get(i);
        holder.textViewNameValue.setText(suggestion.getName());
        holder.textViewCategoryValue.setText((suggestion.getCategory()));

        switch(suggestion.getPriority()){
            case ALTA:
                holder.textViewPriorityValue.setText(R.string.high);
                break;
            case MEDIA:
                holder.textViewPriorityValue.setText(R.string.medium);
                break;
            default:
                holder.textViewPriorityValue.setText(R.string.low);
                break;
        }

        if(suggestion.isFriendSuggestion()){ holder.textViewIsFriendValue.setText(R.string.yes);}
        else {holder.textViewIsFriendValue.setText(R.string.no);}

        if(suggestion.isUrgent()){ holder.textViewIsUrgentValue.setText(R.string.yes);}
        else {holder.textViewIsUrgentValue.setText(R.string.no);}

        if(suggestion.isSerie()){ holder.textViewIsSerie.setText(R.string.yes);}
        else {holder.textViewIsSerie.setText(R.string.no);}

        holder.textViewObs.setText(suggestion.getObs().isEmpty() ? context.getString(R.string.none_obs) : suggestion.getObs());

        return view;
    }
}
