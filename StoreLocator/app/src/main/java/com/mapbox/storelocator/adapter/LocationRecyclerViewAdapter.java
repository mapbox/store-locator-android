package com.mapbox.storelocator.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.storelocator.R;
import com.mapbox.storelocator.model.IndividualLocation;

import java.util.List;

/**
 * RecyclerView adapter to display a list of location cards on top of the map
 */
public class LocationRecyclerViewAdapter extends
  RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> {

  private List<IndividualLocation> listOfLocations;
  private Context context;
  private int selectedTheme;
  private static CardClickListener clickListener;
  private static StartNavClickListener startNavClickListener;
  private static WalkingRouteButtonClickListener walkingRouteButtonClickListener;
  private static BikingRouteButtonClickListener bikingRouteButtonClickListener;
  private static DrivingRouteButtonClickListener drivingRouteButtonClickListener;
  private ImageButton startNavGoButton = null;
  private Drawable emojiForCircle = null;
  private Drawable backgroundCircle = null;
  private int upperCardSectionColor = 0;
  private int locationNameColor = 0;
  private int locationAddressColor = 0;
  private int locationPhoneNumColor = 0;
  private int locationPhoneHeaderColor = 0;
  private int locationHoursColor = 0;
  private int locationHoursHeaderColor = 0;
  private int locationDistanceNumColor = 0;
  private int milesAbbreviationColor = 0;
  private int startNavGoButtonColor = 0;

  public LocationRecyclerViewAdapter(List<IndividualLocation> styles,
                                     Context context, CardClickListener cardClickListener,
                                     StartNavClickListener startNavClickListener,
                                     WalkingRouteButtonClickListener walkingRouteButtonClickListener,
                                     BikingRouteButtonClickListener bikingRouteButtonClickListener,
                                     DrivingRouteButtonClickListener drivingRouteButtonClickListener,
                                     int selectedTheme) {

    this.context = context;
    this.listOfLocations = styles;
    this.selectedTheme = selectedTheme;
    this.clickListener = cardClickListener;
    this.startNavClickListener = startNavClickListener;
    this.walkingRouteButtonClickListener = walkingRouteButtonClickListener;
    this.bikingRouteButtonClickListener = bikingRouteButtonClickListener;
    this.drivingRouteButtonClickListener = drivingRouteButtonClickListener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int singleRvCardToUse = R.layout.single_location_map_view_rv_card;
    View itemView = LayoutInflater.from(parent.getContext()).inflate(singleRvCardToUse, parent, false);
    return new ViewHolder(itemView, context, selectedTheme);
  }

  public interface CardClickListener {
    void onCardClick(int position);
  }

  public interface StartNavClickListener {
    void onStartNavigationGoButtonClick(int position);
  }

  public interface WalkingRouteButtonClickListener {
    void OnWalkingRouteButtonClick(int position);
  }

  public interface BikingRouteButtonClickListener {
    void onBikingRouteButtonClick(int position);
  }

  public interface DrivingRouteButtonClickListener {
    void onDrivingRouteButtonClick(int position);
  }


  @Override
  public int getItemCount() {
    return listOfLocations.size();
  }

  @Override
  public void onBindViewHolder(ViewHolder card, int position) {

    IndividualLocation locationCard = listOfLocations.get(position);

    card.nameTextView.setText(locationCard.getName());
    card.addressTextView.setText(locationCard.getAddress());
    card.phoneNumTextView.setText(locationCard.getPhoneNum());
    card.hoursTextView.setText(locationCard.getHours());
    card.distanceNumberTextView.setText(locationCard.getDistance());

    switch (selectedTheme) {
      case R.style.AppTheme_Blue:
        emojiForCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ice_cream_icon, null);
        backgroundCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.blue_circle, null);
        setColors(R.color.colorPrimaryDark_blue, R.color.white, R.color.white, R.color.cardHourAndPhoneTextColor_blue,
          R.color.cardHourAndPhoneHeaderTextColor_blue, R.color.cardHourAndPhoneTextColor_blue,
          R.color.cardHourAndPhoneHeaderTextColor_blue, R.color.white, R.color.white, R.color.startNavGoButton_purple_theme);
        setAlphas(card, .41f, .48f, 100f, .48f,
          100f,
          .41f);
        break;
      case R.style.AppTheme_Purple:
        emojiForCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.cheese_burger_icon, null);
        backgroundCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.purple_circle, null);
        setColors(R.color.colorPrimaryDark_purple, R.color.white, R.color.white, R.color.cardHourAndPhoneTextColor_purple,
          R.color.cardHourAndPhoneTextColor_purple, R.color.cardHourAndPhoneTextColor_purple,
          R.color.cardHourAndPhoneTextColor_purple, R.color.white, R.color.white, R.color.startNavGoButton_blue_theme);
        setAlphas(card, .41f, .36f, .94f, .36f,
          .94f,
          .41f);
        break;
      case R.style.AppTheme_Green:
        emojiForCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.money_bag_icon, null);
        card.emojiImageView.setPadding(8, 0, 0, 0);
        backgroundCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.green_circle, null);
        setColors(R.color.colorPrimaryDark_green, R.color.white, R.color.white, R.color.cardHourAndPhoneTextColor_green,
          R.color.black, R.color.cardHourAndPhoneTextColor_green,
          R.color.black, R.color.white, R.color.white, R.color.startNavGoButton_green_theme);
        setAlphas(card, 100f, .48f, 100f, .48f,
          100f,
          100f);
        break;
      case R.style.AppTheme_Neutral:
        emojiForCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.house_icon, null);
        backgroundCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.white_circle, null);
        setColors(R.color.white, R.color.black, R.color.black, R.color.black,
          R.color.black, R.color.black,
          R.color.black, R.color.black, R.color.black, R.color.startNavGoButton_neutral_theme);
        setAlphas(card, .37f, .37f, 100f, .37f,
          100f,
          .37f);
        break;
      case R.style.AppTheme_Gray:
        emojiForCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.bicycle_icon, null);
        backgroundCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.gray_circle, null);
        setColors(R.color.colorPrimaryDark_gray, R.color.white, R.color.white, R.color.cardHourAndPhoneTextColor_gray,
          R.color.cardHourAndPhoneTextColor_gray, R.color.cardHourAndPhoneTextColor_gray,
          R.color.cardHourAndPhoneTextColor_gray, R.color.white, R.color.white, R.color.startNavGoButton_gray_theme);
        setAlphas(card, .41f, .48f, 100f, .41f,
          100f,
          .41f);
        break;
    }

    card.emojiImageView.setImageDrawable(emojiForCircle);
    card.constraintUpperColorSection.setBackgroundColor(upperCardSectionColor);
    card.backgroundCircleImageView.setImageDrawable(backgroundCircle);
    card.nameTextView.setTextColor(locationNameColor);
    card.phoneNumTextView.setTextColor(locationPhoneNumColor);
    card.hoursTextView.setTextColor(locationHoursColor);
    card.hoursHeaderTextView.setTextColor(locationHoursHeaderColor);
    card.distanceNumberTextView.setTextColor(locationDistanceNumColor);
    card.milesAbbreviationTextView.setTextColor(milesAbbreviationColor);
    card.addressTextView.setTextColor(locationAddressColor);
    card.phoneHeaderTextView.setTextColor(locationPhoneHeaderColor);

    // TODO: Adjust below
    card.startNavGoButton.setColorFilter(startNavGoButtonColor);
  }

  private void setColors(int colorForUpperCard, int colorForName, int colorForAddress,
                         int colorForHours, int colorForHoursHeader, int colorForPhoneNum,
                         int colorForPhoneHeader, int colorForDistanceNum, int colorForMilesAbbreviation,
                         int colorForStartNavGoButton) {
    upperCardSectionColor = ResourcesCompat.getColor(context.getResources(), colorForUpperCard, null);
    locationNameColor = ResourcesCompat.getColor(context.getResources(), colorForName, null);
    locationAddressColor = ResourcesCompat.getColor(context.getResources(), colorForAddress, null);
    locationHoursColor = ResourcesCompat.getColor(context.getResources(), colorForHours, null);
    locationHoursHeaderColor = ResourcesCompat.getColor(context.getResources(), colorForHoursHeader, null);
    locationPhoneNumColor = ResourcesCompat.getColor(context.getResources(), colorForPhoneNum, null);
    locationPhoneHeaderColor = ResourcesCompat.getColor(context.getResources(), colorForPhoneHeader, null);
    locationDistanceNumColor = ResourcesCompat.getColor(context.getResources(), colorForDistanceNum, null);
    milesAbbreviationColor = ResourcesCompat.getColor(context.getResources(), colorForMilesAbbreviation, null);
    startNavGoButtonColor = ResourcesCompat.getColor(context.getResources(), colorForStartNavGoButton, null);
  }

  private void setAlphas(ViewHolder card, float addressAlpha, float hoursHeaderAlpha, float hoursNumAlpha,
                         float phoneHeaderAlpha, float phoneNumAlpha, float milesAbbreviationAlpha) {
    card.addressTextView.setAlpha(addressAlpha);
    card.hoursHeaderTextView.setAlpha(hoursHeaderAlpha);
    card.hoursTextView.setAlpha(hoursNumAlpha);
    card.phoneHeaderTextView.setAlpha(phoneHeaderAlpha);
    card.phoneNumTextView.setAlpha(phoneNumAlpha);
    card.milesAbbreviationTextView.setAlpha(milesAbbreviationAlpha);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    TextView nameTextView;
    TextView addressTextView;
    TextView phoneNumTextView;
    TextView hoursTextView;
    TextView distanceNumberTextView;
    TextView hoursHeaderTextView;
    TextView milesAbbreviationTextView;
    TextView phoneHeaderTextView;
    ConstraintLayout constraintUpperColorSection;
    CardView cardView;
    ImageView backgroundCircleImageView;
    ImageView emojiImageView;
    ImageButton startNavGoButton;
    ImageButton walkingRouteButton;
    ImageButton bikingRouteButton;
    ImageButton drivingRouteButton;

    ViewHolder(View itemView, final Context context, final int selectedTheme) {
      super(itemView);
      nameTextView = itemView.findViewById(R.id.location_name_tv);
      addressTextView = itemView.findViewById(R.id.location_description_tv);
      phoneNumTextView = itemView.findViewById(R.id.location_phone_num_tv);
      phoneHeaderTextView = itemView.findViewById(R.id.phone_header_tv);
      hoursTextView = itemView.findViewById(R.id.location_hours_tv);
      backgroundCircleImageView = itemView.findViewById(R.id.background_circle);
      emojiImageView = itemView.findViewById(R.id.emoji);
      constraintUpperColorSection = itemView.findViewById(R.id.constraint_upper_color);
      distanceNumberTextView = itemView.findViewById(R.id.distance_num_tv);
      hoursHeaderTextView = itemView.findViewById(R.id.hours_header_tv);
      milesAbbreviationTextView = itemView.findViewById(R.id.miles_mi_tv);
      cardView = itemView.findViewById(R.id.map_view_location_card);
      startNavGoButton = itemView.findViewById(R.id.start_navigation_image_button);
      walkingRouteButton = itemView.findViewById(R.id.walking_route_image_button);
      bikingRouteButton = itemView.findViewById(R.id.show_biking_route_image_button);
      drivingRouteButton = itemView.findViewById(R.id.driving_route_image_button);
      cardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          clickListener.onCardClick(getLayoutPosition());
        }
      });
      startNavGoButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          startNavClickListener.onStartNavigationGoButtonClick(getLayoutPosition());
        }
      });
      walkingRouteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          walkingRouteButtonClickListener.OnWalkingRouteButtonClick(getLayoutPosition());
          moveBackgroundCircleToSelectedRouteMode("walking", context, selectedTheme);
        }
      });
      bikingRouteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          bikingRouteButtonClickListener.onBikingRouteButtonClick(getLayoutPosition());
          moveBackgroundCircleToSelectedRouteMode("biking", context, selectedTheme);
        }
      });
      drivingRouteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          drivingRouteButtonClickListener.onDrivingRouteButtonClick(getLayoutPosition());
          moveBackgroundCircleToSelectedRouteMode("driving", context, selectedTheme);
        }
      });
    }

    private void moveBackgroundCircleToSelectedRouteMode(String buttonType, Context context, int selectedTheme) {
      switch (buttonType) {
        case "walking":
          drivingRouteButton.setColorFilter(context.getResources().getColor(R.color.white));
          drivingRouteButton.setBackgroundResource(0);
          bikingRouteButton.setColorFilter(context.getResources().getColor(R.color.white));
          bikingRouteButton.setBackgroundResource(0);
          walkingRouteButton.setBackgroundResource(R.drawable.white_circle);
          switch (selectedTheme) {
            case R.style.AppTheme_Blue:
              walkingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_blue));
              break;
            case R.style.AppTheme_Purple:
              walkingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_purple));
              break;
            case R.style.AppTheme_Green:
              walkingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_green));
              break;
            case R.style.AppTheme_Neutral:
              walkingRouteButton.setColorFilter(Color.parseColor("#ED2724"));
              break;
            case R.style.AppTheme_Gray:
              walkingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_gray));
              break;
          }
          break;
        case "biking":
          walkingRouteButton.setBackgroundResource(0);
          walkingRouteButton.setColorFilter(context.getResources().getColor(R.color.white));
          drivingRouteButton.setBackgroundResource(0);
          drivingRouteButton.setColorFilter(context.getResources().getColor(R.color.white));
          bikingRouteButton.setBackgroundResource(R.drawable.white_circle);
          switch (selectedTheme) {
            case R.style.AppTheme_Blue:
              bikingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_blue));
              break;
            case R.style.AppTheme_Purple:
              bikingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_purple));
              break;
            case R.style.AppTheme_Green:
              bikingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_green));
              break;
            case R.style.AppTheme_Neutral:
              bikingRouteButton.setColorFilter(Color.parseColor("#ED2724"));
              break;
            case R.style.AppTheme_Gray:
              bikingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_gray));
              break;
          }
          break;
        case "driving":
          walkingRouteButton.setBackgroundResource(0);
          walkingRouteButton.setColorFilter(context.getResources().getColor(R.color.white));
          bikingRouteButton.setColorFilter(context.getResources().getColor(R.color.white));
          bikingRouteButton.setBackgroundResource(0);
          drivingRouteButton.setBackgroundResource(R.drawable.white_circle);
          switch (selectedTheme) {
            case R.style.AppTheme_Blue:
              drivingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_blue));
              break;
            case R.style.AppTheme_Purple:
              drivingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_purple));
              break;
            case R.style.AppTheme_Green:
              drivingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_green));
              break;
            case R.style.AppTheme_Neutral:
              drivingRouteButton.setColorFilter(Color.parseColor("#ED2724"));
              break;
            case R.style.AppTheme_Gray:
              drivingRouteButton.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark_gray));
              break;
          }
          break;
      }
    }
  }
}
