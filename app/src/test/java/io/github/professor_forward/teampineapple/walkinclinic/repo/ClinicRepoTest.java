package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.app.Application;

import com.google.common.base.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Run on the last API to support JDK 1.8
// Don't use MyApplication since it sets up repos
@Config(sdk = 28, application = Application.class)
@RunWith(RobolectricTestRunner.class)
public class ClinicRepoTest extends AbstractRepoTest {

    @Test
    public void create() {
        EmployeeRole employee = new EmployeeRole("foo");
        repo.employeeRoles().create(employee).blockingAwait();
        repo.clinics().create(employee, "name2", "address2", "phonenumber2", "insurance2", "payment2", 2, 3, 4).blockingAwait();
        assertTrue(db.clinics().existsByName("name2").subscribeOn(Schedulers.io()).blockingGet());
        Clinic clinic = db.clinics().getByName("name2").blockingFirst()[0];
        employee = repo.employeeRoles().getByClinicId(clinic.id).blockingFirst().get(0);
        assertEquals("foo", employee.id);
        clinic = employee.getClinic().filter(Optional::isPresent).map(Optional::get).blockingFirst();
        assertNotNull(clinic.id);
        assertEquals("name2", clinic.name);
        assertEquals("address2", clinic.address);
        assertEquals("phonenumber2", clinic.phoneNumber);
        assertEquals("insurance2", clinic.acceptedInsuranceTypes);
        assertEquals("payment2", clinic.acceptedPaymentTypes);
        assertEquals(2, clinic.numStaff);
        assertEquals(3, clinic.numNurses);
        assertEquals(4, clinic.numDoctors);
    }

    @Test
    public void update() {
        Clinic clinic = makeClinic();
        repo.clinics().update(clinic, "name2", "address2", "phonenumber2", "insurance2", "payment2", 2, 3, 4).blockingAwait();
        clinic = db.clinics().getById(clinic.id).blockingFirst()[0];
        assertEquals("name2", clinic.name);
        assertEquals("address2", clinic.address);
        assertEquals("phonenumber2", clinic.phoneNumber);
        assertEquals("insurance2", clinic.acceptedInsuranceTypes);
        assertEquals("payment2", clinic.acceptedPaymentTypes);
        assertEquals(2, clinic.numStaff);
        assertEquals(3, clinic.numNurses);
        assertEquals(4, clinic.numDoctors);
    }

    @Test
    public void addServicetoClinic() {
        Clinic clinic = makeClinic();
        repo.clinicServices().create("test service", ClinicEmployeeRole.DOCTOR).blockingAwait();
        ClinicService service = db.clinicServices().getByName("test service").blockingFirst();
        repo.clinics().addService(clinic, service).blockingAwait();
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();
        List<ClinicService> services = clinic.getServices().blockingFirst();
        assertEquals(1, services.size());
        assertEquals("test service", services.get(0).name);
        assertEquals(ClinicEmployeeRole.DOCTOR, services.get(0).role);
    }

    @Test
    public void addServicestoClinic() {
        Clinic clinic = makeClinic();
        repo.clinicServices().create("test service", ClinicEmployeeRole.DOCTOR).blockingAwait();
        repo.clinicServices().create("test service 2", ClinicEmployeeRole.NURSE).blockingAwait();
        List<ClinicService> services = new ArrayList<>();
        services.add(db.clinicServices().getByName("test service").blockingFirst());
        services.add(db.clinicServices().getByName("test service 2").blockingFirst());
        repo.clinics().addServices(clinic, services).blockingAwait();
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();
        services = clinic.getServices().blockingFirst();
        assertEquals(2, services.size());
        assertEquals("test service", services.get(0).name);
        assertEquals(ClinicEmployeeRole.DOCTOR, services.get(0).role);
        assertEquals("test service 2", services.get(1).name);
        assertEquals(ClinicEmployeeRole.NURSE, services.get(1).role);
    }

    public void addServicestoClinic_doesntAffectClinic2() {
        Clinic clinic = makeClinic();
        Clinic clinic2 = makeClinic();
        repo.clinicServices().create("test service", ClinicEmployeeRole.DOCTOR).blockingAwait();
        repo.clinicServices().create("test service 2", ClinicEmployeeRole.NURSE).blockingAwait();
        List<ClinicService> services = new ArrayList<>();

        services.add(db.clinicServices().getByName("test service").blockingFirst());
        services.add(db.clinicServices().getByName("test service 2").blockingFirst());
        repo.clinics().addServices(clinic, services).blockingAwait();
        services = clinic2.getServices().blockingFirst();
        assertEquals(0, services.size());
    }

    @Test
    public void removeServicesFromClinic() {
        Clinic clinic = makeClinic();
        repo.clinicServices().create("test service", ClinicEmployeeRole.DOCTOR).blockingAwait();
        repo.clinicServices().create("test service 2", ClinicEmployeeRole.NURSE).blockingAwait();
        List<ClinicService> services = new ArrayList<>();
        services.add(db.clinicServices().getByName("test service").blockingFirst());
        services.add(db.clinicServices().getByName("test service 2").blockingFirst());
        repo.clinics().addServices(clinic, services).blockingAwait();
        repo.clinics().deleteServices(clinic, services).blockingAwait();
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();
        services = clinic.getServices().blockingFirst();
        assertEquals(0, services.size());
    }

    @Test
    public void removeServicesFromClinic_doesntAffectClinic2() {
        Clinic clinic = makeClinic();
        Clinic clinic2 = makeClinic();
        repo.clinicServices().create("test service", ClinicEmployeeRole.DOCTOR).blockingAwait();
        repo.clinicServices().create("test service 2", ClinicEmployeeRole.NURSE).blockingAwait();

        repo.clinics().addService(clinic, db.clinicServices().getByName("test service").blockingFirst()).blockingAwait();
        repo.clinics().addService(clinic2, db.clinicServices().getByName("test service 2").blockingFirst()).blockingAwait();
        List<ClinicService> services = new ArrayList<>();
        services.add(db.clinicServices().getByName("test service").blockingFirst());
        repo.clinics().deleteServices(clinic, services).blockingAwait();
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();

        services = clinic.getServices().blockingFirst();
        assertEquals(0, services.size());
        services = clinic2.getServices().blockingFirst();
        assertEquals(1, services.size());
        assertEquals("test service 2", services.get(0).name);
    }

    @Test
    public void removeService_fromClinic() {
        Clinic clinic = makeClinic();
        repo.clinicServices().create("test service", ClinicEmployeeRole.DOCTOR).blockingAwait();
        ClinicService service = db.clinicServices().getByName("test service").blockingFirst();
        repo.clinics().addService(clinic, service).blockingAwait();
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();
        List<ClinicService> services = clinic.getServices().blockingFirst();
        repo.clinics().deleteService(clinic, services.get(0)).blockingAwait();
        services = clinic.getServices().blockingFirst();
        assertEquals(0, services.size());
    }

    @Test
    public void deleteService_globallyRemovesItFromClinic() {
        Clinic clinic = makeClinic();
        repo.clinicServices().create("test service", ClinicEmployeeRole.DOCTOR).blockingAwait();
        ClinicService service = db.clinicServices().getByName("test service").blockingFirst();
        repo.clinics().addService(clinic, service).blockingAwait();
        repo.clinicServices().delete(service).blockingAwait();
        List<ClinicService> services = clinic.getServices().blockingFirst();
        assertEquals(0, services.size());
    }

    @Test
    public void addHours() {
        Clinic clinic = makeClinic();
        repo.clinics().addHours(clinic, DayOfWeek.SATURDAY, new Time(10, 0, 0), new Time(17, 30, 0)).blockingAwait();
        List<ClinicHours> hours = db.clinicHours().getByClinicId(clinic.id).blockingFirst();

        assertEquals(1, hours.size());
        assertEquals(clinic.id, hours.get(0).clinicId);
        assertEquals(DayOfWeek.SATURDAY, hours.get(0).dayOfWeek);
        assertEquals(10, hours.get(0).startTime.getHours());
        assertEquals(0, hours.get(0).startTime.getMinutes());
        assertEquals(17, hours.get(0).endTime.getHours());
        assertEquals(30, hours.get(0).endTime.getMinutes());
    }

    @Test
    public void updateHours() {
        Clinic clinic = makeClinic();
        repo.clinics().addHours(clinic, DayOfWeek.SATURDAY, new Time(9, 30, 0), new Time(17, 0, 0)).blockingAwait();
        ClinicHours hours = db.clinicHours().getByClinicId(clinic.id).blockingFirst().get(0);
        repo.clinics().updateHours(hours, DayOfWeek.MONDAY, new Time(10, 0, 0), new Time(17, 30, 0)).blockingAwait();
        hours = clinic.getHours().blockingFirst().get(0);

        assertEquals(clinic.id, hours.clinicId);
        assertEquals(DayOfWeek.MONDAY, hours.dayOfWeek);
        assertEquals(10, hours.startTime.getHours());
        assertEquals(0, hours.startTime.getMinutes());
        assertEquals(17, hours.endTime.getHours());
        assertEquals(30, hours.endTime.getMinutes());
    }

    @Test
    public void deleteHours() {
        Clinic clinic = makeClinic();
        repo.clinics().addHours(clinic, DayOfWeek.MONDAY, new Time(1, 2, 3), new Time(1, 2, 3)).blockingAwait();
        ClinicHours hours = db.clinicHours().getByClinicId(clinic.id).blockingFirst().get(0);
        repo.clinics().deleteHours(hours).blockingAwait();

        List<ClinicHours> hoursList = clinic.getHours().blockingFirst();
        assertEquals(0, hoursList.size());

    }

    @Test
    public void getById() {
        Clinic clinic = makeClinic();
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();
        assertEquals("name1", clinic.name);
        assertEquals("address1", clinic.address);
        assertEquals("phonenumber1", clinic.phoneNumber);
        assertEquals("insurance", clinic.acceptedInsuranceTypes);
        assertEquals("payment", clinic.acceptedPaymentTypes);
        assertEquals(1, clinic.numStaff);
        assertEquals(2, clinic.numNurses);
        assertEquals(3, clinic.numDoctors);
    }

    @Test
    public void loadRelations_hours() {
        Clinic clinic = makeClinic();
        repo.clinics().addHours(clinic, DayOfWeek.SATURDAY, new Time(10, 0, 0), new Time(17, 30, 0)).blockingAwait();
        List<ClinicHours> hours = clinic.getHours().blockingFirst();
        assertEquals(1, hours.size());
        assertEquals(clinic, hours.get(0).getClinic().blockingFirst());
        assertEquals(clinic.id, hours.get(0).clinicId);
        assertEquals(DayOfWeek.SATURDAY, hours.get(0).dayOfWeek);
        assertEquals(10, hours.get(0).startTime.getHours());
        assertEquals(0, hours.get(0).startTime.getMinutes());
        assertEquals(17, hours.get(0).endTime.getHours());
        assertEquals(30, hours.get(0).endTime.getMinutes());
    }

    @Test
    public void loadRelations_services() {
        Clinic clinic = makeClinic();
        repo.clinicServices().create("foo", ClinicEmployeeRole.NURSE).blockingAwait();
        ClinicService foo = db.clinicServices().getByName("foo").blockingFirst();
        repo.clinics().addService(clinic, foo).blockingAwait();

        List<ClinicService> services = clinic.getServices().blockingFirst();
        assertEquals(1, services.size());
        assertEquals("foo", services.get(0).name);
    }

    @Test
    public void loadRelations_employees() {
        Clinic clinic = makeClinic();
        List<EmployeeRole> employeeRoles = clinic.getEmployees().blockingFirst();

        assertEquals(1, employeeRoles.size());
        assertNotNull(employeeRoles.get(0).id);
    }

    @Test
    public void getWaitTimeForDay_noBookings() {
        Clinic clinic = makeClinic();
        int time = repo.clinics().getWaitTimeForDate(clinic.id, Calendar.getInstance().getTime()).blockingFirst();
        assertEquals(0, time);
    }

    @Test
    public void getWaitTimeForDay_noBookingsThisDay() {
        Clinic clinic = makeClinic();
        Date today = Calendar.getInstance().getTime();
        today.setDate(19);
        Date tomorrow = Calendar.getInstance().getTime();
        tomorrow.setDate(20);
        addBookings(clinic, today, 20);
        int time = repo.clinics().getWaitTimeForDate(clinic.id, tomorrow).blockingFirst();
        assertEquals(0, time);
    }

    @Test
    public void getWaitTimeForDay_noStaff() {
        Clinic clinic = makeClinic(0, 0, 0);
        Date today = Calendar.getInstance().getTime();
        addBookings(clinic, today, 4);
        int time = repo.clinics().getWaitTimeForDate(clinic.id, today).blockingFirst();
        assertEquals(60, time);
    }

    @Test
    public void getWaitTimeForDay_twoBookingsTwoDoctors() {
        Clinic clinic = makeClinic(0, 0, 2);
        Date today = Calendar.getInstance().getTime();
        addBookings(clinic, today, 2);
        int time = repo.clinics().getWaitTimeForDate(clinic.id, today).blockingFirst();
        assertEquals(15, time);
    }

    @Test
    public void getWaitTimeForDay_twoBookingsTwoNurses() {
        Clinic clinic = makeClinic(0, 2, 0);
        Date today = Calendar.getInstance().getTime();
        addBookings(clinic, today, 2);
        int time = repo.clinics().getWaitTimeForDate(clinic.id, today).blockingFirst();
        assertEquals(15, time);
    }

    @Test
    public void getWaitTimeForDay_fourBookingsTwoNursesAndDoctors() {
        Clinic clinic = makeClinic(0, 2, 2);
        Date today = Calendar.getInstance().getTime();
        addBookings(clinic, today, 4);
        int time = repo.clinics().getWaitTimeForDate(clinic.id, today).blockingFirst();
        assertEquals(15, time);
    }

    @Test
    public void getWaitTimeForDay_fiveBookingsTwoNursesAndDoctors() {
        Clinic clinic = makeClinic(0, 2, 2);
        Date today = Calendar.getInstance().getTime();
        addBookings(clinic, today, 5);
        int time = repo.clinics().getWaitTimeForDate(clinic.id, today).blockingFirst();
        assertEquals(30, time);
    }

    @Test
    public void getWaitTimeForDay_eightBookingsTwoNursesAndDoctors() {
        Clinic clinic = makeClinic(0, 2, 2);
        Date today = Calendar.getInstance().getTime();
        addBookings(clinic, today, 8);
        int time = repo.clinics().getWaitTimeForDate(clinic.id, today).blockingFirst();
        assertEquals(30, time);
    }

    private void addBookings(Clinic clinic, Date date, int i) {
        while (i-- > 0) {
            PatientRole patientRole = makePatient();
            repo.bookings().create(clinic, patientRole, date).blockingAwait();
        }
    }
}