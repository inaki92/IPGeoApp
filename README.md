# Project Title

Geolocation IP Address code assessment

## Getting Started

This project is a location app that retrieves the current  location for  a given IP Address.
1. It has the capability to search by IP Address and it will return you to the actual location.
2. When it is in offline mode it will retrieve the location for the given IP Address if it has been saved before in the database
3. When a location is found in the database it will retrieve the data from it instead of making a network call.
4. If the location in DB is older than 5 minutes then it will make a new network call to retrieve the latest location from the server and then save it into the DB.
5. The entry point should be a valid IP Address, otherwise it will not allow you to do the search and an error will be displayed.
6. When clicking the text "see map..." it will trigger an  intent to open the maps application on your device.

## Database schema

<img width="249" alt="Screenshot 2023-08-22 at 1 10 50 PM" src="https://github.com/inaki92/IPGeoApp/assets/45763109/5ff05d42-dc98-462e-b896-eacac75f5e78">


## Built With

- The UI was built using a single activity with a search field and button, the result will appear in a card view.
- The architecture followed was MVVM + clean architecture
- Network connection was made by using Retrofit and OkHttp with force cache implemented
- The dependency injection framework used was Hilt from Jetpack
- Asynchronous operations were made in the app using Kotlin coroutines and Kotlin flows
- For the unit testing libraries such as mockK, Truth assertions, and Junit were used

## Future roadmap

- Functionality to retrieve the current IP Address automatically and display location based on it
- Most frequent IP Addresses searched so you can check the location in a list

## Author

  - Inaki Lizarraga
