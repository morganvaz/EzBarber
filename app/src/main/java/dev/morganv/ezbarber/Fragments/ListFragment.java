package dev.morganv.ezbarber.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dev.morganv.ezbarber.CallBacks.CallBack_List;
import dev.morganv.ezbarber.Models.Appointment;
import dev.morganv.ezbarber.R;

public class ListFragment extends Fragment {
    private final String TAG = "List Activity";
    private CallBack_List callBackList;
    private MaterialTextView[] appointments = new MaterialTextView[10];

    public void setCallBackList(CallBack_List callBackList) {
        this.callBackList = callBackList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        initViews(view);
        return view;
    }

    public void updateAppointment(ArrayList<Appointment> appointmentList){
        for (MaterialTextView appointment : appointments)
            appointment.setText("");

        int length;
        if (appointmentList.size() > 10)
            length = 10;
        else
            length = appointmentList.size();

        for (int i=0; i<length; i++){
            if (!appointmentList.get(i).isAddedToList()){
                appointmentList.get(i).setAddedToList(true);
                appointments[i].setText(appointmentList.get(i).toString());
            }
        }
    }

    private void initViews(View view) {
        appointments[0] = view.findViewById(R.id.list_LBL_appointment1);
        appointments[1] = view.findViewById(R.id.list_LBL_appointment2);
        appointments[2] = view.findViewById(R.id.list_LBL_appointment3);
        appointments[3] = view.findViewById(R.id.list_LBL_appointment4);
        appointments[4] = view.findViewById(R.id.list_LBL_appointment5);
        appointments[5] = view.findViewById(R.id.list_LBL_appointment6);
        appointments[6] = view.findViewById(R.id.list_LBL_appointment7);
        appointments[7] = view.findViewById(R.id.list_LBL_appointment8);
        appointments[8] = view.findViewById(R.id.list_LBL_appointment9);
        appointments[9] = view.findViewById(R.id.list_LBL_appointment10);
    }
}