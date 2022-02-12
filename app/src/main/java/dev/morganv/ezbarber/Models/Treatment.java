package dev.morganv.ezbarber.Models;

public class Treatment {

    private String[] treatments = new String[10];

    public Treatment(){
        treatments[0] =  "Blow Dry 50$";
        treatments[1] =  "Blow Dry with curling 55$";
        treatments[2] =  "Hair Cut with Blow Dry 65$";
        treatments[3] =  "Mens Hair Cut 40$";
        treatments[4] =  "Basic Tint 65$";
        treatments[5] =  "Full Highlights 100$";
        treatments[6] =  "Partial Highlights 75$";
        treatments[7] =  "Gloss 40$";
        treatments[8] =  "Perms 100$";
        treatments[9] =  "Relaxers 50$";
    }

    public String[] getTreatments() {
        return treatments;
    }
}
