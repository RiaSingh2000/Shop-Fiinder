package Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import Models.PlacesApi;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlacesAutoCompleteAdapter extends ArrayAdapter implements Filterable {
    ArrayList<String> results;
    Context context;
    int resource;
    PlacesApi placesApi=new PlacesApi();

    public PlacesAutoCompleteAdapter(@NonNull Context context, int resource) {
        super(context,resource);
        this.context=context;
        this.resource=resource;
    }


    public int getCount() {
        return results.size();
    }

    public  String getItem(int pos){
        return results.get(pos);
    }
    @Override
    public Filter getFilter(){
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults=new FilterResults();
                if(charSequence!=null){
                    results=placesApi.autoComplete(charSequence.toString());
                    filterResults.values=results;
                    filterResults.count=results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(results!=null)
                    notifyDataSetChanged();
                else
                    notifyDataSetInvalidated();

            }
        };
        return filter;
    }
}
