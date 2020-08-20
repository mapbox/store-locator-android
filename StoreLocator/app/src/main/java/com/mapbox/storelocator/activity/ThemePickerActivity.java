package com.mapbox.storelocator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.storelocator.R;

import static com.mapbox.storelocator.util.StringConstants.SELECTED_THEME;

/**
 * Activity for picking the specific map theme to view
 */
public class ThemePickerActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // This contains the MapView in XML and needs to be called after the access token is configured.
    setContentView(R.layout.activity_theme_picker);
    setUpButtonClickListeners();
  }

  private void goToMapActivity(int selectedTheme) {
    Intent mapIntent = new Intent(this, MapActivity.class);
    mapIntent.putExtra(SELECTED_THEME, selectedTheme);
    startActivity(mapIntent);
  }

  private void setUpButtonClickListeners() {
    ImageButton purpleButton = findViewById(R.id.purple_theme_button);
    ImageButton blueButton = findViewById(R.id.blue_theme_button);
    ImageButton greenButton = findViewById(R.id.green_theme_button);
    ImageButton neutralButton = findViewById(R.id.neutral_theme_button);
    ImageButton grayButton = findViewById(R.id.gray_theme_button);

    purpleButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        goToMapActivity(R.style.AppTheme_Purple);
      }
    });
    blueButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        goToMapActivity(R.style.AppTheme_Blue);
      }
    });
    greenButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        goToMapActivity(R.style.AppTheme_Green);
      }
    });
    neutralButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        goToMapActivity(R.style.AppTheme_Neutral);
      }
    });
    grayButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        goToMapActivity(R.style.AppTheme_Gray);
      }
    });
  }
}
