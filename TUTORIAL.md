# Tutorial

This guide will walk you through how to use our **Android Store Locator starter kit** to create a custom store locator map that can be integrated into any Android application. You'll be able to browse several store locations, select a specific store to view more information, and retrieve an estimated travel distance and route from a user's location to any of the store locations. You can start with one of five different themes and customize everything from store location data to marker icons and individual store cards.

![android-store-locator-intro](https://user-images.githubusercontent.com/4394910/61020375-60e86700-a352-11e9-8009-7b463a936c9b.gif)


## Requirements

Here's what you'll need to start building a store locator for Android:

- **Starter kit files**. Download or clone this repository from GitHub. This includes all the necessary files for a functional Android Studio project.
- **Android Studio 3.0**. You will need to use the starter kit files with [a version of Android Studio](https://developer.android.com/studio/index.html) that is 3.0 or higher. If you try to run your application and receive an `Error running android: Gradle project sync failed. Please fix your project and try again`, verify that you’re using a version of Android Studio that is 3.0 or higher.
- **Mapbox access token**. You will use an [access token](https://www.mapbox.com/help/glossary/access-token) to associate a map with your account and you can find it on the [Account page](https://www.mapbox.com/account/).
- **An Android device (physical or virtual)**. You will need either a physical Android device or an [emulated Android device](https://developer.android.com/studio/run/emulator.html) to preview the store locator. *Note: the device’s Android SDK version number must be [16 (“Jelly Bean”) or higher](https://source.android.com/source/build-numbers).*
- **Optional: Android Drawable Importer**. If you plan to add custom images to your application (for example, custom icons for markers at store locations), install [the Android Drawable Importer](http://www.javahelps.com/2015/02/android-drawable-importer.html) plugin to add images to your project.
- **Optional: SVG editing tool**. If you'd like to create custom layouts or icons, you can use a program like [Sketch](http://sketchapp.com), [Figma](http://figma.com), or Adobe Illustrator.


## Set up the starter kit

After you've downloaded or cloned this repo, open the project in Android Studio 3.0. In the project, you’ll find all the source files for the entire app, including:

- Five UI theme variations.
- Sample GeoJSON data with store locations.
- Code for retrieving directions with the [Mapbox Directions API](https://docs.mapbox.com/api/navigation/#directions) and displaying a navigation route line on the map.

### Access token

Before running the application, you will need to add your Mapbox [access token](https://docs.mapbox.com/help/glossary/access-token/) to the `strings.xml` file. You can find this file in `app` > `src` > `main` > `res` > `values`.

```xml
<!-- Add your Mapbox access token between >< in the line below. Your token can
be retrieved at https://www.mapbox.com/account/
-->
<string name="access_token" translatable="false">YOUR_ACCESS_TOKEN</string>
```

### `MapActivity`

The `MapActivity.java` file is the primary place you'll adjust code. This activity can be found by navigating to  `app` > `src` > `main` > `java` > `com` > `mapbox` > `storelocator` > `activity`. This is the file where you’ll customize your app by:

- Selecting a theme to start with.
- Setting the map's style URL.
- Choosing the marker icons.
- Customizing other UI elements and colors as needed.

*Note: The starter kit files include detailed inline comments that tell you what customizations are possible.*

## Choose a theme

Once you have the starter kit set up, start by picking a theme. Unless adjusted, the theme picker activity will be the first thing you see when the app is launched. Choose a theme by clicking on the preview image for one of five themes made by our mobile designers.

![android-store-locator-select-theme](https://user-images.githubusercontent.com/4394910/61020459-b7ee3c00-a352-11e9-8129-ad34388df378.png)

Once you’ve selected your theme, remove this activity before adding the `MapActivity.java` file to your final app:

- Delete the file – `ThemePickerActivity.java` is in the same directory as the `MapActivity.java` file (`app` > `src` > `main` > `java` > `com` > `mapbox` > `storelocator` > `activity`).
- Remove the `ThemePickerActivity` from the app’s manifest file and move the launcher intent filter to the `MapActivity` (or any other activity you'd like to first open when the app is launched):

Add to `Manifest.xml` file:

```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN"/>
    <category android:name="android.intent.category.LAUNCHER"/>
</intent-filter>
```

- Adjust the `MapActivity.java` file so it no longer initializes the `chosenTheme` object via `getIntent().getIntExtra(*SELECTED_THEME*, R.style.*AppTheme_Blue*);`. Instead, set it equal to whichever theme you chose:

Add to `MapActivity.java` file:

```java
chosenTheme = R.style.YOUR_CHOSEN_THEME; // Example of a theme: AppTheme_Blue
```

Once you've made these changes, run your app again. You should see the map using your chosen theme and some store location data. In the next step, you'll add your own data with store locations.

![android-store-locator-blue-theme](https://user-images.githubusercontent.com/4394910/61020765-ab1e1800-a353-11e9-8ab0-c38ddd301926.png)


## Add your data

The starter kit contains a GeoJSON file called `list_of_locations.geojson`  where you’ll find all the current store locations that are visible on the map. [GeoJSON](https://docs.mapbox.com/help/glossary/geojson/) is a file format for geospatial data and a subset of the JSON format. In this section, you'll update the sample data to reflect your actual store locations.

Locate the `list_of_locations.geojson` file in `app` > `src` > `main` > `assets`. Take a look at how the data is formatted. Notice that each store location is a separate GeoJSON _feature_ in the file. Each feature has four `properties` describing a few characteristics about the store location and `geometry` specifying that the feature is a single point and where that point is located in the world.

You can replace the data with your own store data. If you have store location data available in GeoJSON form, you can delete the current data and replace it with your own. _Note: If your data contains different `properties` than the default data, you will need to update the related code in both the `MapActivity.java` file and the `LocationRecyclerViewAdapter` class._ We recommend using [geojson.net](http://geojson.net/) to quickly validate and visualize your GeoJSON.

If you don't have your store data in GeoJSON format, you can create a new GeoJSON file using the Mapbox Studio dataset editor. In this example, you'll create a dataset with store locations for [Jeni's Splendid Ice Cream](https://jenis.com/about/) locations in the Columbus, Ohio area using the [Mapbox Studio dataset editor](https://www.mapbox.com/studio/datasets), export a GeoJSON file, replace the GeoJSON data in the `list_of_locations.geojson`, adjust the bounding box and mock user location, and run your app to see the new store locations.

### Create a dataset

_If you already have a GeoJSON file of store locations, you can skip ahead to the "Replace data" section._

There are several ways to create a new GeoJSON file. In this guide you'll use the [Mapbox Studio dataset editor](https://www.mapbox.com/studio-manual/reference/datasets/#dataset-editor), a convenient in-browser application for creating and editing Mapbox [datasets](https://www.mapbox.com/help/glossary/dataset/). In this guide, you'll use known addresses for Jeni's locations to search and add store locations to your dataset.

First, create a new dataset:

1. Log into [Mapbox Studio](https://www.mapbox.com/studio) and navigate to the [Datasets page](https://www.mapbox.com/studio/datasets).
1. Click the **New dataset** button.
1. A new window will open. You'll use the **Blank dataset** option in the upper right corner.
1. Name your dataset "ice-cream" and click **Create**.
1. The dataset editor will automatically open.

Next, you'll begin adding stores. You can use the geocoder in the dataset editor to search for a place and the draw tools to add a new point to your dataset. You can change the geometry, placement, and properties of existing features with the dataset editor’s draw tools.

1. Click inside the **Search places** field and type `3998 Gramercy St, Columbus, Ohio`.
1. Click the address that matches your search in the search results.
1. Click the **Add to dataset** button to add that address to your dataset.
1. Click on the new feature and use the properties list on the left hand side to:
  - Change the `place_name` field to `description`.
  - Add the field name `name` and give it the value `Easton`.
  - Add the field name `hours` and give it the value `11 a.m. to 11 p.m.`.
  - Add the field name `phone` and give it the value `614-476-5364`.

You could go through this process for each known store address. After you've added each store location, you can **Save**, return to the three line menu button next to the name of your "ice-cream" dataset, and click **Download GeoJSON**.

In the interest of time, for this guide, we've provided the complete GeoJSON below for you to use in the next step:

```json
{
  "features": [
    {
      "type": "Feature",
      "properties": {
        "description": "3998 Gramercy St, Columbus, Ohio 43219, United States",
        "name": "Easton",
        "hours": "11 a.m. to 11 p.m.",
        "phone": "614-476-5364"
      },
      "geometry": {
        "coordinates": [
          -82.917665,
          40.051791
        ],
        "type": "Point"
      },
      "id": "address.11780104298749790"
    },
    {
      "type": "Feature",
      "properties": {
        "description": "714 N High St, Columbus, Ohio 43215, United States",
        "name": "North Market",
        "hours": "10 a.m. to 5 p.m.",
        "phone": "614-228-9960"
      },
      "geometry": {
        "coordinates": [
          -83.00418,
          39.971888
        ],
        "type": "Point"
      },
      "id": "address.1504101080777130"
    },
    {
      "type": "Feature",
      "properties": {
        "description": "900 Mohawk St, Columbus, Ohio 43206, United States",
        "name": "German Village",
        "hours": "Noon to 10 p.m.",
        "phone": "614-445-6513"
      },
      "geometry": {
        "coordinates": [
          -82.992495,
          39.944236
        ],
        "type": "Point"
      },
      "id": "address.18662160207305960"
    },
    {
      "type": "Feature",
      "properties": {
        "description": "1 W Bridge St, Dublin, Ohio 43017, United States",
        "name": "Dublin",
        "hours": "11 a.m. to 11 p.m.",
        "phone": "614-792-5364"
      },
      "geometry": {
        "coordinates": [
          -83.114164,
          40.099224
        ],
        "type": "Point"
      },
      "id": "address.3847501076015100"
    },
    {
      "type": "Feature",
      "properties": {
        "description": "2156 E Main St, Columbus, Ohio 43209, United States",
        "name": "Bexley",
        "hours": "11 a.m. to 11 p.m.",
        "phone": "614-231-5364"
      },
      "geometry": {
        "coordinates": [
          -82.941956,
          39.957461
        ],
        "type": "Point"
      },
      "id": "address.5795414383633980"
    },
    {
      "type": "Feature",
      "properties": {
        "description": "714 N High St, Columbus, Ohio 43215, United States",
        "name": "Short North",
        "hours": "11 a.m. to 11 p.m.",
        "phone": "614-294-5364"
      },
      "geometry": {
        "coordinates": [
          -83.003353,
          39.976965
        ],
        "type": "Point"
      },
      "id": "address.748502797349860"
    },
    {
      "type": "Feature",
      "properties": {
        "description": "160 S High St, Columbus, Ohio 43215, United States",
        "name": "The Commons",
        "hours": "11 a.m. to 8 p.m.",
        "phone": "614-867-5512"
      },
      "geometry": {
        "coordinates": [
          -82.999991,
          39.958812
        ],
        "type": "Point"
      },
      "id": "address.7894313840035430"
    },
    {
      "type": "Feature",
      "properties": {
        "description": "1281 Grandview Ave, Columbus, Ohio 43212, United States",
        "name": "Grandview Heights",
        "hours": "11 a.m. to 11 p.m.",
        "phone": "614-488-2680"
      },
      "geometry": {
        "coordinates": [
          -83.045003,
          39.983947
        ],
        "type": "Point"
      },
      "id": "address.8328248295013750"
    },
    {
      "type": "Feature",
      "properties": {
        "description": "8 N Liberty St, Powell, Ohio 43065, United States",
        "name": "Powell",
        "hours": "11 a.m. to 11 p.m.",
        "phone": "614-846-1060"
      },
      "geometry": {
        "coordinates": [
          -83.074985,
          40.158137
        ],
        "type": "Point"
      },
      "id": "address.8508074803994320"
    }
  ],
  "type": "FeatureCollection"
}
```

### Replace data

Open the `list_of_locations.geojson` file. Delete the current contents and add the GeoJSON data from the previous step. The data that's provided in the sample code and in this example contain four properties for each store location:

- `name`
- `description`
- `hours`
- `phone`

Since all the feature `properties` are identical to the four that were used in the initial GeoJSON data, your map will be fully functional immediately. If you have different information you would like to display as `properties`, you will need to update the related code in both the `MapActivity.java` file and the `LocationRecyclerViewAdapter` class.

### Update bounding box and location

The `MapActivity.java` file specifies that the map be centered on New York when the app is initialized. Replace the current code with the following code snippets to update the location to Columbus, Ohio.

First, change the target latitude and longitude when the app is initialized to center on Columbus in the `app` > `res` > `layout` > `activity_map.xml` file.

**activity_map.xml**

```xml
mapbox:mapbox_cameraTargetLat="39.95"
mapbox:mapbox_cameraTargetLng="-83"
```

Then, change the bounding box so the map opens displaying the Columbus area in the `MapActivity.java` file.

**MapActivity.java**

```java
private static final LatLngBounds LOCKED_MAP_CAMERA_BOUNDS = new LatLngBounds.Builder()
    .include(new LatLng(40.1746, -83.1426))
    .include(new LatLng(39.9278, -82.8814))
    .build();
```

Finally, change the simulated user location to be near Columbus in the `MapActivity.java` file.

**MapActivity.java**

```java
private static final LatLng MOCK_DEVICE_LOCATION_LAT_LNG = new LatLng(40, -83);
```

Run the application, and you will see the map focused on Columbus, Ohio.

![android-store-locator-custom-data](https://user-images.githubusercontent.com/4394910/61020799-c25d0580-a353-11e9-83ef-66b71e7239d7.png)


### Display distances and routes

The starter kit uses the [Mapbox Directions API](https://docs.mapbox.com/api/navigation/#directions) to display estimated travel distances and routes. Once you’ve updated the GeoJSON data for store locations and the mock location, the Mapbox Directions API will automatically read your GeoJSON file to retrieve the distances and routes to each of your locations. The code that specifies how the Directions API is used can be found in the `MapActivity.java` file.


## Add custom markers

You can use any image as a marker &mdash; you can use an emoji (like in the starter kit files), create your own icon, use your company's logo, or use an icon from several open source resources. Here are some recommended resources for finding marker icons:

- [Maki icons](https://www.mapbox.com/maki/) by Mapbox
- [Flat Icon](http://flaticon.com)
- [Get Emoji](http://getemoji.com)

In this guide, you'll use an icon from the open source project, Flat Icon. This icon, made by <a href="https://www.flaticon.com/authors/eucalyp" title="Eucalyp">Eucalyp</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>, is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>. You can download an image this icon on a white marker or download another icon from [Flat Icon](https://www.flaticon.com/). **When using icons from Flat Icon, be sure to credit the author appropriately.**

[Click to download the icon on a white marker](https://user-images.githubusercontent.com/4394910/61020880-149e2680-a354-11e9-9cb0-3cc8da68cf7d.png)

![new-ice-cream-icon](https://user-images.githubusercontent.com/4394910/61020880-149e2680-a354-11e9-9cb0-3cc8da68cf7d.png)


As mentioned in the requirements section above, we recommend installing and using [the Android Drawable Importer](http://www.javahelps.com/2015/02/android-drawable-importer.html) plugin to add images to your project. This plugin automatically scales and adds the image to a drawable folder for each screen size (MDPI, XHDPI, XXHDPI, etc).

### Add a new image to your project

After installing the Android Drawable Importer plugin and downloading the icon that you would like to use for the store location markers, add the image to the `app` > `res` > `drawable` folder. Right-click on the **drawable** folder, hover over the **New** menu item, in the sub menu choose **Batch Drawable Importer**. *Note: This option will only be available after installing the Android Drawable Importer.*

![android-store-locator-add-image](https://user-images.githubusercontent.com/4394910/61020643-4367cd00-a353-11e9-9f4e-0f10ec7c0ce4.png)

In the window that opens, click the **+** and choose your file. Another window will open. In this window, take note of the *Target-name* that is assigned to the image. For the guide, use the name `new_ice_cream_icon` and click **Ok**. In the first window, you can also click **Ok**.

Notice that a new folder has been added to the `drawable` folder. It is named *new_ice_cream_icon*, the *Target-name* from the importer process, and contains several files &mdash; your new image optimized for various screens.

![android-store-locator-batch-image](https://user-images.githubusercontent.com/4394910/61020604-2df2a300-a353-11e9-90cb-8925cdfef7a2.png)

### Use the new image as a marker

Now that you've successfully imported a new image, change the code that specifies what image should be used in the markers for each store location. The code that specifies which image to use for store markers lives in the `MapActivity.java` file. To change the image to the `new_ice_cream_image`:

1. Open `MapActivity.java`.
1. Find `private void initializeTheme() { }`.
1. Within that method, find `case R.style.AppTheme_Blue:`. This is the code that specifies the styles for markers, routes, and mock user location.
1. For both `unselectedMarkerIcon` and `selectedMarkerIcon` change `R.drawable.ice_cream_icon` to `R.drawable.new_ice_cream_icon`.

Run the application again, and you should see new ice cream icon markers at all store locations.

![android-store-locator-custom-icon](https://user-images.githubusercontent.com/4394910/61020668-5b3f5100-a353-11e9-92eb-fbba831dc372.png)


## Customize card icons

Besides customizing the look of the map, you can also customize the style of the cards for each store. Next you'll change the icon shown on the scrolling cards. This example swaps out the ice cream icon for a photo of a child enjoying an ice cream cone from the incredible open source project, [Unsplash](https://unsplash.com/). The original image has been cropped and modified to an image size of 105px x 105px.

[Click to download the image of the kid](https://user-images.githubusercontent.com/4394910/61020933-457e5b80-a354-11e9-866b-c676ee75288b.jpg)

![ice-cream-kid](https://user-images.githubusercontent.com/4394910/61020933-457e5b80-a354-11e9-866b-c676ee75288b.jpg)

### Import the image

Following the same process described in the [section above](#add-a-new-image-to-your-project), add the new scrolling card image to your project using the Android Drawable Importer plugin. Use the *Target-name* `ice_cream_kid`.

### Use the new image on the scrolling card

Now that you've successfully imported a new image, change the code that specifies what image should be used in the scrolling cards. The code for the cards lives in the `single_location_map_view_rv_card.xml` layout. In this file you can adjust the style (including the spacing, location, and size) of different elements on the cards. This single card layout is given to the `ReyclerView.java` adapter, and this adapter eventually creates the scrollable list of cards at the bottom of your device’s screen.

To change the image used in the scrolling cards you'll need to pass through your desired `drawable` when the `emojiForCircle` object is initialized in the `onBindViewHolder()` method of the `LocationRecyclerViewAdapter` class:

1. Open the `ReyclerView.java` file in `app` > `java` > `adapter`.
1. Find `switch (selectedTheme) { }`.
1. Within that method, find `case R.style.AppTheme_Blue:`. This is the code that specifies the styles for the information shown on the scrolling cards.
1. Set `emojiForCircle` equal to `ResourcesCompat.getDrawable(context.getResources(), R.drawable.ice_cream_kid, null);`
1. Delete `backgroundCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.blue_circle, null);`.

Run the application again, and you should see the new image on the scrolling cards.

![android-store-locator-custom-card-image](https://user-images.githubusercontent.com/4394910/61020703-74e09880-a353-11e9-80b3-30706af596fd.png)


## Final product

You've learned how the Android Store Locator starter kit works and modified a few of the key customizable elements of a store locator.

![android-store-locator-final-product](https://user-images.githubusercontent.com/4394910/61020722-888bff00-a353-11e9-9ed3-152457abb63f.gif)


## Next steps

Take a look at the Mapbox Maps SDK for Android [documentation](https://www.mapbox.com/android-docs/) and [examples](https://www.mapbox.com/android-docs/map-sdk/examples) to learn more about customizing your application or adding additional functionality. If you're interested in creating your own custom map style to replace the designer themes provided in the Store Locator starter kit, learn how to use Mapbox Studio to build a style that fits your brand with the [Create a custom style](https://www.mapbox.com/help/tutorials/create-a-custom-style/) tutorial.
