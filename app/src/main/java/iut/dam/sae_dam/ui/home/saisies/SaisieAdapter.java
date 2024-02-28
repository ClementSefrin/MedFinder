package iut.dam.sae_dam.ui.home.saisies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import iut.dam.sae_dam.R;

public class SaisieAdapter extends ArrayAdapter<Saisie> {

    private final Activity activity;
    private final int itemResourceId;
    private final List<Saisie> items;

    public SaisieAdapter(Activity activity, int itemResourceId, List<Saisie> items) {
        super(activity, itemResourceId, items);
        this.activity = activity;
        this.itemResourceId = itemResourceId;
        this.items = items;
    }

    private static class ViewHolder {
        TextView nomMedicamentTV;
        TextView codeCipTV;
        TextView dateSaisieTV;
        TextView pharmacieTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;
        ViewHolder viewHolder;

        if (layout == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            layout = inflater.inflate(itemResourceId, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nomMedicamentTV = layout.findViewById(R.id.nomMedicamentTV);
            viewHolder.codeCipTV = layout.findViewById(R.id.codeCisTV);
            viewHolder.dateSaisieTV = layout.findViewById(R.id.dateSaisieTV);
            viewHolder.pharmacieTV = layout.findViewById(R.id.pharmacieTV);

            layout.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) layout.getTag();
        }

        Saisie currentSaisie = items.get(position);

        viewHolder.nomMedicamentTV.setText(currentSaisie.getMedicament());
        viewHolder.codeCipTV.setText(currentSaisie.getCode());

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(activity.getApplicationContext());

        viewHolder.dateSaisieTV.setText(dateFormat.format(currentSaisie.getDateSaisie()));

        viewHolder.pharmacieTV.setText(currentSaisie.getPharmacie());

        return layout;
    }
}
