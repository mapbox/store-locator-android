![storelocatorkit](https://user-images.githubusercontent.com/5862541/30187389-41e3bdb2-93de-11e7-9d78-009b0ac7e016.png)

[![CircleCI](https://circleci.com/gh/mapbox/store-locator-android.svg?style=svg)](https://circleci.com/gh/mapbox/store-locator-android)

# Mapbox Android Store Locator Kit

The Android Store Locator Kit is a downloadable project for you to add beautiful plug-and-play Store Locators to your Android applications. Use the Kit to allow your users to find and browse store locations, view additional info for each store, and preview the distance and route to the store. Not building for a store owner or a business? You can use this project to locate anything from bike share hubs to ATMs to your neighborhood parks.

#### Included in the Kit:
+ Source files for the app
+ Five UI themes
+ A sample dataset in the form of a GeoJSON file
+ Code for retrieving directions to store locations with the [Mapbox Directions API](https://www.mapbox.com/help/define-directions-api/)


### Get started by downloading the project and walking through the [step-by-step tutorial](TUTORIAL.md). Don't forget your [access token](https://docs.mapbox.com/help/glossary/access-token/).


# What can I customize?

We built this Kit to cut down on the set-up and development time needed to add a Store Locator into your app. Use our starter themes and features as a plug-and-play solution, or further customize your Store Locator with our flexible build. 

### Add custom markers

Use our pre-built markers, or add in your own by creating your own icon, using your company’s logo, or another open source image. 

### Card icons

Customize the style of the interactive scrolling cards (i.e. pop-ups) included in your Store Locator. Simply import any image using the [Android Drawable importer plugin](https://github.com/winterDroid/android-drawable-importer-intellij-plugin) and modify the code. 

### Bringing your own data

Add as many store locations as you wish as a GeoJSON file. Remember that you could use this Kit to locate not just stores, but anything else like bike share hubs, ATMs, parks, or even your friends!

### Map

The Kit comes with five UI starter themes, but you can further customize these themes as you see fit. Or create your own custom map styles by using Mapbox Studio to build a style that fits your brand. 

### Routing profile 

The Kit includes the use of the Mapbox Directions API to display estimated travel distances and display driving routes to store locations. It’s also possible to modify the `MapActivity.java` file to use our cycling or walking directions. 

# Mapbox Kits

This is the first of several plug-and-play Kits that Mapbox will be releasing to reduce set-up time and make it easy for developers to get up and running with common mapping & location builds. If you have any feedback, questions, or suggestions for future Android Kits, open an [issue](https://github.com/mapbox/store-locator-android/issues) for us!
