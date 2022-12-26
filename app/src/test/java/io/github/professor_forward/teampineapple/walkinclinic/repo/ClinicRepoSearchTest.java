package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.paging.LimitOffsetDataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.Time;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

// Run on the last API to support JDK 1.8
// Don't use MyApplication since it sets up repos
@Config(sdk = 28, application = Application.class)
@RunWith(RobolectricTestRunner.class)
/**
 * This class tests only the search methods on the ClinicRepo. Unfortunately, Robolectric's test runner
 * currently pauses threads in such a way that it makes testing the RxPagedListBuilder (which tries
 * to load results on a background thread) either extremely difficult or impossible (see
 * https://github.com/robolectric/robolectric/issues/2977).
 *
 * Instead, we use internal methods to get the same DataSource the ClinicRepo uses, and test this on the main thread.
 *
 * Also, since we need to do main thread queries, we need to use a different db instance than the one used by ClinicRepoTest,
 * which need to test that repo methods run all real db queries on Schedulers.io().
 */
public class ClinicRepoSearchTest extends AbstractRepoTest {
    private int clinicCounter = 1;

    @Override
    protected boolean useMainThreadQueries() {
        return true;
    }

    @Test
    public void search_basic() {
        makeClinic();

        List<Clinic> clinics = search("", "", "");

        assertEquals(1, clinics.size());
    }

    @Test
    public void search_basic_withTimeBeginning() {
        makeClinic();

        List<Clinic> clinics = search("", "", "", new Time(8, 0, 0));

        assertEquals(1, clinics.size());
    }

    @Test
    public void search_basic_withTimeEnding() {
        makeClinic();

        List<Clinic> clinics = search("", "", "", new Time(17, 0, 0));

        assertEquals(1, clinics.size());
    }

    @Test
    public void search_basic_withTimeTen() {
        makeClinic();

        List<Clinic> clinics = search("", "", "", new Time(10, 0, 0));

        assertEquals(1, clinics.size());
    }

    @Test
    public void search_basic_withTimeTooEarly() {
        makeClinic();

        List<Clinic> clinics = search("", "", "", new Time(7, 59, 0));

        assertEquals(0, clinics.size());
    }

    @Test
    public void search_basic_withTimeTooLate() {
        makeClinic();

        List<Clinic> clinics = search("", "", "", new Time(17, 1, 0));

        assertEquals(0, clinics.size());
    }

    @Test
    public void search_byName() {
        makeClinic();
        Clinic clinic2 = makeClinic();

        List<Clinic> clinics = search("e2", "", "");

        assertEquals(1, clinics.size());
        assertEquals(clinic2.id, clinics.get(0).id);
    }

    @Test
    public void search_byName_withTime() {
        makeClinic();
        Clinic clinic2 = makeClinic();

        List<Clinic> clinics = search("e2", "", "", new Time(8, 0, 0));

        assertEquals(1, clinics.size());
        assertEquals(clinic2.id, clinics.get(0).id);
    }

    @Test
    public void search_byAddress() {
        Clinic clinic1 = makeClinic();
        makeClinic();

        List<Clinic> clinics = search("", "ss1", "");

        assertEquals(1, clinics.size());
        assertEquals(clinic1.id, clinics.get(0).id);
    }

    @Test
    public void search_byAddress_withTime() {
        Clinic clinic1 = makeClinic();
        makeClinic();

        List<Clinic> clinics = search("", "ss1", "", new Time(8, 0, 0));

        assertEquals(1, clinics.size());
        assertEquals(clinic1.id, clinics.get(0).id);
    }

    @Test
    public void search_byService() {
        makeClinic();
        makeClinic();
        Clinic clinic3 = makeClinic();

        List<Clinic> clinics = search("", "", "ervice3");

        assertEquals(1, clinics.size());
        assertEquals(clinic3.id, clinics.get(0).id);
    }

    @Test
    public void search_byService_withTime() {
        makeClinic();
        makeClinic();
        Clinic clinic3 = makeClinic();

        List<Clinic> clinics = search("", "", "ervice3", new Time(8, 0, 0));

        assertEquals(1, clinics.size());
        assertEquals(clinic3.id, clinics.get(0).id);
    }

    protected Clinic makeClinic() {
        Clinic clinic = super.makeClinic();

        repo.clinics().addHours(clinic, DayOfWeek.MONDAY, new Time(8, 0, 0), new Time(17, 0, 0)).blockingAwait();

        repo.clinicServices().create("service" + clinicCounter, ClinicEmployeeRole.DOCTOR).blockingAwait();
        ClinicService clinicService = db.clinicServices().getByName("service" + clinicCounter).subscribeOn(Schedulers.io()).blockingFirst();
        repo.clinics().addService(clinic, clinicService).blockingAwait();

        clinicCounter++;
        return clinic;
    }

    private List<Clinic> search(@NonNull String name, @NonNull String address, @NonNull String service) {
        return ((LimitOffsetDataSource<Clinic>) repo().clinics().buildSearchDataSource(name, address, service).create()).loadRange(0, Integer.MAX_VALUE);
    }

    public List<Clinic> search(@NonNull String name, @NonNull String address, @NonNull String service, @NonNull Time time) {
        return ((LimitOffsetDataSource<Clinic>) repo().clinics().buildSearchDataSource(name, address, service, time).create()).loadRange(0, Integer.MAX_VALUE);
    }
}
