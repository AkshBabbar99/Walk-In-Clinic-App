package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

// Run on the last API to support JDK 1.8
// Don't use MyApplication since it sets up repos
@Config(sdk = 28, application = Application.class)
@RunWith(RobolectricTestRunner.class)
public class RatingTest extends AbstractRepoTest {
    @Test
    public void defaultRating() {
        assertEquals(0, makeClinic().rating);
    }

    @Test
    public void addOneRating() {
        Clinic clinic = makeClinic();
        PatientRole patient = makePatient();

        repo.ratings().create(clinic, patient, "amazing", 5).blockingAwait();

        // Reload
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();

        // Updated clinic rating
        assertEquals(5, clinic.rating);

        // Clinic relations
        List<Rating> ratings = clinic.getRatings().blockingFirst();
        assertEquals(1, ratings.size());
        assertEquals("amazing", ratings.get(0).comment);
        assertEquals(5, ratings.get(0).value);

        // Associations on ratings
        Clinic associatedClinic = ratings.get(0).getClinic().blockingFirst();
        PatientRole associatedPatient = ratings.get(0).getPatient().blockingFirst();
        assertEquals(clinic.id, associatedClinic.id);
        assertEquals(patient.id, associatedPatient.id);

        // Patient relations
        ratings = patient.getRatings().blockingFirst();
        assertEquals(1, ratings.size());
        assertEquals("amazing", ratings.get(0).comment);
        assertEquals(5, ratings.get(0).value);

        // Associations on ratings
        associatedClinic = ratings.get(0).getClinic().blockingFirst();
        associatedPatient = ratings.get(0).getPatient().blockingFirst();
        assertEquals(clinic.id, associatedClinic.id);
        assertEquals(patient.id, associatedPatient.id);
    }

    @Test
    public void ratingCalculation() {
        Clinic clinic = makeClinic();
        PatientRole patient = makePatient();

        for (int i = 1; i < 6; i++) {
            repo.ratings().create(clinic, patient, "", i).blockingAwait();
        }

        // Reload
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();

        assertEquals(3, clinic.rating);
    }

    @Test
    public void userDeletion() {
        Clinic clinic = makeClinic();
        PatientRole patient = makePatient();

        for (int i = 1; i < 6; i++) {
            repo.ratings().create(clinic, patient, "", i).blockingAwait();
        }

        List<PatientRole> patientRoles = Arrays.asList(new PatientRole[]{patient});
        repo.patientRoles().delete(patientRoles).blockingAwait();

        // Reload
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();
        List<Rating> ratings = clinic.getRatings().blockingFirst();

        assertEquals(0, clinic.rating);
        assertEquals(0, ratings.size());

        patient = makePatient();
        repo.ratings().create(clinic, patient, "", 1).blockingAwait();
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();
        assertEquals(1, clinic.rating);
    }
}
