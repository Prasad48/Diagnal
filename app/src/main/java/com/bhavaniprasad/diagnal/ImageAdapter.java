package com.bhavaniprasad.diagnal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Customview> implements Filterable {

    private ArrayList<HashMap<String,String>> arrlist;
    private ArrayList<HashMap<String,String>> arrListall;

    private Context cnt;
    LayoutInflater layoutInflater;

    public ImageAdapter(Context context, ArrayList<HashMap<String, String>> formList) {
        this.cnt=context;
        this.arrlist=formList;
        this.arrListall=new ArrayList<>(formList);
    }

    @NonNull
    @Override
    public ImageAdapter.Customview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = layoutInflater.from(parent.getContext())
                .inflate(R.layout.photos_list_row, parent, false);
        return new Customview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.Customview holder, int position) {
        final Customview viewholder = (Customview) holder;
        viewholder.name.setText(arrlist.get(position).get("name"));
        String imagename= arrlist.get(position).get("poster-image");
        imagename=imagename.replaceAll(imagename.substring(imagename.length()-4),"");
        int img=cnt.getResources().getIdentifier(imagename,"drawable",cnt.getPackageName());
        if(img==0)
            img=cnt.getResources().getIdentifier("placeholder_for_missing_posters","drawable",cnt.getPackageName());
        Picasso.with(cnt).load(img).resize(200, 200).centerCrop().onlyScaleDown()
                .into(viewholder.imageview);

    }

    @Override
    public int getItemCount() {
        return arrlist.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


     Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<HashMap<String,String>> filteredList =  new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                filteredList.addAll(arrListall);
            }
            else{
                try {
                    for(HashMap<String,String> list:arrListall){
                        if(list.get("name").toLowerCase().contains(charSequence.toString().toLowerCase())){
                            filteredList.add(list);
                        }
                    }
                }
                catch (Exception e){
                    Log.e("sdf","except"+e);
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            arrlist.clear();
            arrlist.addAll((Collection<? extends HashMap<String,String>>) filterResults.values);
            notifyDataSetChanged();
        }
    };



    class Customview extends RecyclerView.ViewHolder {

        TextView name;
        ImageView imageview;

        public Customview(@NonNull View itemView) {
            super(itemView);
            imageview = (ImageView)itemView.findViewById(R.id.gallery_pic);
            name = itemView.findViewById(R.id.pic_description);
        }
    }

}
