package ru.atproduction.heyaround;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class account_fragment extends Fragment {

    FirebaseFirestore db;
    ArrayList<String> data;
    ArrayList<LatLng> coords;
    RecyclerView recyclerView;
    MapsActivity mapsActivity;
    String id;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        id = getArguments().getString("ID");
        mapsActivity = (MapsActivity) getActivity();


        View view = inflater.inflate(R.layout.activity_account_fragment,null);



        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            Log.d("OLOL","editAcc");
            Fragment fragment = new EditAcc();
            Bundle bundle = new Bundle();
            bundle.putString("id",id);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = mapsActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
            fragmentTransaction.remove(this).add(R.id.container,fragment);
            fragmentTransaction.commit();
            mapsActivity.fragment = fragment;
        });

        recyclerView = view.findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //Getting datas
        data = new ArrayList<>();
        coords = new ArrayList<>();
        getData(id);





        TextView name = view.findViewById(R.id.TextViewName);
        TextView email = view.findViewById(R.id.textView9);

       // User user = new User();
        if(id !=null) {
            Task<DocumentSnapshot> datas = db.collection("users").document(id).get();
            datas.addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                name.setText(documentSnapshot.getString("name"));
                String s = "E-mail: "+documentSnapshot.getString("email");
                email.setText(s);

            });
        }
     //   name.setText(MapsActivity.user.getName());
      //  Log.d("OLOL",""+MapsActivity.user.getName().isEmpty());

        Button btnExit = view.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(),LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }


    private void deleteMarker(String name, LatLng lng){
        Log.d("OLOL", "start deleting");
        ArrayList<String> ids = new ArrayList<>();

        Task<QuerySnapshot> task = db.collection("markers").whereEqualTo("owner", id).whereEqualTo("name", name).whereEqualTo("coords", lng).get();
        task.addOnCompleteListener(task1 -> {
           if(task1.isSuccessful()){
                for (DocumentSnapshot d:task1.getResult().getDocuments()) {
                    db.collection("markers").document(d.getId()).delete();
//                    Map<String, Object> map = new HashMap<>();
//                    map.put(d.getId(),FieldValue.delete())
                    db.collection("users").document(id).update("markers",FieldValue.arrayRemove(d.getId()));

                    Log.d("OLOL", "delete " + d.getId());
                }
               mapsActivity.updateMarkers();
           }
        });


    }

    private void getData(String id){

        Task<QuerySnapshot> task = db.collection("markers").whereEqualTo("owner",id).get();
        task.addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                QuerySnapshot result = task1.getResult();
                for(DocumentSnapshot d: result.getDocuments()){
                    data.add(d.getString("name"));
                    Map<String, Object> map = (Map<String, Object>) d.getData().get("coords");
                    LatLng lng = new LatLng((Double) map.get("latitude"),(Double) map.get("longitude"));
                    coords.add(lng);
                }
                Adapter adapter = new Adapter(data, coords);
                recyclerView.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(getContext(), "Error. Please, try again.", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private class Adapter extends RecyclerView.Adapter<Holder>{
        List<String> datas;
        List<LatLng> latLngs;
        public Adapter(List<String> data, List<LatLng> latLngs){
            this.datas = data;
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
                holder.textView.setText(datas.get(position));
                holder.textView.setOnClickListener(view ->{
                    InputMethodManager imm = (InputMethodManager) mapsActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    mapsActivity.zoomToMarker(latLngs.get(position));

                });

                holder.imageButton.setOnClickListener(view -> {
                    deleteMarker(data.get(position),latLngs.get(position));
                    data.remove(position);
                    latLngs.remove(position);

                    notifyDataSetChanged();
                });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class Holder extends  RecyclerView.ViewHolder{
        TextView textView;
        ImageButton imageButton;

        public Holder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.eventName);
            imageButton = itemView.findViewById(R.id.btnDelete);

        }



    }
}
