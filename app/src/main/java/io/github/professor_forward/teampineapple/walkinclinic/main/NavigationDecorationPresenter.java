package io.github.professor_forward.teampineapple.walkinclinic.main;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.FloatingWindow;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Method;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.NavigationDirections;
import io.github.professor_forward.teampineapple.walkinclinic.R;

class NavigationDecorationPresenter implements
        NavController.OnDestinationChangedListener,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener
{
    // Activity Context
    private final AppCompatActivity activity;
    private final Toolbar toolbar;
    private final DrawerLayout drawer;
    private final NavigationView navigationView;
    private final BottomNavigationView bottomNav;
    private final NavController navController;

    // Animation-related
    private final AppBarConfiguration appBarConfig;
    private final ActionBarDrawerToggle abdt;
    private final DrawerArrowDrawable navIcon;
    private ValueAnimator animator;

    // Destination-related
    private NavigationDecorations curDecorations;
    @MenuRes
    private int lastDrawerMenu;
    @MenuRes
    private int lastBottomMenu;
    @IdRes
    private int fallbackNavDest;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        try {
            // Adapted from NavigationUI.onNavDestinationSelected() to allow onDestinationChanged
            // Customization: Pop entire back stack because we remove the start destination and
            // our drawer only contains top level items
            navController.navigate(menuItem.getItemId(), null,
                    new NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setEnterAnim(R.anim.nav_default_enter_anim)
                            .setExitAnim(R.anim.nav_default_exit_anim)
                            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                            .setPopUpTo(fallbackNavDest != 0 ? fallbackNavDest :
                                    navController.getGraph().getId(), false)
                            .build()
            );
            drawer.closeDrawers();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void onDestinationChanged(
            @NonNull NavController controller, @NonNull NavDestination destination,
            @Nullable Bundle arguments
    ) {
        // Customized and fixed version of NavigationUI.setupActionBarWithNavController();
        if (destination instanceof FloatingWindow) {
            return;
        }
        toolbar.setVisibility(destination.getId() == R.id.splashScreen ? View.GONE : View.VISIBLE);
        toolbar.setTitle(destination.getLabel());
        Log.w("NavDestPres", "Destination Changed " + destination.getLabel());
        int destinationId = destination.getId();
        curDecorations = NavigationDecorations.forDestination(destinationId);
        final int drawerMenu = curDecorations.drawerMenu;
        final int bottomMenu = curDecorations.bottomMenu;
        stopIconAnimation();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        float startValue = navIcon.getProgress();
        if (lastDrawerMenu != drawerMenu) {
            navigationView.getMenu().clear();
        }
        if (lastBottomMenu != bottomMenu) {
            bottomNav.getMenu().clear();
        }
        // Customization: Add option for no nav icon at all
        if (drawerMenu != 0) {
            drawer.addDrawerListener(this);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // Fixed: Drawer and icon are now back in sync
            abdt.setDrawerIndicatorEnabled(true);
            animator = ObjectAnimator.ofFloat(navIcon, "progress",
                    startValue, 0.0f);
            if (drawerMenu != lastDrawerMenu) {
                navigationView.inflateMenu(drawerMenu);
            }
        } else if (isBackAvailable() && bottomMenu == 0) {
            abdt.setHomeAsUpIndicator(navIcon);
            abdt.setDrawerIndicatorEnabled(false);
            animator = ObjectAnimator.ofFloat(navIcon, "progress",
                    startValue, 1.0f);
        } else {
            abdt.setHomeAsUpIndicator(null);
            abdt.setDrawerIndicatorEnabled(false);
            navIcon.setProgress(0.5f);
        }
        lastDrawerMenu = drawerMenu;
        if (animator != null) {
            animator.start();
        }
        if (bottomMenu != 0) {
            bottomNav.setVisibility(View.VISIBLE);
            if (bottomMenu != lastBottomMenu) {
                bottomNav.inflateMenu(bottomMenu);
            }
        } else {
            bottomNav.setVisibility(View.GONE);
        }
        lastBottomMenu = bottomMenu;
        activity.invalidateOptionsMenu();
        // Duplicated from NavigationUI.setupWithNavController()
        // to allow onNavigationItemSelected() to be customized
        Menu drawerMenuObj = navigationView.getMenu();
        for (int i = drawerMenuObj.size() - 1; i >= 0; i--) {
            MenuItem item = drawerMenuObj.getItem(i);
            item.setChecked(destinationId == item.getItemId());
        }
        Menu bottomMenuObj = bottomNav.getMenu();
        for (int i = bottomMenuObj.size() - 1; i >= 0; i--) {
            MenuItem item = bottomMenuObj.getItem(i);
            item.setChecked(destinationId == item.getItemId());
        }
        // Drawer is considered closer to the root than the bottom nav
        if (drawerMenuObj.size() != 0) {
            fallbackNavDest = drawerMenuObj.getItem(0).getItemId();
        } else if (bottomMenuObj.size() != 0) {
            fallbackNavDest = bottomMenuObj.getItem(0).getItemId();
        } else {
            fallbackNavDest = 0;
        }
        drawer.clearFocus();
        InputMethodManager imm = (InputMethodManager)
                MyApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(drawer.getWindowToken(), 0);
        }
    }

    private boolean isBackAvailable() {
        try {
            Method met = NavController.class.getDeclaredMethod("getDestinationCountOnBackStack");
            met.setAccessible(true);
            Integer count = (Integer) met.invoke(navController);
            //noinspection ConstantConditions
            return count > 1;
        } catch (Exception e) {
            throw new RuntimeException("Failed to determine if back button should be shown", e);
        }
    }

    private void stopIconAnimation() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        drawer.removeDrawerListener(this);
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        // Don't interfere with ActionBarDrawerToggle
        stopIconAnimation();
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {}

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {}

    @Override
    public void onDrawerStateChanged(int newState) {}

    @SuppressLint("PrivateResource")
    NavigationDecorationPresenter(
            @NonNull AppCompatActivity activity, @NonNull Toolbar toolbar,
            @NonNull DrawerLayout drawer, @NonNull NavigationView navigationView,
            @NonNull BottomNavigationView bottomNav,
            @NonNull NavController navController
    ) {
        this.activity = activity;
        this.toolbar = toolbar;
        this.drawer = drawer;
        this.navigationView = navigationView;
        this.bottomNav = bottomNav;
        this.navController = navController;
        appBarConfig = new AppBarConfiguration.Builder(navController.getGraph())
                .setDrawerLayout(drawer)
                .build();
        activity.setSupportActionBar(toolbar);
        abdt = new ActionBarDrawerToggle(
                activity, drawer, toolbar,
                R.string.drawer_open, R.string.drawer_close
        );
        drawer.addDrawerListener(abdt);
        abdt.syncState();
        // Restore back button behavior overwritten by ActionBarDrawerToggle
        abdt.setToolbarNavigationClickListener(v -> activity.onSupportNavigateUp());
        navIcon = abdt.getDrawerArrowDrawable();
        navController.addOnDestinationChangedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    void updateOptionsMenu(@NonNull MenuInflater menuInflater, @NonNull Menu menu) {
        menu.clear();
        int destinationMenu = curDecorations.optionsMenu;
        if (destinationMenu != 0) {
            menuInflater.inflate(destinationMenu, menu);
        }
    }

    boolean onOption(@IdRes int itemId) {
        if (itemId == R.id.action_logout) {
            navController.navigate(NavigationDirections.doLogout());
            return true;
        }
        return false;
    }

    boolean onBack() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return true;
        }
        return false;
    }

    boolean onSupportUp() {
        return NavigationUI.navigateUp(navController, appBarConfig);
    }
}
