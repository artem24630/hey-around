package ru.atproduction.heyaround;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CreateEvent extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {



    private TextView currentDateTime;
    private Calendar dateAndTime=Calendar.getInstance();
    private Switch swt;
    private  EditText name;
    private LatLng latLng;
    private int number;
    private EditText description;
    private String userId;
    private EditText numberOfPersons;
//    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

number =-1;
        latLng = getIntent().getParcelableExtra("LatLng");
        userId = getIntent().getStringExtra("userId");
        //myRef = MapsActivity.myRef;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);//кнопка назад
        name = findViewById(R.id.editText);
        description = findViewById(R.id.editText4);

        numberOfPersons = findViewById(R.id.editText2);
        numberOfPersons.setVisibility(View.INVISIBLE);

        swt = findViewById(R.id.switch1);
        if(swt != null){
                swt.setOnCheckedChangeListener(this);

        }

        currentDateTime = findViewById(R.id.date);
        setInitialDateTime();// установка текущей даты в currentDateTime
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setInitialDateTime() {

        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    public void setDate(View v) {
        new TimePickerDialog(CreateEvent.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();


        new DatePickerDialog(CreateEvent.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();


    }

    // отображаем диалоговое окно для выбора времени
//    public void setTime(View v) {
//
//    }


    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            numberOfPersons.setVisibility(View.INVISIBLE);
            number=-1;

        }
        else

            numberOfPersons.setVisibility(View.VISIBLE);
    }

    public void createBtn(View view) {
        if(name == null || description == null)
        {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
        }else if((numberOfPersons.getVisibility()==View.VISIBLE) & (numberOfPersons.getText()==null)){
            Toast.makeText(this, "Fill No.of persons", Toast.LENGTH_SHORT).show();

        }
        else{

            if(numberOfPersons.getVisibility() == View.VISIBLE)
                number = Integer.parseInt(numberOfPersons.getText().toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateEvent.this);
            builder.setTitle("Important message");
            builder.setMessage("All events last 1 day, after that it will be deleted");
            builder.setNegativeButton("I understand", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    if(toDataBase()==1)
                        return;
                    else{
                        Intent intent = new Intent(CreateEvent.this, MapsActivity.class);
                        startActivity(intent);
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();


            //Log.d("DataBase","Push");
//            myRef.child("users").child("marker").child("name").setValue(name);
//            myRef.child("users").child("marker").child("description").setValue(description);



        }
    }

    private int toDataBase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<Object,Object> marker = new HashMap<>();
        marker.put("name",name.getText().toString());
        marker.put("description",description.getText().toString());
        marker.put("coords",latLng);
        marker.put("users",null);
        marker.put("numberOfPersons",number);
        marker.put("time",dateAndTime.getTime());
        marker.put("owner",userId);
        AtomicReference<String> documentId = new AtomicReference<>();
        db.collection("markers").add(marker).addOnSuccessListener(documentReference -> {
                documentId.set(documentReference.getId());
            String docid = documentId.get();
           // Toast.makeText(this, "All is ok", Toast.LENGTH_SHORT).show();
            db.collection("users").document(userId).update("markers", FieldValue.arrayUnion(docid));

        }).addOnFailureListener(e -> {
                documentId.set(null);
                });
        if(documentId == null)
            return 1;
        else
            return 0;



    }
}
