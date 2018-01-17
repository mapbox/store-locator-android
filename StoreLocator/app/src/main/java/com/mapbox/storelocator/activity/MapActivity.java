package com.mapbox.storelocator.activity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbox.androidsdk.plugins.building.BuildingPlugin;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.api.directions.v5.DirectionsCriteria;
import com.mapbox.services.api.directions.v5.MapboxDirections;
import com.mapbox.services.api.directions.v5.models.DirectionsResponse;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.api.utils.turf.TurfHelpers;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.mapbox.storelocator.R;
import com.mapbox.storelocator.adapter.LocationRecyclerViewAdapter;
import com.mapbox.storelocator.model.IndividualLocation;
import com.mapbox.storelocator.util.LinearLayoutManagerWithSmoothScroller;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.services.Constants.PRECISION_6;
import static com.mapbox.storelocator.util.StringConstants.SELECTED_THEME_INTENT_KEY;

/**
 * Activity with a Mapbox map and recyclerview to view various locations
 */
public class MapActivity extends AppCompatActivity implements LocationRecyclerViewAdapter.CardClickListener,
  LocationRecyclerViewAdapter.StartNavClickListener, LocationRecyclerViewAdapter.WalkingRouteButtonClickListener,
  LocationRecyclerViewAdapter.BikingRouteButtonClickListener, LocationRecyclerViewAdapter.DrivingRouteButtonClickListener,
  PermissionsListener {


  private static final LatLngBounds LOCKED_MAP_CAMERA_BOUNDS = new LatLngBounds.Builder()
    .include(new LatLng(40.87096725853152, -74.08277394720501))
    .include(new LatLng(40.67035340371385,
      -73.87063900287112)).build();
  private static final LatLng MOCK_DEVICE_LOCATION_LAT_LNG = new LatLng(40.713469, -74.006735);
  private static final int MAPBOX_LOGO_OPACITY = 75;
  private static final int CAMERA_MOVEMENT_SPEED_IN_MILSECS = 1200;
  private static final float NAVIGATION_LINE_WIDTH = 9;
  private PermissionsManager permissionsManager;
  private DirectionsRoute currentRoute;
  private FeatureCollection featureCollection;
  private MapboxMap mapboxMap;
  private MapView mapView;
  private MapboxDirections directionsApiClient;
  private RecyclerView locationsRecyclerView;
  private ArrayList<IndividualLocation> listOfIndividualLocations;
  private CustomThemeManager customThemeManager;
  private LocationRecyclerViewAdapter styleRvAdapter;
  private int selectedTheme;
  private String currentDesiredRouteProfile;
  private String TAG = "MapActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Configure the Mapbox access token. Configuration can either be called in your application
    // class or in the same activity which contains the mapview.
    Mapbox.getInstance(this, getString(R.string.access_token));

    // Hide the status bar for the map to fill the entire screen
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    // Inflate the layout with the the MapView. Always inflate this after the Mapbox access token is configured.
    setContentView(R.layout.activity_map);

    // Create a GeoJSON feature collection from the GeoJSON file in the assets folder.
    try {
      getFeatureCollectionFromJson();
    } catch (Exception exception) {
      Log.e("MapActivity", "onCreate: " + exception);
      Toast.makeText(this, R.string.failure_to_load_file, Toast.LENGTH_LONG).show();
    }

    // Initialize a list of IndividualLocation objects for future use with recyclerview
    listOfIndividualLocations = new ArrayList<>();

    // Initialize the theme that was selected in the previous activity. The blue theme is set as the backup default.
    selectedTheme = getIntent().getIntExtra(SELECTED_THEME_INTENT_KEY, R.style.AppTheme_Blue);
    Log.d(TAG, "onCreate: selectedTheme = " + selectedTheme);

    // TODO: Add comment about what the line below is about
    currentDesiredRouteProfile = DirectionsCriteria.PROFILE_DRIVING;

    // Set up the Mapbox map
    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(final MapboxMap mapboxMap) {

        // Setting the returned mapboxMap object (directly above) equal to the "globally declared" one
        MapActivity.this.mapboxMap = mapboxMap;

        // Initialize the custom class that handles marker icon creation and map styling based on the selected theme
        customThemeManager = new CustomThemeManager(selectedTheme, MapActivity.this, mapView, mapboxMap);
        customThemeManager.initializeTheme();

        // Adjust the opacity of the Mapbox logo in the lower left hand corner of the map
        ImageView logo = mapView.findViewById(R.id.logoView);
        logo.setImageAlpha(MAPBOX_LOGO_OPACITY);

        // Set bounds for the map camera so that the user can't pan the map outside of the NYC area
        mapboxMap.setLatLngBoundsForCameraTarget(LOCKED_MAP_CAMERA_BOUNDS);

        // Create a list of features from the feature collection
        List<Feature> featureList = featureCollection.getFeatures();

        // Loop through the locations to add markers to the map
        for (int x = 0; x < featureList.size(); x++) {

          Feature singleLocation = featureList.get(x);

          // Get the single location's String properties to place in its map marker
          String singleLocationName = singleLocation.getStringProperty("name");
          String singleLocationHours = singleLocation.getStringProperty("hours");
          String singleLocationDescription = singleLocation.getStringProperty("description");
          String singleLocationPhoneNum = singleLocation.getStringProperty("phone");

          // Get the single location's LatLng coordinates
          Position singleLocationPosition = (Position) singleLocation.getGeometry().getCoordinates();

          // Create a new LatLng object with the Position object created above
          LatLng singleLocationLatLng = new LatLng(singleLocationPosition.getLatitude(),
            singleLocationPosition.getLongitude());

          // Add the location to the Arraylist of locations for later use in the recyclerview
          listOfIndividualLocations.add(new IndividualLocation(
            singleLocationName,
            singleLocationDescription,
            singleLocationHours,
            singleLocationPhoneNum,
            singleLocationLatLng
          ));

          // Add the location's marker to the map
          mapboxMap.addMarker(new MarkerOptions()
            .position(singleLocationLatLng)
            .title(singleLocationName)
            .icon(customThemeManager.getUnselectedMarkerIcon()));

          // Call getInformationFromDirectionsApi() to eventually display the location's
          // distance from mocked device location
          getInformationFromDirectionsApi(singleLocationLatLng, false, x);
        }

        // Add the fake device location marker to the map. In a real use case scenario, the Mapbox location layer plugin
        // can be used to easily display the device's location
        addMockDeviceLocationMarkerToMap();

        initMarkerClickListener();

        setUpRecyclerViewOfLocationCards(selectedTheme);
      }
    });

    // Check for location permission
    permissionsManager = new PermissionsManager(this);
    if (!PermissionsManager.areLocationPermissionsGranted(this)) {
      Log.d(TAG, "onCreate: permissionsManager.requestLocationPermissions(this);");
      permissionsManager.requestLocationPermissions(this);
    } else {
      Log.d(TAG, "onCreate: Location permissions not needed");
    }

  }

  @Override
  public void onCardClick(int positionOfCardInRecyclerView) {
    repositionMapCamera(getCoordinatesOfSelectedLocation(positionOfCardInRecyclerView));
  }

  @Override
  public void onStartNavigationGoButtonClick(int position) {
    startNavigation(getCoordinatesOfSelectedLocation(position));
  }

  @Override
  public void OnWalkingRouteButtonClick(int position) {
    // TODO: Add loading spinner UI on top of menu?
    currentDesiredRouteProfile = DirectionsCriteria.PROFILE_WALKING;
    getInformationFromDirectionsApi(getCoordinatesOfSelectedLocation(position), true, position);
  }

  @Override
  public void onBikingRouteButtonClick(int position) {
    // TODO: Add loading spinner UI on top of menu?
    currentDesiredRouteProfile = DirectionsCriteria.PROFILE_CYCLING;
    getInformationFromDirectionsApi(getCoordinatesOfSelectedLocation(position), true, position);
  }

  @Override
  public void onDrivingRouteButtonClick(int position) {
    // TODO: Add loading spinner UI on top of menu?
    currentDesiredRouteProfile = DirectionsCriteria.PROFILE_DRIVING;
    // Want the route to take real-time traffic into account? If so, set
    // currentDesiredRouteProfile = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC;
    getInformationFromDirectionsApi(getCoordinatesOfSelectedLocation(position), true, position);
  }

  @Override
  public void onExplanationNeeded(List<String> permissionsToExplain) {
    Toast.makeText(this, R.string.permission_explanation, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onPermissionResult(boolean granted) {
    if (granted) {
    } else {
      Toast.makeText(this, R.string.did_not_grant_permissions, Toast.LENGTH_LONG).show();
    }
  }

  private LatLng getCoordinatesOfSelectedLocation(int selectedLocationPosition) {
    // Get the selected individual location via its card's position in the recyclerview of cards
    IndividualLocation selectedLocation = listOfIndividualLocations.get(selectedLocationPosition);

    // Retrieve and change the selected card's marker to the selected marker icon
    Marker markerTiedToSelectedCard = mapboxMap.getMarkers().get(selectedLocationPosition);
    adjustMarkerSelectStateIcons(markerTiedToSelectedCard);

    // Reposition the map camera target to the selected marker
    return selectedLocation.getLocation();
  }


  private void getInformationFromDirectionsApi(LatLng selectedLocationCoordinates, final boolean fromMarkerClick,
                                               @Nullable final Integer listIndex) {
    // Set up origin and destination coordinates for the call to the Mapbox Directions API
    Position mockCurrentLocation = Position.fromLngLat(MOCK_DEVICE_LOCATION_LAT_LNG.getLongitude(),
      MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude());
    Position destinationMarker = Position.fromLngLat(selectedLocationCoordinates.getLongitude(),
      selectedLocationCoordinates.getLatitude());

    // Initialize the directionsApiClient object for eventually drawing a navigation route on the map
    directionsApiClient = new MapboxDirections.Builder()
      .setOrigin(mockCurrentLocation)
      .setDestination(destinationMarker)
      .setOverview(DirectionsCriteria.OVERVIEW_FULL)
      .setProfile(currentDesiredRouteProfile)
      .setAccessToken(getString(R.string.access_token))
      .build();

    directionsApiClient.enqueueCall(new Callback<DirectionsResponse>() {
      @Override
      public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        // Check that the response isn't null and that the response has a route
        if (response.body() == null) {
          Log.e("MapActivity", "No routes found, make sure you set the right user and access token.");
        } else if (response.body().getRoutes().size() < 1) {
          Log.e("MapActivity", "No routes found");
        } else {
          if (fromMarkerClick) {
            // Retrieve and draw the navigation route on the map
            currentRoute = response.body().getRoutes().get(0);
            drawNavigationPolylineRoute(currentRoute);
          } else {
            // Use Mapbox Turf helper method to convert meters to miles and then format the mileage number
            DecimalFormat df = new DecimalFormat("#.#");
            String finalConvertedFormattedDistance = String.valueOf(df.format(TurfHelpers.convertDistance(
              response.body().getRoutes().get(0).getDistance(), "meters", "miles")));

            // Set the distance for each location object in the list of locations
            if (listIndex != null) {
              listOfIndividualLocations.get(listIndex).setDistance(finalConvertedFormattedDistance);
              // Refresh the displayed recyclerview when the location's distance is set
              styleRvAdapter.notifyDataSetChanged();
            }
          }
        }
      }

      @Override
      public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
        Toast.makeText(MapActivity.this, R.string.failure_to_retrieve, Toast.LENGTH_LONG).show();
      }
    });
  }

  private void repositionMapCamera(LatLng newTarget) {
    CameraPosition newCameraPosition = new CameraPosition.Builder()
      .target(newTarget)
      .build();
    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), CAMERA_MOVEMENT_SPEED_IN_MILSECS);
  }

  private void addMockDeviceLocationMarkerToMap() {
    // Add the fake user location marker to the map
    mapboxMap.addMarker(new MarkerOptions()
      .position(MOCK_DEVICE_LOCATION_LAT_LNG)
      .title(getString(R.string.mock_location_title))
      .icon(customThemeManager.getMockLocationIcon()));
  }

  private void getFeatureCollectionFromJson() throws IOException {
    try {
      // Use fromJson() method to convert the GeoJSON file into a usable FeatureCollection object
      featureCollection = FeatureCollection.fromJson(loadGeoJsonFromAsset("list_of_locations.geojson"));
    } catch (Exception exception) {
      Log.e("MapActivity", "getFeatureCollectionFromJson: " + exception);
    }
  }

  private String loadGeoJsonFromAsset(String filename) {
    try {
      // Load the GeoJSON file from the local asset folder
      InputStream is = getAssets().open(filename);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      return new String(buffer, "UTF-8");
    } catch (Exception exception) {
      Log.e("MapActivity", "Exception Loading GeoJSON: " + exception.toString());
      exception.printStackTrace();
      return null;
    }
  }

  private void setUpRecyclerViewOfLocationCards(int selectedTheme) {
    // Initialize the recyclerview of location cards and a custom class for automatic card scrolling
    locationsRecyclerView = findViewById(R.id.map_layout_rv);
    locationsRecyclerView.setHasFixedSize(true);
    locationsRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
    styleRvAdapter = new LocationRecyclerViewAdapter(listOfIndividualLocations,
      getApplicationContext(), this, this
      , this, this, this, selectedTheme);
    locationsRecyclerView.setAdapter(styleRvAdapter);
    SnapHelper snapHelper = new LinearSnapHelper();
    snapHelper.attachToRecyclerView(locationsRecyclerView);
  }

  private void initMarkerClickListener() {
    mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(@NonNull Marker marker) {

        // Get the position of the selected marker
        LatLng latLngOfSelectedMarker = marker.getPosition();

        // Check that the selected marker isn't the mock device location marker
        if (!marker.getPosition().equals(MOCK_DEVICE_LOCATION_LAT_LNG)) {

          for (int x = 0; x < mapboxMap.getMarkers().size(); x++) {
            if (mapboxMap.getMarkers().get(x).getPosition() == latLngOfSelectedMarker) {
              // Scroll the recyclerview to the selected marker's card. It's "x-1" below because
              // the mock device location marker is part of the marker list but doesn't have its own card
              // in the actual recyclerview.
              locationsRecyclerView.smoothScrollToPosition(x);
            }
          }
          adjustMarkerSelectStateIcons(marker);

          // Get the directionsApiClient route to the selected marker except if the mock device location marker is selected
          if (!marker.getIcon().equals(customThemeManager.getMockLocationIcon())) {
            // Check for an internet connection before making the call to Mapbox Directions API
            if (deviceHasInternetConnection()) {
              // Start the call to the Mapbox Directions API
              getInformationFromDirectionsApi(marker.getPosition(), false, null);
            } else {
              Toast.makeText(MapActivity.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
            }
          }
        }

        // Return true so that the selected marker's info window doesn't pop up
        return true;
      }
    });
  }

  private void startNavigation(LatLng selectedDestination) {

    int lightNavUiStyleToUse;
    int darkNavUiStyleToUse = R.style.NavigationViewDark;

    switch (selectedTheme) {
      case R.style.AppTheme_Purple:
        lightNavUiStyleToUse = R.style.CustomPurpleNavigationViewLight;
        // Uncomment line below and adjust custom dark style
        // darkNavUiStyleToUse = R.style.CustomPurpleNavigationViewDark;
        break;
      case R.style.AppTheme_Blue:
        lightNavUiStyleToUse = R.style.CustomBlueNavigationViewLight;
        // Uncomment line below and adjust custom dark style
        // darkNavUiStyleToUse = R.style.CustomBlueNavigationViewDark;
        break;
      case R.style.AppTheme_Green:
        lightNavUiStyleToUse = R.style.CustomGreenNavigationViewLight;
        // Uncomment line below and adjust custom dark style
        // darkNavUiStyleToUse = R.style.CustomGreenNavigationViewDark;
        break;
      case R.style.AppTheme_Neutral:
        lightNavUiStyleToUse = R.style.CustomNeutralNavigationViewLight;
        // Uncomment line below and adjust custom dark style
        // darkNavUiStyleToUse = R.style.CustomNeutralNavigationViewDark;
        break;
      case R.style.AppTheme_Gray:
        lightNavUiStyleToUse = R.style.CustomGrayNavigationViewLight;
        // Uncomment line below and adjust custom dark style
        // darkNavUiStyleToUse = R.style.CustomGrayNavigationViewDark;
        break;
      default:
        lightNavUiStyleToUse = R.style.CustomGrayNavigationViewLight;
        // Uncomment line below and adjust custom dark style
        // darkNavUiStyleToUse = R.style.CustomGrayNavigationViewDark;
        break;
    }

    NavigationViewOptions options = NavigationViewOptions.builder()
      .origin(Point.fromLngLat(MOCK_DEVICE_LOCATION_LAT_LNG.getLongitude(), MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude()))
      .destination(Point.fromLngLat(selectedDestination.getLongitude(), selectedDestination.getLatitude()))
      .awsPoolId(null)
      .lightThemeResId(lightNavUiStyleToUse)
      .darkThemeResId(darkNavUiStyleToUse)
      .shouldSimulateRoute(true)
      .build();

    NavigationLauncher.startNavigation(this, options);
  }

  private void adjustMarkerSelectStateIcons(Marker marker) {
    // Set all of the markers' icons to the unselected marker icon
    for (Marker singleMarker : mapboxMap.getMarkers()) {
      if (!singleMarker.getTitle().equals(getString(R.string.mock_location_title))) {
        singleMarker.setIcon(customThemeManager.getUnselectedMarkerIcon());
      }
    }

    // Change the selected marker's icon to a selected state marker except if the mock device location marker is selected
    if (!marker.getIcon().equals(customThemeManager.getMockLocationIcon())) {
      marker.setIcon(customThemeManager.getSelectedMarkerIcon());
    }
  }

  private void drawNavigationPolylineRoute(DirectionsRoute route) {
    // Check for and remove a previously-drawn navigation route polyline before drawing the new one
    if (mapboxMap.getPolylines().size() > 0) {
      mapboxMap.removePolyline(mapboxMap.getPolylines().get(0));
    }

    // Convert LineString coordinates into a LatLng[]
    LineString lineString = LineString.fromPolyline(route.getGeometry(), PRECISION_6);
    List<Position> coordinates = lineString.getCoordinates();
    LatLng[] polylineDirectionsPoints = new LatLng[coordinates.size()];
    for (int i = 0; i < coordinates.size(); i++) {
      polylineDirectionsPoints[i] = new LatLng(
        coordinates.get(i).getLatitude(),
        coordinates.get(i).getLongitude());
    }

    // Draw the navigation route polyline on to the map
    mapboxMap.addPolyline(new PolylineOptions()
      .add(polylineDirectionsPoints)
      .color(customThemeManager.getNavigationLineColor())
      .width(NAVIGATION_LINE_WIDTH));
  }

  private boolean deviceHasInternetConnection() {
    ConnectivityManager connectivityManager = (ConnectivityManager)
      getApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnected();
  }

  //region Add the mapView's lifecycle to the activity's lifecycle methods
  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }
  //endregion

  /**
   * Custom class which creates marker icons and colors based on the selected theme
   */
  class CustomThemeManager {
    private static final String BUILDING_EXTRUSION_COLOR = "#c4dbed";
    private static final float BUILDING_EXTRUSION_OPACITY = .8f;
    private int selectedTheme;
    private Context context;
    private Icon unselectedMarkerIcon;
    private Icon selectedMarkerIcon;
    private Icon mockLocationIcon;
    private int navigationLineColor;
    private MapboxMap mapboxMap;
    private MapView mapView;

    CustomThemeManager(int selectedTheme, Context context,
                       MapView mapView, MapboxMap mapboxMap) {
      this.selectedTheme = selectedTheme;
      this.context = context;
      this.mapboxMap = mapboxMap;
      this.mapView = mapView;
    }

    private void initializeTheme() {
      switch (selectedTheme) {
        case R.style.AppTheme_Blue:
          mapboxMap.setStyle(getString(R.string.blue_map_style));
          navigationLineColor = getResources().getColor(R.color.navigationRouteLine_blue);
          unselectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.blue_unselected_ice_cream);
          selectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.blue_selected_ice_cream);
          mockLocationIcon = IconFactory.getInstance(context).fromResource(R.drawable.blue_user_location);
          showBuildingExtrusions();
          break;
        case R.style.AppTheme_Purple:
          mapboxMap.setStyle(getString(R.string.purple_map_style));
          navigationLineColor = getResources().getColor(R.color.navigationRouteLine_purple);
          unselectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.purple_unselected_burger);
          selectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.purple_selected_burger);
          mockLocationIcon = IconFactory.getInstance(context).fromResource(R.drawable.purple_user_location);
          break;
        case R.style.AppTheme_Green:
          mapboxMap.setStyle(getString(R.string.terminal_map_style));
          navigationLineColor = getResources().getColor(R.color.navigationRouteLine_green);
          unselectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.green_unselected_money);
          selectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.green_selected_money);
          mockLocationIcon = IconFactory.getInstance(context).fromResource(R.drawable.green_user_location);
          break;
        case R.style.AppTheme_Neutral:
          mapboxMap.setStyle(Style.MAPBOX_STREETS);
          navigationLineColor = getResources().getColor(R.color.navigationRouteLine_neutral);
          unselectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.white_unselected_house);
          selectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.gray_selected_house);
          mockLocationIcon = IconFactory.getInstance(context).fromResource(R.drawable.neutral_orange_user_location);
          break;
        case R.style.AppTheme_Gray:
          mapboxMap.setStyle(Style.LIGHT);
          navigationLineColor = getResources().getColor(R.color.navigationRouteLine_gray);
          unselectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.white_unselected_bike);
          selectedMarkerIcon = IconFactory.getInstance(context).fromResource(R.drawable.gray_selected_bike);
          mockLocationIcon = IconFactory.getInstance(context).fromResource(R.drawable.gray_user_location);
          break;
      }
    }

    private void showBuildingExtrusions() {
      // Use the Mapbox building plugin to display and customize the opacity/color of building extrusions
      BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, mapboxMap);
      buildingPlugin.setVisibility(true);
      buildingPlugin.setOpacity(BUILDING_EXTRUSION_OPACITY);
      buildingPlugin.setColor(Color.parseColor(BUILDING_EXTRUSION_COLOR));
    }

    Icon getUnselectedMarkerIcon() {
      return unselectedMarkerIcon;
    }

    Icon getSelectedMarkerIcon() {
      return selectedMarkerIcon;
    }

    Icon getMockLocationIcon() {
      return mockLocationIcon;
    }

    int getNavigationLineColor() {
      return navigationLineColor;
    }

    public int getSelectedTheme() {
      return selectedTheme;
    }
  }
}
