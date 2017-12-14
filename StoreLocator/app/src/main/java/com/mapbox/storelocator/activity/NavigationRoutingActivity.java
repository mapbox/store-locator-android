package com.mapbox.storelocator.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewListener;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationUnitType;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.storelocator.R;

import java.util.List;

import static com.mapbox.storelocator.util.StringConstants.DESTINATION_LOCATION_LAT_KEY;
import static com.mapbox.storelocator.util.StringConstants.DESTINATION_LOCATION_LONG_KEY;
import static com.mapbox.storelocator.util.StringConstants.MOCK_DEVICE_LOCATION_LAT_KEY;
import static com.mapbox.storelocator.util.StringConstants.MOCK_DEVICE_LOCATION_LONG_KEY;

public class NavigationRoutingActivity extends AppCompatActivity implements NavigationViewListener,
  PermissionsListener {

  private NavigationView navigationView;
  private PermissionsManager permissionsManager;

  private double navOriginLat;
  private double navOriginLong;
  private double navDestinationLat;
  private double navDestinationLong;
  private boolean simulateRoute;

  private String TAG = "NavigationRoutingActivity";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getIntent().getExtras() != null) {
      navOriginLat = getIntent().getExtras().getDouble(MOCK_DEVICE_LOCATION_LAT_KEY);
      navOriginLong = getIntent().getExtras().getDouble(MOCK_DEVICE_LOCATION_LONG_KEY);
      navDestinationLat = getIntent().getExtras().getDouble(DESTINATION_LOCATION_LAT_KEY);
      navDestinationLong = getIntent().getExtras().getDouble(DESTINATION_LOCATION_LONG_KEY);
      // TODO - add back once fixed in the nav SDK
      //      simulateRoute = getIntent().getExtras().getBoolean(SIMULATE_NAV_ROUTE_KEY);

      Log.d(TAG, "onCreate: navOriginLat = " + navOriginLat);
      Log.d(TAG, "onCreate: navOriginLong = " + navOriginLong);
      Log.d(TAG, "onCreate: navDestinationLat = " + navDestinationLat);
      Log.d(TAG, "onCreate: navDestinationLong = " + navDestinationLong);
    }
    // Hide the status bar for the map to fill the entire screen
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    // Inflate the layout with the the NavigationView.
    setContentView(R.layout.activity_navigation_ui);
    navigationView = findViewById(R.id.navigationView);
    navigationView.onCreate(savedInstanceState);

    // Check for location permission
    permissionsManager = new PermissionsManager(this);
    if (!PermissionsManager.areLocationPermissionsGranted(this)) {
      permissionsManager.requestLocationPermissions(this);
    } else {
      navigationView.getNavigationAsync(this);
    }
  }

  @Override
  public void onNavigationReady() {
    Log.d(TAG, "onNavigationReady:");
    NavigationViewOptions options = NavigationViewOptions.builder()
      .origin(Point.fromLngLat(navOriginLong, navOriginLat))
      .destination(Point.fromLngLat(navDestinationLong, navDestinationLat))
      .unitType(NavigationUnitType.TYPE_IMPERIAL)
      .build();
    navigationView.startNavigation(options);
    Log.d(TAG, "onNavigationReady: starting navigation");
  }

  @Override
  public void onNavigationFinished() {
    finish();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    navigationView.onLowMemory();
  }

  @Override
  public void onBackPressed() {
    // If the navigation view didn't need to do anything, call super
    if (!navigationView.onBackPressed()) {
      super.onBackPressed();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    navigationView.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    navigationView.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  public void onExplanationNeeded(List<String> permissionsToExplain) {
    Toast.makeText(this, "This app needs location permissions in order to use navigation.",
      Toast.LENGTH_LONG).show();
  }

  @Override
  public void onPermissionResult(boolean granted) {
    if (granted) {
      navigationView.getNavigationAsync(this);
    } else {
      Toast.makeText(this, "You didn't grant location permissions.",
        Toast.LENGTH_LONG).show();
    }
  }
}
