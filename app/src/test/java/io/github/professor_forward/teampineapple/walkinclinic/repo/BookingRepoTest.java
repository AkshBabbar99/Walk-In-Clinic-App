package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

// Run on the last API to support JDK 1.8
// Don't use MyApplication since it sets up repos
@Config(sdk = 28, application = Application.class)
@RunWith(RobolectricTestRunner.class)
public class BookingRepoTest extends AbstractRepoTest {
    @Test
    public void createAndLoadRelations() {
        Clinic clinic = makeClinic();
        PatientRole patient = makePatient();

        Date date = Calendar.getInstance().getTime();
        repo.bookings().create(clinic, patient, date).blockingAwait();

        // relation on clinics
        List<Booking> bookings = clinic.getBookings().blockingFirst();
        assertEquals(1, bookings.size());
        assertEquals(BookingState.PENDING, bookings.get(0).state);

        Clinic associatedClinic = bookings.get(0).getClinic().blockingFirst();
        PatientRole associatedPatient = bookings.get(0).getPatient().blockingFirst();
        assertEquals(clinic.id, associatedClinic.id);
        assertEquals(patient.id, associatedPatient.id);

        // relation on patients
        bookings = patient.getBookings().blockingFirst();
        assertEquals(1, bookings.size());
        assertEquals(BookingState.PENDING, bookings.get(0).state);
        associatedClinic = bookings.get(0).getClinic().blockingFirst();
        associatedPatient = bookings.get(0).getPatient().blockingFirst();
        assertEquals(clinic.id, associatedClinic.id);
        assertEquals(patient.id, associatedPatient.id);

        Date actualDate = bookings.get(0).date;
        assertEquals(date.getYear(), actualDate.getYear());
        assertEquals(date.getMonth(), actualDate.getMonth());
        assertEquals(date.getDay(), actualDate.getDay());

        actualDate = bookings.get(0).createdAt;
        assertEquals(date.getYear(), actualDate.getYear());
        assertEquals(date.getMonth(), actualDate.getMonth());
        assertEquals(date.getDay(), actualDate.getDay());
        assertEquals(date.getHours(), actualDate.getHours());
    }

}
