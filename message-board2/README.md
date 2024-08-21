## **MessageBoard Application**


### **Overview**

The MessageBoard application is a community-driven platform where users can join various interest-based communities, share content, interact with posts through comments and votes, and personalize their experience with a customized front page. The platform fosters engagement and discussions while ensuring quality and appropriate content through community moderation.


### **Features**



* User Registration and Login
* Community Creation and Membership
* Post Creation and Management
* Commenting on Posts
* Voting on Posts
* Personalized Front Page
* Community Moderation


### **Technologies Used**



* **Java**: Backend logic
* **JavaFX**: User interface
* **MySQL**: Database management


### **Getting Started**

To set up and run the MessageBoard application, follow these steps:


#### **Prerequisites**



* Java Development Kit (JDK)
* JavaFX SDK
* MySQL Server


#### **Setup Instructions**



1. **Clone the Repository**: 
    
    ```
    git clone https://github.com/uni-dos/message-board.git
    cd MessageBoard
    ```


2. **Set Up JavaFX**:
    * Ensure you have the JavaFX SDK installed.

Set up the VM options in your IDE to include the JavaFX libraries. For example: 

`--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`



    
3. **Database Setup**:
    * Install and run MySQL Server on your local machine.
    * Create a database named `messageBoard`.

Insert your MySQL credentials (username and password) in the `Main.java` file: 

```
String uName = "<your-username>";
String passwd = "<your-password>";
```


4. **Run the Application**:
    * Open the project in your IDE.
    * Run the `Main.java` file to start the application.


### **Contribution**


#### **Team Members and Contributions**



* **Robert Gonzalez**
    * Worked on sql scripts to build the database, login/sign up page, frontpage, front page post views, create post, and jdbc initilization steps. 
* **Zain Khan**
    * Edited the front end, helped desinged the database, and allowing the user to view Communities Joined. Worked on the Final report.
* **Davel Radindra**
    * Worked on user session handling, the community page and view, the post view with its relative comments, and the voting system that displays the total votes based on upvotes/downvotes.


