package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

// Run on the last API to support JDK 1.8
// Don't use MyApplication since it sets up repos
@Config(sdk = 28, application = Application.class)
@RunWith(RobolectricTestRunner.class)
public class UserRepoTest {
    private LocalDb db;
    private UserDb userDb;
    private UserRepo repo;

    @Before
    public void createRepo() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = TestLocalDb.create(context);
        userDb = Room.inMemoryDatabaseBuilder(context, UserDb.class).allowMainThreadQueries().build();
        repo = new Repository(db, userDb).users();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void create() {
        repo.create("user", "user123@gmail.com", "pass", "patient").blockingAwait();
        User user = repo.getByEmail("user123@gmail.com").data.blockingFirst();
        assertEquals("user", user.username);
        assertEquals("user123@gmail.com", user.email);
        assertEquals(User.digestPassword("pass"), user.hashedPassword);
        assertEquals("patient", user.roleId);
        assertTrue(user.getRole().blockingFirst() instanceof PatientRole);
    }

    @Test
    public void delete() {
        repo.create("user", "user123@gmail.com", "pass", "patient").blockingAwait();
        User user = db.users().getByEmail("user123@gmail.com").blockingFirst();
        repo.delete(Arrays.asList(user)).blockingAwait();
        assertFalse(db.users().exists("user123@gmail.com").subscribeOn(Schedulers.io()).blockingGet());
    }

    @Test
    public void delete_employeeRoleIsDeleted() {
        repo.create(new User("user", "a@c", "pass", "employee")).blockingAwait();
        User user = db.users().getByEmail("a@c").blockingFirst();
        repo.delete(user).blockingAwait();
        assertFalse(db.employeeRoles().exists(user.id).subscribeOn(Schedulers.io()).blockingGet());
    }

    @Test
    public void delete_patientRoleIsDeleted() {
        repo.create(new User("user", "a@c", "pass", "patient")).blockingAwait();
        User user = db.users().getByEmail("a@c").blockingFirst();
        repo.delete(user).blockingAwait();
        assertFalse(db.patientRoles().exists(user.id).subscribeOn(Schedulers.io()).blockingGet());
    }

    @Test
    public void login() {
        repo.create("user", "user123@gmail.com", "pass", "employee").blockingAwait();
        User user = db.users().getByEmail("user123@gmail.com").blockingFirst();
        user = repo.login(user.email, "pass").blockingGet();
        assertEquals("user", user.username);
        assertEquals("user123@gmail.com", user.email);
        assertEquals(User.digestPassword("pass"), user.hashedPassword);
        assertEquals("employee", user.roleId);
        assertTrue(user.getRole().blockingFirst() instanceof EmployeeRole);
    }

    @Test
    public void loginAdmin() {
        User admin = repo.login("admin", "5T5ptQ").blockingGet();
        assertEquals("admin", admin.username);
        assertEquals(UserRepo.ADMIN_EMAIL, admin.email);
        assertEquals(User.digestPassword("5T5ptQ"), admin.hashedPassword);
        assertEquals("admin", admin.roleId);
        assertTrue(admin.getRole().blockingFirst() instanceof AdminRole);
    }

    @Test
    public void login_loadsEmployeeRole() {
        repo.create("employee", "a@d", "pass", EmployeeRole.ROLE_KEY).blockingAwait();
        User user = repo.login("a@d", "pass").blockingGet();
        UserRole role = user.getRole().timeout(1, TimeUnit.SECONDS).blockingFirst();
        assertTrue(role instanceof EmployeeRole);
        EmployeeRole employeeRole = (EmployeeRole) role;
        assertEquals(user.id, employeeRole.id);
    }

    @Test
    public void login_loadsPatientRole() {
        repo.create("patient", "a@d", "pass", PatientRole.ROLE_KEY).blockingAwait();
        User user = repo.login("a@d", "pass").blockingGet();
        UserRole role = user.getRole().timeout(1, TimeUnit.SECONDS).blockingFirst();
        assertTrue(role instanceof PatientRole);
        PatientRole patientRole = (PatientRole) role;
        assertEquals(user.id, patientRole.id);
    }

    @Test
    public void loginIncorrectPassword() {
        repo.create("user", "user123@gmail.com", "pass", "patient").blockingAwait();
        User user = repo.login("user123@gmail.com", "passwrong").blockingGet();
        assertNull(user);
    }

    @Test
    public void migrateUsersToLocalDb() {
        userDb.users().insert(new User("test admin", "admin@a4word.com", "5T5ptQ", "admin")).blockingAwait();
        userDb.users().insert(new User("test patient", "patient@example.com", User.digestPassword("pass"), "patient")).blockingAwait();
        userDb.users().insert(new User("test employee", "employee@example.com", User.digestPassword("pass"), "employee")).blockingAwait();
        repo = new Repository(db, userDb).users();

        User user = repo.login("admin", "5T5ptQ").blockingGet();
        assertEquals("admin", user.username);
        assertEquals("admin@a4word.com", user.email);
        assertEquals("admin", user.roleId);

        user = repo.login("patient@example.com", "pass").blockingGet();
        assertEquals("test patient", user.username);
        assertEquals("patient", user.roleId);

        user = repo.login("employee@example.com", "pass").blockingGet();
        assertEquals("test employee", user.username);
        assertEquals("employee", user.roleId);
        UserRole role = user.getRole().timeout(1, TimeUnit.SECONDS).blockingFirst();
        assertTrue(role instanceof EmployeeRole);
    }
}
