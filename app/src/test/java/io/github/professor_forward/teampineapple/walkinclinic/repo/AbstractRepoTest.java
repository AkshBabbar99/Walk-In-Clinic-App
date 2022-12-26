package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;

import io.reactivex.schedulers.Schedulers;

abstract class AbstractRepoTest {
    private int clinicCounter = 1;
    private int userCounter = 1;

    LocalDb db;
    Repository repo;

    Repository repo() { return repo; }

    boolean useMainThreadQueries() {
        return false;
    }

    @Before
    public void createRepo() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserDb userDb;
        if (useMainThreadQueries()) {
            db = Room.inMemoryDatabaseBuilder(context, LocalDb.class).allowMainThreadQueries().build();
            userDb = Room.inMemoryDatabaseBuilder(context, UserDb.class).allowMainThreadQueries().build();
        }
        else {
            db = TestLocalDb.create(context);
            userDb = Room.inMemoryDatabaseBuilder(context, UserDb.class).allowMainThreadQueries().build();
        }
        repo = new Repository(db, userDb);
    }

    @After
    public void closeDb() {
        db.close();
    }

    Clinic makeClinic() {
        return makeClinic(1, 2, 3);
    }

    Clinic makeClinic(int numStaff, int numNurses, int numDoctors) {
        Clinic clinic = new Clinic("name" + clinicCounter, "address" + clinicCounter, "phonenumber" + clinicCounter, "insurance", "payment", numStaff, numNurses, numDoctors);

        db.clinics().insert(clinic).subscribeOn(Schedulers.io()).blockingAwait();
        clinic = db.clinics().getByName("name" + clinicCounter).subscribeOn(Schedulers.io()).blockingFirst()[0];
        // Trigger instance load
        clinic = repo.clinics().getById(clinic.id).blockingFirst().orNull();
        EmployeeRole employeeRole = makeEmployee();
        repo.employeeRoles().update(employeeRole, clinic.id).blockingAwait();

        clinicCounter++;
        return clinic;
    }

    private EmployeeRole makeEmployee() {
        repo.users().create("name" + userCounter, "email" + userCounter, "password", "employee").blockingAwait();
        User user = repo.users().getByEmail("email" + userCounter).data.blockingFirst();
        userCounter++;
        return (EmployeeRole) user.getRole().blockingFirst();
    }

    PatientRole makePatient() {
        repo.users().create("name" + userCounter, "email" + userCounter, "password", "patient").blockingAwait();
        User user = repo.users().getByEmail("email" + userCounter).data.blockingFirst();
        userCounter++;
        return (PatientRole) user.getRole().blockingFirst();
    }
}
