package iut.dam.sae_dam.saisies;

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
    Activity activity;
    int itemRessourceId;
    List<Saisie> items;

    public SaisieAdapter(Activity activity, int itemRessourceId, List<Saisie> items) {
        super(activity, itemRessourceId, items);
        this.activity = activity;
        this.itemRessourceId = itemRessourceId;
        this.items = items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            layout = inflater.inflate(itemRessourceId, parent, false);
        }

        //Récupérer les view
        TextView nomMedicamentTV = (TextView) layout.findViewById(R.id.itemSaisie_nomMedicamentTV);
        TextView codeCisTV = (TextView) layout.findViewById(R.id.itemSaisie_codeCisTV);
        TextView pharmacieTV = (TextView) layout.findViewById(R.id.itemSaisie_pharmacieTV);
        TextView dateSaisieTV = (TextView) layout.findViewById(R.id.itemSaisie_dateSaisieTV);

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(activity.getApplicationContext());

        nomMedicamentTV.setText(items.get(position).getMedicament().getDenomination());
        codeCisTV.setText(String.valueOf(items.get(position).getMedicament().getCisCode()));
        pharmacieTV.setText(String.valueOf(items.get(position).getPharmacie().getName()));
        dateSaisieTV.setText(dateFormat.format(items.get(position).getDateSaisie()));

        return layout;
    }


}
