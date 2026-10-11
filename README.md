# Digital Notice Board Management System

A Java-based Digital Notice Board Management System integrated with MySQL. This project demonstrates how a Java application can interact with a relational database using JDBC.

## About the Project

The Digital Notice Board Management System provides a digital approach to managing notices. It uses Java for application logic and MySQL for database management.

## Technologies Used

* **Programming Language:** Java
* **Database:** MySQL
* **Database Connectivity:** JDBC
* **JDBC Driver:** MySQL Connector/J
* **IDE:** Eclipse
* **Version Control:** Git and GitHub

## Features

* Java-based notice board application
* MySQL database integration
* JDBC connectivity between Java and MySQL
* SQL script for database setup

*Additional features depend on the functionality implemented in the source code.*

## Project Structure

```text
DigitalNoticeBoard/
├── src/
│   ├── DigitalNoticeBoardApp.java
│   ├── Notice.java
│   ├── NoticeClientApp.java
│   └── NoticeServer.java
├── database/
│   └── database.sql
├── lib/
│   └── mysql-connector-j-26.7.0.jar
├── .classpath
├── .project
├── .gitignore
└── README.md
```

## Prerequisites

Before running the project, install:

1. Java Development Kit (JDK)
2. Eclipse IDE
3. MySQL Server
4. MySQL Connector/J (already included in the repository's `lib` folder)

## Getting Started

### Step 1: Download the Project

Clone the repository using Git:

```bash
git clone https://github.com/bhoomisharma3009-ui/Notice-board.git
```

Alternatively, download the repository as a ZIP file from GitHub and extract it.

### Step 2: Import the Project into Eclipse

1. Open Eclipse.
2. Select **File → Import**.
3. Select **General → Existing Projects into Workspace**.
4. Browse to the extracted project folder.
5. Select the project and click **Finish**.

### Step 3: Configure the MySQL Database

The SQL script contains the commands required to create the database and tables.

1. Start your MySQL server.
2. Open MySQL Workbench or MySQL Command Line Client.
3. Open the SQL script located in the project's `database` folder.
4. Execute the SQL statements.
5. Verify that the required database and tables have been created.

**Important:** The SQL file is included in the GitHub repository, so you do not need to create another copy. However, you must execute the script on your own MySQL server to set up your local database.

If the SQL script does not create a database automatically, create or select the required database before executing the table-creation statements.

### Step 4: Configure MySQL Connector/J

The repository includes the JDBC driver in the `lib` folder.

If Eclipse does not recognize the JAR automatically:

1. Right-click the project in Package Explorer.
2. Select **Build Path → Configure Build Path**.
3. Open the **Libraries** tab.
4. Select **Classpath**, if applicable.
5. Click **Add JARs**.
6. Expand the project and open the `lib` folder.
7. Select `mysql-connector-j-26.7.0.jar`.
8. Click **Apply and Close**.

The JAR enables Java to communicate with MySQL through JDBC.

### Step 5: Configure the Database Connection

Open the Java source file containing the database connection code.

Check the JDBC connection URL, database name, MySQL username, and password.

For example, a JDBC connection may look like this:

```java
String url = "jdbc:mysql://localhost:3306/your_database";
String username = "your_username";
String password = "your_password";
```

Replace these example values with the details for your own MySQL setup.

**Security note:** Do not commit your real database password to GitHub.

### Step 6: Run the Application

1. Ensure MySQL is running.
2. Verify that the database and tables have been created.
3. Confirm that the JDBC driver is configured in Eclipse.
4. Open the Java class containing the application's `main()` method.
5. Right-click the file and select **Run As → Java Application**.

If the application uses separate server and client programs, start the server before the client.

## Troubleshooting

### JDBC Driver Error

If you receive a driver-related error, check that the MySQL Connector/J JAR is configured in the Eclipse Build Path.

### Database Connection Error

Verify that:

* MySQL Server is running.
* The JDBC URL contains the correct database name and port.
* Your MySQL username and password are correct.
* The required database and tables exist.

### Table or Database Not Found

Execute the SQL setup script and check that its database and table names match those used by the Java source code.

## Repository

GitHub: https://github.com/bhoomisharma3009-ui/Notice-board

## Future Improvements

* Improve the user interface.
* Extend notice management functionality.
* Improve input validation and error handling.
* Extend database functionality as needed.
