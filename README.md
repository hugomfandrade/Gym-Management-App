# Gym Management App

This is the Android app for the Capstone project course in the Coursera Android App Development (AAD) Specialization. The Capstone started the week of Oct 16th, 2017 with the final submission due November 16th, 2017.

## Project Description

This Android app is designed for gym use and supports three types of users: Administrators, Gym Staff (Personal Trainers, Instructor) and Gym members. Each one has its own account and can log in to the app. The user will interact with different components of the app, depending on the user's credential.

This app can be watched [here](https://www.youtube.com/playlist?list=PLuERM-U4AtnnfP6aC1H5wwm5zGNTV-5fn).

### Administrators

The administrator manages the accounts of both the Gym staff and Gym members, which includes creating their accounts, assigning their credentials and editing/removing their information. The Administrator interacts with 8 user-facing interfaces: 
* login activity
* main activity, where the administrator may choose
	* check existing gym members and add new gym member activitiies
	* check existing gym staff and add new gym staff activitiies
    * check existing exercises and add new exercise activities

This version of the Android app only uses three of the 4 Android Components: the multiple Activities succinctly described above, the BroadcastReceiver in order to send the data retrieved from the web service to the activity, and the Service which interacts with the Web Service.

This version of the app can be watched [here](https://www.youtube.com/watch?v=Zy-YvNP-Uis).

### Gym Staff

The gym staff associates gym members to himself/herself and manages his/her gym members, which includes building workout plans, suggesting gym classes and checking their progress. The gym staff interacts with 6 user-facing interfaces: 
* login activity, 
* main activity where the gym staff checks the list of the gym members that are associated with him and the gym staff may choose to
    * associate to other existing gym members
	* select a gym member and check the history of exercise plans made. Here, the gym staff may choose to
		* select a exercise plan and see it in detail 
		* build and suggest a exercise plan to the selected gym member

This version of the Android app only uses three of the 4 Android Components: the multiple Activities succinctly described above, the BroadcastReceiver in order to send the data retrieved from the web service to the activity, and the Service which interacts with the Web Service.

This version of the app can be watched [here](https://www.youtube.com/watch?v=7yiUZmk3nMI).

### Gym Member

The gym member can check the plans and classes suggested to him by the gym staff and report the progress by telling the workouts he performed and which suggestions were completed. The gym member interacts with 9 user-facing interfaces: 
* login activity
* main activity where the gym members may choose to
	* check the list of the gym staff that are associated with him
	* check the list of suggested exercise plans, and the gym member may
        * select a exercise plan and see it in detail, where he may accept or dismiss the suggestion
    * build an exercise planand add it to the history
	* check the history of exercise plans made, and see them in detail

This version of the Android app uses the 4 Android Components: the multiple Activities succinctly described above, the ContentProvider in order to store data allowing the user to use the App offline, the BroadcastReceiver in order to send the data retrieved from the web service to the activity, and the Service which interacts with the Web Service.

This version of the app can be watched [here](https://www.youtube.com/watch?v=KJbYrSFWSa8) and [here](https://www.youtube.com/watch?v=vXK76ssiSIY).

## Web Service

This Android app will be accessing the web service http://gym-management-app.azurewebsites.net/ throughout. This web service is a Microsoft Azure App service that hosts an SQL Database and enables to structurely store and access the data through a REST API. In this service, the backend language used is Node.js.

### Data Structure

The figure displays the Relational Model Diagram that represents how the data of this app is structured.

![relational model diagram](/img/gymapp_diagram.png?raw=true)

Succintly, the structured data is described as having:

* Gym **User**(s) which can have **Admin**, **Staff** or **Member** credential
* **WaitingUser**(s) which are temporary gym users created by the **Admin** that are waiting to sign up
* **StaffMember**(s) which are associations between gym members and gym staff
* **ExercisePlanRecord**(s) which are built by the gym members and have multiple **ExerciseSet**(s)
* **ExerciseSet**(s) which are described by an **Exercise** and a list of **ExerciseRecord**
* **ExerciseRecord**(s) which are described by the number of repetitions that the **Exercise** of the parent **ExerciseSet** was done.
* **Exercise** which are built by the **Admin**
* **ExercisePlanRecordSuggested**(s) which are built by the gym staff for a specific gym member and have the samre child structure as the **ExercisePlanRecordSuggested**(s)

### REST APIs

Acccording to data structure and the design of the Android App, the following RESTful APIs are performed:
* **User**: 
    * GET: by Admin, Staff and Member
    * POST: by Admin, Staff, Member and Anonymous (to login and signup)
* **User**/(id): 
    * GET: by Admin, Staff and Member
* **WaitingUser**:
    * GET: by Anonymous (to signup)
    * POST: by Admin
* **Admin**: 
    * GET: by Admin
* **Staff**/(id): 
    * GET: by Admin, Staff and Member
* **Member**/(id): 
    * GET: by Admin, Staff and Member
* **StaffMember**:
    * GET: by Staff and Member
    * POST: by Staff
* **ExercisePlanRecord**:
    * GET: by Staff and Member
* **ExercisePlanRecord**/(id):
    * PUT: by Member
* **ExerciseSet**:
    * GET: by Staff and Member
* **ExerciseSet**/(id):
    * PUT: by Staff and Member
* **ExerciseRecord**:
    * GET: by Staff and Member
* **ExerciseRecord**/(id):
    * PUT: by Staff and Member
* **Exercise**:
    * GET: by Admin, Staff and Member
* **Exercise**/(id):
    * GET: by Staff and Member
    * PUT: by Admin
* **ExercisePlanRecordSuggested**:
    * GET: by Member
* **ExercisePlanRecordSuggested**/(id):
    * PUT: by Staff
    * DELETE: by Member

### Server side code

This app required the implementation of a few features in the server side, particularly:

* Custom login authentication
* Generating a 10-character code for the WaitingUser 
* Signup

These features were implemented using Node.js and the code can be found [here](https://gitlab.com/hugomfandrade/gym-management-app/blob/master/server/).