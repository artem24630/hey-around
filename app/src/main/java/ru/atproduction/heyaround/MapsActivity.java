package ru.atproduction.heyaround;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterManager;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.atproduction.heyaround.IdleResource.DialogIdleResource;
import ru.atproduction.heyaround.IdleResource.IdlingResources;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FirebaseUser userFire;
    private FirebaseAuth myAuth;
    static FirebaseFirestore db;
    private StorageReference st;
    Fragment fragment;
    AlertDialog dl;
    SupportMapFragment mapFragment;
    private User user;
    @Nullable
    private DialogIdleResource mIdlingResource = IdlingResources.dialogIdlingResource;
    static ClusterManager<AbstractMarker> clusterManager;
    private static boolean LocationPermision = false;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private GoogleMap mMap;
    private Drawer.Result drawer;
    private AbstractMarker chosenMarker;
    private static String description2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        st = FirebaseStorage.getInstance().getReference();
        if (mIdlingResource != null)
        {
            mIdlingResource.setIdleState(false);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        drawer = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_account).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_map).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(3)
                        //new DividerDrawerItem(),
                        //new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog),
                        //new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withIdentifier(1)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView)
                    {
                        InputMethodManager inputMethodManager = (InputMethodManager) MapsActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(MapsActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView)
                    {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem)
                    {
                        if (drawerItem instanceof Nameable)
                            selectFragment(position);
                    }

                    private void selectFragment(int pos)
                    {
                        Fragment newFragment = null;
                        boolean flag = false;
//                        if(pos==2)
//                             flag = true;
//                         if(pos==1) {
//                             newFragment = new account_fragment();
//                         flag = false;
//                         }

                        switch (pos)
                        {
                            case 1:
                            {
                                newFragment = new AccountFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("ID", user.getId());
                                newFragment.setArguments(bundle);


                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                if (fragment != null)
                                    fragmentTransaction.hide(mapFragment).add(R.id.container, newFragment).remove(fragment);
                                else
                                    fragmentTransaction.hide(mapFragment).add(R.id.container, newFragment);


                                fragmentTransaction.commit();


                                fragment = newFragment;


                            }
                            break;
                            case 2:
                            {

                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                if (fragment != null)
                                    fragmentTransaction.show(mapFragment).remove(fragment);
                                else
                                    fragmentTransaction.show(mapFragment);
                                fragmentTransaction.commit();

                                fragment = null;

                            }
                            break;
                            case 3:
                            {
                                newFragment = new EventsListFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("ID", user.getId());
                                newFragment.setArguments(bundle);

                                Log.d("OLOL", "changeFragmentsTo3");

                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                if (fragment != null)
                                    fragmentTransaction.hide(mapFragment).add(R.id.container, newFragment).remove(fragment);
                                else
                                    fragmentTransaction.hide(mapFragment).add(R.id.container, newFragment);


                                fragmentTransaction.commit();


                                fragment = newFragment;

                            }
                        }


//                         if(flag && fragment!=null && mapFragment!=null){
//                             FragmentManager fragmentManager = getSupportFragmentManager();
//                             FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
//                             fragmentTransaction.show(mapFragment).remove(fragment);
//
//
//                             fragmentTransaction.commit();
//
//
//                             //fragment = newFragment;
//                         }
//                         else if(newFragment!=null)
//                         {
//                             Bundle bundle = new Bundle();
//                             bundle.putString("ID",user.getId());
//                             newFragment.setArguments(bundle);
//
//                             Log.d("OLOL","changeFragments");
//
//                             FragmentManager fragmentManager = getSupportFragmentManager();
//                             FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
//                             fragmentTransaction.hide(mapFragment).add(R.id.container,newFragment);
//
//                             fragmentTransaction.commit();
//
//
//                             fragment = newFragment;
//                         }


                    }
                })
                .build();


        // FirebaseApp.initializeApp(this);


        db = FirebaseFirestore.getInstance();
        myAuth = FirebaseAuth.getInstance();
        userFire = myAuth.getCurrentUser();

        setUserInfo();


        if (getIntent().getBooleanExtra("isFirstTime", false))
        {
            Log.d("OLOL", "First Time");

            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            EditText input = new EditText(MapsActivity.this);
            input.setId(R.id.edit_text_name);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            input.setLayoutParams(lp);

            builder.setView(input);
            builder.setTitle("Write your name");
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                user.setName(input.getText().toString());
                Map<String, Object> usr = new HashMap<>();

                usr.put("name", user.getName());//TODO ввод имени пользователя
                usr.put("email", user.getEmail());

                usr.put("idAuth", user.getIdAuth());
                db.collection("users").add(usr).addOnSuccessListener(documentReference -> {
                    user.setId(documentReference.getId());

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                });
            });
            builder.show();
            if (mIdlingResource != null)
            {
                mIdlingResource.setIdleState(true);
            }

        }
        else
        {
            //Берем id пользователя из Firestore, записываем в user
            Query query = db.collection("users").whereEqualTo("idAuth", user.getIdAuth());
            Task<QuerySnapshot> document = query.get();
            document.addOnSuccessListener(documentSnapshots -> {
                for (DocumentSnapshot d : documentSnapshots.getDocuments()
                )
                {
                    Log.d("OLOL", "got " + d.get("idAuth") + " " + user.getIdAuth());
                    if (d.get("idAuth").equals(user.getIdAuth()))
                    {
                        user.setId(d.getId());
                        Log.d("OLOL", "setId");
                        break;
                    }
                }

            });


            //TODO Переписать в onStart()
        }


        checkPermision();
        drawer.setSelectionByIdentifier(2, false);
//        if(LocationPermision) {
//
//
//        }
//        else{
//            //TODO надпись с просьбой разрешить геопозицию
//        }
    }


    private void setUpMap()
    {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.container,mapFragment).commit();
    }

    private void checkPermision()
    {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED)
        {
            setUpMap();
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            //LocationPermision = false;
        }
    }


    public void zoomToMarker(LatLng lng)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment != null)
            fragmentTransaction.show(mapFragment).remove(fragment);
        else
            fragmentTransaction.show(mapFragment);
        fragmentTransaction.commit();

        fragment = null;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lng, 15);
        mMap.moveCamera(cameraUpdate);
        drawer.setSelection(2);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setUpMap();

                }
                else
                {
                    // LocationPermision = false;
                    Toast.makeText(this, "Please, allow permision", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private void setUserInfo()
    {
        user = new User();
        user.setEmail(userFire.getEmail());
        user.setName(userFire.getDisplayName());
        user.setIdAuth(userFire.getUid());
        Log.d("OLOL", "SetUserInfo");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen())
        {
            drawer.closeDrawer();
        }
        else
        {
            super.onBackPressed();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        mMap.setOnMapLongClickListener(latLng -> {

            Intent intent = new Intent(MapsActivity.this, CreateEvent.class);
            intent.putExtra("LatLng", latLng);
            intent.putExtra("userId", user.getId());
            //  intent.putExtra("ref", (Parcelable) myRef);
            startActivity(intent);

        });

        clusterManager = new ClusterManager<AbstractMarker>(this.getApplicationContext(), mMap);

        clusterManager.setOnClusterItemClickListener(item -> {
            chosenMarker = item;
            Log.d("OLOL", "chosenMarker " + item.getName());
            return false;
        });


        addMarkersToCluster();
        clusterManager.cluster();
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MarkerInfoWindowAdapter());


        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        //---------------------------------------------------------------------


        mMap.setOnInfoWindowClickListener(marker -> {
            description2 = null;


            AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            View view2 = getLayoutInflater().inflate(R.layout.event_dialog_layout, null);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, HH:mm, z");
            RecyclerView recyclerView = view2.findViewById(R.id.rvUsers);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            TextView numberUs = view2.findViewById(R.id.tvNumberUs);

            //getData(recyclerView);
            AtomicReference<ArrayList<String>> users = new AtomicReference<>(new ArrayList<>());
            Task<DocumentSnapshot> markers = db.collection("markers").document(chosenMarker.getId()).get();
            markers.addOnSuccessListener(documentSnapshot -> {
                users.set((ArrayList<String>) documentSnapshot.get("users"));

                // Log.d("OLOL","getusers " + documentSnapshot.get("users"));
                getData(recyclerView, users, numberUs);
            });


            TextView timeTv = view2.findViewById(R.id.time);
            TextView name = view2.findViewById(R.id.name);
            TextView description = view2.findViewById(R.id.description);
            name.setText(chosenMarker.getName());


            DocumentReference document = db.collection("markers").document(chosenMarker.getId());
            document.get().addOnCompleteListener(task -> {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document2 = task.getResult();
                    description2 = document2.getString("description");
                    Log.d("OLOL", description2);
                    description.setText(description2);
                    timeTv.setText(simpleDateFormat.format(document2.getTimestamp("time").toDate()));
                }
            });

            ImageButton close = view2.findViewById(R.id.closeBtn);
            close.setOnClickListener((View) -> {
                dl.cancel();
                Log.d("OLOL", "close dialog");

            });

            // description.setText(description2);//TODO проверка на уже присоединенного пользователя к данному ивенту
            Button join = view2.findViewById(R.id.btnJoin);
            if (chosenMarker.getOwner().equals(user.getId()))
            {
                join.setEnabled(false);
            }
            else
            {
                join.setEnabled(true);


                ArrayList<String> checkUser = users.get();
                if (checkUser.contains(user.getId()))
                    join.setEnabled(false);

                join.setOnClickListener((olol) -> {

                    addJoinedUser(view2);


                });
            }

            dialog.setView(view2);

            dl = dialog.create();

            dl.show();
            //dialog.show();
        });


    }

    private void getData(RecyclerView recyclerView, AtomicReference<ArrayList<String>> users, TextView numberUs)
    {
        ArrayList<Uri> uris = new ArrayList<>();
        ArrayList<String> user = users.get();
        // Log.d("OLOL","dsa" + user.get(1));
        Log.d("OLOL", "in getData");
        if (user != null)
            numberUs.setText((user.size()) + " joined users");
        else
            numberUs.setText(0 + " joined users");
        st.child("images/" + chosenMarker.getOwner()).getDownloadUrl().addOnCompleteListener(task -> {
            boolean flag = true;
            if (task.isSuccessful())
            {
                uris.add(task.getResult());
                Log.d("OLOL", "uris++");
                if (user == null)
                {
                    Adapter adapter = new Adapter(uris);
                    recyclerView.setAdapter(adapter);
                    flag = false;
                }


            }
            else
            {
                uris.add(null);
            }

            if (flag)
            {
                AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids)
                    {
                        AtomicReference<Byte> next = new AtomicReference<>((byte) 0);
                        if (user != null)
                            for (String a : user)
                            {
                                next.set((byte) 1);
                                st.child("images/" + a).getDownloadUrl().addOnSuccessListener(uri -> {
                                    uris.add(uri);
                                    next.set((byte) 0);
                                }).addOnFailureListener(e -> {
                                    uris.add(null);
                                    next.set((byte) 0);
                                });
                                while (next.get() == 1)
                                {
                                    try
                                    {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }

                            }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid)
                    {
                        super.onPostExecute(aVoid);
                        Adapter adapter = new Adapter(uris);
                        recyclerView.setAdapter(adapter);
                        // Log.d("OLOL", "recycle is ready"+uris.get(0));
                    }
                };
                asyncTask.execute();


            }
        });


    }


    private void addMarkersToCluster()
    {
        Query query = db.collection("markers");
        Task<QuerySnapshot> document = query.get();

        document.addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> map;
            for (DocumentSnapshot d : documentSnapshot.getDocuments()
            )
            {
                map = (Map<String, Object>) d.getData().get("coords");


                clusterManager.addItem(new AbstractMarker((double) map.get("latitude"), (double) map.get("longitude"), d.getString("name"), d.getId(), d.getString("owner")));


            }


        });

    }

    public void updateMarkers()
    {
        clusterManager.clearItems();
        addMarkersToCluster();
        clusterManager.cluster();
    }

//    @Override
//    public void onInfoWindowClick(Marker marker) {
//
//    }

    private class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker)
        {
            if (chosenMarker != null)
            {


                View v = getLayoutInflater().inflate(R.layout.marker_info_window, null);
                TextView textView = v.findViewById(R.id.window);
                textView.setText(chosenMarker.getName());

                return v;
            }


            return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            return null;
        }
    }

    private boolean addJoinedUser(View view2)
    {
        ProgressBar progressBar = view2.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Task<Void> update = db.collection("markers").document(chosenMarker.getId()).update("users", FieldValue.arrayUnion(user.getId()));
        view2.findViewById(R.id.btnJoin).setEnabled(false);
        update.addOnCompleteListener(task -> {
            progressBar.setVisibility(View.INVISIBLE);
            if (task.isSuccessful())
                Toast.makeText(this, "Joined", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        });

        return true;

    }


    private class Adapter extends RecyclerView.Adapter<Holder> {
        List<Uri> datas;

        public Adapter(List<Uri> data)
        {
            datas = data;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rvcircle, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position)
        {
            Log.d("OLOL", "bindView" + position + " " + datas.get(position));
            if (datas.get(position) != null)
                Glide.with(MapsActivity.this).load(datas.get(position)).override(4096, 4096).fitCenter().into(holder.circleImageView);
            else
                holder.circleImageView.setImageResource(R.drawable.ic_menu_camera);
        }

        @Override
        public int getItemCount()
        {
            return datas.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;

        public Holder(View itemView)
        {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.roundIm);
        }
    }

//    @VisibleForTesting
//    @NonNull
//    public IdlingResource getIdlingResource()
//    {
//        if (mIdlingResource == null)
//        {
//            mIdlingResource = new DialogIdleResource();
//        }
//        return mIdlingResource;
//    }
}
