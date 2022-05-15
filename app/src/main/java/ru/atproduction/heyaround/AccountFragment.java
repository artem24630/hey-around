package ru.atproduction.heyaround;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountFragment extends Fragment {

    private FirebaseFirestore db;
    private ArrayList<String> data;
    private ArrayList<LatLng> coords;
    private RecyclerView recyclerView;
    private MapsActivity mapsActivity;
    private String id;
    private FirebaseUser userFire;
    private FirebaseAuth myAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        db = FirebaseFirestore.getInstance();
        myAuth = FirebaseAuth.getInstance();
        userFire = myAuth.getCurrentUser();
        User user = new User();
        user.setEmail(userFire.getEmail());
        user.setName(userFire.getDisplayName());
        user.setIdAuth(userFire.getUid());
        Query query = db.collection("users").whereEqualTo("idAuth", user.getIdAuth());
        Task<QuerySnapshot> document = query.get();
        View view = inflater.inflate(R.layout.activity_account_fragment, null);
        TextView name = view.findViewById(R.id.TextViewName);
        TextView email = view.findViewById(R.id.textView9);


        // getArguments().getString("ID");
        mapsActivity = (MapsActivity) getActivity();


        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            Fragment fragment = new EditAcc();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = mapsActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(this).add(R.id.container, fragment);
            fragmentTransaction.commit();
            mapsActivity.fragment = fragment;
        });

        recyclerView = view.findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //Getting datas
        data = new ArrayList<>();
        coords = new ArrayList<>();


        // User user = new User();
        if (id != null)
        {

        }
        Button btnExit = view.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });
        document.addOnSuccessListener(documentSnapshots -> {
            for (DocumentSnapshot d : documentSnapshots.getDocuments()
            )
            {
                Log.d("OLOL", "got " + d.get("idAuth") + " " + user.getIdAuth());
                if (d.get("idAuth").equals(user.getIdAuth()))
                {
                    user.setId(d.getId());
                    id = user.getId();
                    Log.d("OLOL", "setId");
                    Task<DocumentSnapshot> datas = db.collection("users").document(id).get();
                    datas.addOnCompleteListener(task -> {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        name.setText(documentSnapshot.getString("name"));
                        String s = "E-mail: " + documentSnapshot.getString("email");
                        email.setText(s);

                    });
                    getData(id);
                    break;
                }
            }

        });
        return view;
    }


    private void deleteMarker(String name, LatLng lng)
    {
        ArrayList<String> ids = new ArrayList<>();

        Task<QuerySnapshot> task = db.collection("markers").whereEqualTo("owner", id).whereEqualTo("name", name).whereEqualTo("coords", lng).get();
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful())
            {
                for (DocumentSnapshot d : task1.getResult().getDocuments())
                {
                    db.collection("markers").document(d.getId()).delete();
//                    Map<String, Object> map = new HashMap<>();
//                    map.put(d.getId(),FieldValue.delete())
                    db.collection("users").document(id).update("markers", FieldValue.arrayRemove(d.getId()));

                    Log.d("OLOL", "delete " + d.getId());
                }
                mapsActivity.updateMarkers();
            }
        });


    }

    private void getData(String id)
    {

        Task<QuerySnapshot> task = db.collection("markers").whereEqualTo("owner", id).get();
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful())
            {
                QuerySnapshot result = task1.getResult();
                for (DocumentSnapshot d : result.getDocuments())
                {
                    data.add(d.getString("name"));
                    Map<String, Object> map = (Map<String, Object>) d.getData().get("coords");
                    LatLng lng = new LatLng((Double) map.get("latitude"), (Double) map.get("longitude"));
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

    private class Adapter extends RecyclerView.Adapter<Holder> {
        List<String> datas;
        List<LatLng> latLngs;

        public Adapter(List<String> data, List<LatLng> latLngs)
        {
            this.datas = data;
            this.latLngs = latLngs;
        }


        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position)
        {
            holder.textView.setText(datas.get(position));
            holder.textView.setOnClickListener(view -> {
                InputMethodManager imm = (InputMethodManager) mapsActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                mapsActivity.zoomToMarker(latLngs.get(position));

            });

            holder.imageButton.setOnClickListener(view -> {
                deleteMarker(data.get(position), latLngs.get(position));
                data.remove(position);
                latLngs.remove(position);

                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount()
        {
            return datas.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton imageButton;

        public Holder(View itemView)
        {
            super(itemView);
            textView = itemView.findViewById(R.id.eventName);
            imageButton = itemView.findViewById(R.id.btnDelete);

        }


    }
}
