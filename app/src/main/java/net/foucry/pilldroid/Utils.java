package net.foucry.pilldroid;

import net.foucry.pilldroid.models.Medicine;
import net.foucry.pilldroid.models.Prescription;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Utils {
    private static final String TAG = Utils.class.getName();

    /**
     * Return a random number between two values - use to generate a false demo DB
     * @param min minimal value accepted
     * @param max maximum value accepted
     * @return int random number
     */
    static int intRandomExclusive(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min) +max;
    }

    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format(Locale.getDefault(),"%d",(long)d);
        else
            return String.format("%s",d);
    }

    public static Prescription medicine2prescription(Medicine aMedicine) {
        Prescription aPrescription = new Prescription();

        aPrescription.setName(aMedicine.getName());
        aPrescription.setCis(aMedicine.getCis());
        aPrescription.setCip13(aMedicine.getCip13());
        aPrescription.setPresentation(aMedicine.getPresentation());
        aPrescription.setAdministration_mode(aMedicine.getAdministration_mode());
        aPrescription.setStock(0F);
        aPrescription.setAlert(7);
        aPrescription.setTake(0F);
        aPrescription.setWarning(14);
        aPrescription.setLast_update(UtilDate.dateAtNoon(new Date()).getTime());

        return aPrescription;
    }

    public static List<Prescription> sortPrescriptionList(List<Prescription> prescriptionList) {
        prescriptionList.sort(new Comparator<Prescription>() {
            @Override
            public int compare(Prescription lhs, Prescription rhs) {
                if (lhs.getDateEndOfStock().compareTo(rhs.getDateEndOfStock()) != 0)
                    return lhs.getDateEndOfStock().compareTo(rhs.getDateEndOfStock());
                else
                    return (int) (lhs.getStock() - rhs.getStock());
            }
        });
        return prescriptionList;
    }
}
