package iut.dam.sae_dam.data.pharmacies;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class PharmacieAdapter extends ArrayAdapter<Pharmacie> implements Filterable {
    private List<Pharmacie> pharmaciesListFull;

    public PharmacieAdapter(Context context, List<Pharmacie> pharmaciesList) {
        super(context, android.R.layout.simple_dropdown_item_1line, pharmaciesList);
        pharmaciesListFull = new ArrayList<>(pharmaciesList);
    }

    public boolean contains(int id, String name) {
        return pharmaciesListFull.contains(new Pharmacie(id, name));
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return pharmacieFilter;
    }

    private Filter pharmacieFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Pharmacie> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(pharmaciesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Pharmacie pharmacie : pharmaciesListFull) {
                    if (pharmacie.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(pharmacie);
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
            return String.valueOf(((Pharmacie) resultValue).getName());
        }
    };


}
