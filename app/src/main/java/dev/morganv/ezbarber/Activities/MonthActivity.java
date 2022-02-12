package dev.morganv.ezbarber.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import dev.morganv.ezbarber.CallBacks.CallBack_List;
import dev.morganv.ezbarber.Fragments.ListFragment;
import dev.morganv.ezbarber.Models.Appointment;
import dev.morganv.ezbarber.Models.Treatment;
import dev.morganv.ezbarber.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MonthActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    boolean signInWithGoogle;
    String databaseReference;
    ArrayList<Appointment> appointmentsList = new ArrayList<>();
    Appointment appointment = new Appointment();

    MaterialTextView selectedDate;
    MaterialTextView selectedHour;
    MaterialButton btnConfirm;
    MaterialButton btnLogout;
    MaterialButton btnDetails;

    // Date&Time pickers
    DatePickerDialog datePickerDialog ;
    TimePickerDialog timePickerDialog ;
    int Year, Month, Day, Hour, Minute;

    // Database
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private final String TAG = "Month Activity";

    private ListFragment listFragment;

    Treatment treatments = new Treatment();
    Spinner treatmentsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        listFragment = new ListFragment();
        listFragment.setCallBackList(callBack_List);
        getSupportFragmentManager().beginTransaction().add(R.id.list_frame, listFragment).commit();

        signInWithGoogle = getIntent().getBooleanExtra("signInWithGoogle", false);
        setDatabaseReference();
        readFromDB();

        selectedDate = findViewById(R.id.calendar_selecteddate);
        selectedHour = findViewById(R.id.calendar_selectedhour);

        initDatePicker();
        initTimePicker();

        btnConfirm = findViewById(R.id.calendar_BTN_confirm);
        btnConfirm.setOnClickListener(view -> saveAppointmentToDB());
        btnLogout = findViewById(R.id.calendar_BTN_logout);
        btnLogout.setOnClickListener(view -> signOut());
        btnDetails = findViewById(R.id.calendar_BTN_details);
        btnDetails.setOnClickListener(v -> startActivity(new Intent(MonthActivity.this, DetailsActivity.class)));

        initSpinner();
    }

    private void initTimePicker() {
        final Button button_timepicker = (Button) findViewById(R.id.calendar_BTN_time);
        button_timepicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timePickerDialog = TimePickerDialog.newInstance(MonthActivity.this, Hour, Minute,true );
                timePickerDialog.setThemeDark(false);
                timePickerDialog.setTitle("Select Time");

                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(MonthActivity.this, "Timepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
            }
        });
    }

    private void initDatePicker() {
        final Button button_datepicker = (Button) findViewById(R.id.calendar_BTN_date);
        button_datepicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(MonthActivity.this, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Select Date");

                // Setting Min Date to today date
                Calendar min_date_c = Calendar.getInstance();
                datePickerDialog.setMinDate(min_date_c);

                // Setting Max Date to next 2 weeks
                Calendar max_date_c = Calendar.getInstance();
                max_date_c.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH+20);
                datePickerDialog.setMaxDate(max_date_c);

                //Disable all MONDAYS and SATURDAYS between Min and Max Dates
                for (Calendar loopdate = min_date_c; min_date_c.before(max_date_c); min_date_c.add(Calendar.DATE, 1), loopdate = min_date_c) {
                    int dayOfWeek = loopdate.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.SATURDAY) {
                        Calendar[] disabledDays =  new Calendar[1];
                        disabledDays[0] = loopdate;
                        datePickerDialog.setDisabledDays(disabledDays);
                    }
                }
                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(MonthActivity.this, "Datepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });
    }

    private void readFromDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                    String dbValue = snapshot1.getValue().toString();
                    String[] split = dbValue.split("-", 3);
                    Appointment temp = new Appointment();
                    temp.setDate(split[0]);
                    temp.setTime(split[1]);
                    temp.setTreatment(split[2]);
                    appointmentsList.add(temp);
                    Log.d(TAG, "Read from DB - Value is: " + dbValue);
                }
                Collections.sort(appointmentsList);
                listFragment.updateAppointment(appointmentsList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value from DB.", error.toException());
            }
        });
    }

    private void setDatabaseReference() {
        if (signInWithGoogle) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            databaseReference = acct.getId();
        } else {
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            databaseReference = currentUser.getUid();
        }
        myRef = database.getReference(databaseReference);
    }

    private void initSpinner() {
        treatmentsSpinner = findViewById(R.id.calendar_spinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, treatments.getTreatments());
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        treatmentsSpinner.setAdapter(adapter1);
        treatmentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appointment.setTreatment(treatmentsSpinner.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    CallBack_List callBack_List = new CallBack_List() {
        @Override
        public void rowSelected(int i) {
        }
    };

    private void signOut() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("signout", true);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        appointment.setDate(date);
        Toast.makeText(MonthActivity.this, date, Toast.LENGTH_LONG).show();
        selectedDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (checkValidTime(hourOfDay, minute)) {
            String mHour, mMinute;
            if (hourOfDay < 10) {
                mHour = "0" + hourOfDay;
            } else {
                mHour = "" + hourOfDay;
            }
            if (minute < 10) {
                mMinute = "0" + minute;
            } else {
                mMinute = "" + minute;
            }
            String time = mHour + ":" + mMinute;
            appointment.setTime(time);
            Toast.makeText(MonthActivity.this, time, Toast.LENGTH_LONG).show();
            selectedHour.setText(time);
        }
    }

    private void saveAppointmentToDB() {
        if(!appointment.isComplete()){
            AlertDialog.Builder appointmentDialog = new AlertDialog.Builder(MonthActivity.this);
            appointmentDialog.setMessage("You must select date, time and treatment to create an appointment!").setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = appointmentDialog.create();
            alert.setTitle("Appointment error");
            alert.show();
            return;
        }
        for (int i=0; i<appointmentsList.size(); i++){
            if (appointmentsList.get(i).equals(appointment)){
                AlertDialog.Builder appointmentDialog = new AlertDialog.Builder(MonthActivity.this);
                appointmentDialog.setMessage("Appointment already exists.\nplease schedule a new appointment").setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = appointmentDialog.create();
                alert.setTitle("Appointment error");
                alert.show();
                return;
            }
        }
        String dbValue = appointment.getDate() + "-" + appointment.getTime() + "-" + appointment.getTreatment();
        myRef.child(String.valueOf(Calendar.getInstance().getTime())).setValue(dbValue);
        Log.d(TAG, "Added to DB: " + dbValue);
    }

    private boolean checkValidTime(int hourOfDay, int minute) {
        if(hourOfDay < 8 || hourOfDay > 17) {
            AlertDialog.Builder hoursDialog = new AlertDialog.Builder(MonthActivity.this);
            hoursDialog.setMessage("Hour must be between 08:00 - 17:45").setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = hoursDialog.create();
            alert.setTitle("Hour error");
            alert.show();
            return false;
        }
        if(minute%15 != 0){
            AlertDialog.Builder minutesDialog = new AlertDialog.Builder(MonthActivity.this);
            minutesDialog.setMessage("Accepted minutes are 00, 15, 30 or 45 only! ").setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = minutesDialog.create();
            alert.setTitle("Minute error");
            alert.show();
            return false;
        }
        return true;
    }
}