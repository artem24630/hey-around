package ru.atproduction.heyaround;

import static android.app.Activity.RESULT_OK;
import static ru.atproduction.heyaround.MapsActivity.db;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class EditProfileFragment extends Fragment {
    private String id;
    private Uri selectedImage;

    private ImageView imageView;
    private MapsActivity activity;
    private StorageReference mStorageRef;
    static final int GALLERY_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mStorageRef = FirebaseStorage.getInstance().getReference();
        id = getArguments().getString("id");
        activity = (MapsActivity) getActivity();
        View view = inflater.inflate(R.layout.edit_acc, null);
        imageView = view.findViewById(R.id.roundIm);
        EditText name = view.findViewById(R.id.editTextChangeName);
        EditText email = view.findViewById(R.id.editTextChangeEmail);
        Button change = view.findViewById(R.id.button2);
        change.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        });

        Button save = view.findViewById(R.id.button3);
        save.setOnClickListener(v -> {
            String editName, editEmail;
            editName = name.getText().toString();
            editEmail = email.getText().toString();

            uploadImage();

            if (editEmail != "" && editName != "") {
                db.collection("users").document(id).update("name", editName, "email", editEmail);
                Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Please, write name and email", Toast.LENGTH_SHORT).show();

        });
        Handler mHandler = new Handler(msg -> {
            name.setText(msg.getData().getString("name"));
            email.setText(msg.getData().getString("email"));

            return true;
        });

        Task<DocumentSnapshot> users = db.collection("users").document(id).get();
        users.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("name", task.getResult().getString("name"));
                bundle.putString("email", task.getResult().getString("email"));
                message.setData(bundle);
                mHandler.sendMessage(message);


            }
        });

        mStorageRef.child("images/" + id).getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d("OLOL","loadsas "+uri.toString());
                Bitmap bitmap;

                    Glide.with(activity).load(uri).into(imageView);
//                bitmap = changeSize(bitmap);
//                imageView.setImageBitmap(bitmap);




                               }


        );

        return view;
    }

    private void uploadImage() {
        if (selectedImage != null) {
            StorageReference sr = mStorageRef.child("images/" + id);
            sr.putFile(selectedImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(activity, "Image was uploaded", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(task -> {
                Toast.makeText(activity, "Image wasn't uploaded", Toast.LENGTH_SHORT).show();
            });


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;


        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int h = bitmap.getHeight();
                    int w = bitmap.getWidth();
                    if (h > 4096 || w > 4096) {

                        if (h > w) {
                            float d = (float) h / 4096.0f;
                            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (w / d), 4096, false);

                        } else {
                            float d = (float) w / 4096.0f;
                            bitmap = Bitmap.createScaledBitmap(bitmap, 4096, (int) (h / d), false);
                        }


                        imageView.setImageBitmap(bitmap);
                    } else
                        imageView.setImageBitmap(bitmap);


                }
        }
    }


        private Bitmap changeSize (Bitmap bitmap){
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();

            if (h > 4096 || w > 4096) {

                if (h > w) {
                    float d = (float) h / 4096.0f;
                    bitmap = (Bitmap) Bitmap.createScaledBitmap(bitmap, (int) (w / d), 4096, false);

                } else {
                    float d = (float) w / 4096.0f;
                    bitmap = (Bitmap) Bitmap.createScaledBitmap(bitmap, 4096, (int) (h / d), false);
                }


            }
            return bitmap;
        }
    }



