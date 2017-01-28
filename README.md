# Popular Movies
Android app that displays a list of movies sorted by popularity, rating, and user's favorites. These options are located in Settings.
When the user selects a movie, it displays details such as rating average, year of release, trailers available to watch, reviews, and an option to add that movie to favorites. The user is also able to share the first trailer with other apps.
Data is synchronized with the server every 12 hours.

## Configuration
The app retrieves the movie data from [The Movie DB](https://www.themoviedb.org/documentation/api), and it requires your API key (which you can generate on [The Movie DB](https://www.themoviedb.org/documentation/api)) in order to run it. Once you obtain an API key, replace MyMoviesApiKey with your API key in the build.gradle file.

    buildTypes.each {
      it.buildConfigField 'String', 'MOVIES_API_KEY', MyMoviesApiKey
    }

## Demo
[Popular Movies Video](https://youtu.be/ReORb-pKp8M)

## Libraries used
* [Picasso](https://github.com/square/picasso)
* [Firebase JobDispatcher](https://github.com/firebase/firebase-jobdispatcher-android)
