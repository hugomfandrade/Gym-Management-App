# Gym Management App

This is the Android app for the Capstone project course in the Coursera Android App Development (AAD) Specialization. The Capstone started the week of Oct 16th, 2017 with the final submission due November 16th, 2017.

## Project Description
This Android app is designed for gym use and supports three types of users: Administrators, Gym Staff (Personal Trainers, Instructor) and Gym members. Each one has its own account and can log in to the app. The user will interact with different components of the app, depending on the user's credential.

### Administrators
The administrator manages the accounts of both the Gym staff and Gym members, which includes creating their accounts, assigning their credentials and editing/removing their information. The Administrator interacts with 4 user-facing interfaces: the (1) login activity, the (2) main activity where the administrator may choose the (3) add new account activity, or the (4) edit/remove existing account activity.

This version of the Android app only uses two of the 4 Android Components: the multiple Activities succinctly described above, and the Service which interacts with the Web Service.

### Gym Staff
The gym staff associates gym members to himself/herself and manages his/her gym members, which includes building workout plans, suggesting gym classes and checking their progress. The gym staff interacts with 6 user-facing interfaces: the (1) login activity, the (2) main activity where the gym staff may choose the (3) "associate gym members" activity, the (4) "check my gym members" activity, and after selecting a gym member the staff may choose the (5) "see member progress" activity or the (6) "build workout/suggest class" activity.

This version of the Android app only uses two of the 4 Android Components: the multiple Activities succinctly described above, and the Service which interacts with the Web Service.

### Gym member
The gym member can check the plans and classes suggested to him by the gym staff and report the progress by telling the workouts he performed and which suggestions were completed. The gym member interacts with 4 user-facing interfaces: the (1) login activity, the (2) main activity where the gym members may choose the (3) "check suggested workouts" activity, (4) may choose to check his/her workouts history or (5) may choose to report and save a workout that was just performed.

This version of the Android app uses the 4 Android Components: the multiple Activities succinctly described above, the ContentProvider in order to store data allowing the user to use the App offline, the BroadcastReceiver in order to remind the user of the suggested workout built by the staff, and the Service which interacts with the Web Service.

## Web Service
This Android app will be accessing the web service http://gym-management-app.azurewebsites.net/ throughout. This web service is a Microsoft Azure App service that enables to structurely store and access the data through a REST API.