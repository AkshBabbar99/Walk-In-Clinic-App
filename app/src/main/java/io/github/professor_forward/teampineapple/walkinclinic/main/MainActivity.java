package io.github.professor_forward.teampineapple.walkinclinic.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import io.github.professor_forward.teampineapple.walkinclinic.R;

public class MainActivity extends AppCompatActivity {
    private NavigationDecorationPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_outer);
        presenter = new NavigationDecorationPresenter(
                this,
                findViewById(R.id.toolbar),
                findViewById(R.id.drawer_layout),
                findViewById(R.id.nav_view),
                findViewById(R.id.bottomNav),
                Navigation.findNavController(this, R.id.my_nav_host_fragment)
        );
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        presenter.updateOptionsMenu(getMenuInflater(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return presenter.onOption(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!presenter.onBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return presenter.onSupportUp() || super.onSupportNavigateUp();
    }
}
