package iut.dam.sae_dam.data.villes;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class VilleAdapter extends ArrayAdapter<Ville> implements Filterable {
    private List<Ville> cityListFull;

    public VilleAdapter(Context context, List<Ville> cityList) {
        super(context, android.R.layout.simple_dropdown_item_1line, cityList);
        cityListFull = new ArrayList<>(cityList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return villeFilter;
    }


    private Filter villeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Ville> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(cityListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Ville ville : cityListFull) {
                    if (ville.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(ville);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return String.valueOf((Ville) resultValue);
        }
    };
}
