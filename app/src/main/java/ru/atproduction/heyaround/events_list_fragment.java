package ru.atproduction.heyaround;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class events_list_fragment extends Fragment {
    FirebaseFirestore db;
    MapsActivity mapsActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.activity_events_list_fragment,null);

       db = FirebaseFirestore.getInstance();
        mapsActivity = (MapsActivity) getActivity();



        RecyclerView rv = view.findViewById(R.id.recycler_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);

        EditText editText = view.findViewById(R.id.etSearch);

        ImageButton imageButton = view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(view1 -> {
            String text = editText.getText().toString();
            if(text!=null && text!=" "){
               // Task<QuerySnapshot> task = db.collection("markers").whereEqualTo("name",text).get();
                Task<QuerySnapshot> task = db.collection("markers").orderBy("name").startAt(text).endAt(text + "\uf8ff").get();

                task.addOnCompleteListener(task1 -> {
                   if(task1.isSuccessful()){
                       ArrayList<String> data = new ArrayList<>();
                       ArrayList<LatLng> coords = new ArrayList<>();
                       for(DocumentSnapshot d:task1.getResult().getDocuments()){



                           data.add(d.getString("name"));
                           Map<String, Object> map = (Map<String, Object>) d.getData().get("coords");
                           LatLng lng = new LatLng((Double) map.get("latitude"),(Double) map.get("longitude"));
                           coords.add(lng);





                       }

                       Adapter adapter = new Adapter(data,coords);
                       rv.setAdapter(adapter);







                   }
                });

            }
        });










       return view;
    }
    private class Adapter extends RecyclerView.Adapter<Holder>{
        List<String> data;
        List<LatLng> latLngs;
        public Adapter(List<String> data, List<LatLng> latLngs){
            this.data = data;
            this.latLngs = latLngs;
        }


        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
           holder.textView.setText(data.get(position));
           holder.imageButton.setVisibility(View.INVISIBLE);
            holder.textView.setOnClickListener(view ->{
                InputMethodManager imm = (InputMethodManager) mapsActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                mapsActivity.zoomToMarker(latLngs.get(position));

            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class Holder extends  RecyclerView.ViewHolder{
        TextView textView;
        ImageButton imageButton;

        public Holder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.eventName);
            imageButton = itemView.findViewById(R.id.btnDelete);
            imageButton.setVisibility(View.INVISIBLE);

        }



    }
}
