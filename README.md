# Event Lottery System Application

## Overview

The **Event Lottery System Application** is a mobile app designed to help people sign up for community center events that have high demand. The app uses a **lottery system** to fairly select participants, ensuring that everyone has an equal chance of attending, regardless of time constraints or accessibility issues.

Instead of constantly refreshing an application to get a spot, users can join a waiting list, and after a set registration period, the system randomly selects attendees for available spots. This approach makes it easier for individuals to participate in events, even if they have other responsibilities, such as work or disability.

---

## Features

- **Lottery-based Event Sign-Up:** Users can join a waiting list for an event, and after registration closes, a random selection process picks the attendees.
  
- **QR Code Scanning:** Event details can be accessed via QR codes, and users can join the waiting list by scanning the code on promotional material.

- **Firebase Integration:** Firebase is used for real-time data storage and updates, including event details, attendee lists, and registration status.

- **Multi-User Roles:** The app supports multiple user roles:
  - **Entrants:** Users who sign up for events.
  - **Organizers:** Event managers who post and manage events.
  - **Admins:** Entities that oversee the system and its infrastructure.

- **Image Upload:** Organizers can upload event posters for better visibility.

---

## How it Works

1. **Organizers create events**: They can specify details such as event name, description, schedule, available spots, and a registration deadline.
   
2. **Users join waiting lists**: Interested entrants can scan a QR code to register for the event. The waiting list is open for a defined period (e.g., one week).

3. **Lottery Selection**: Once the registration period ends, the system randomly selects participants for available spots.

4. **Notification and Confirmation**: Selected users are notified, and if they decline, another entrant is randomly chosen.

---

## Technology Stack

- **Android Studio Version 8.7.+**: The app is built using **Java 8+** for Android development.
- **Firebase**: Used for backend real-time data storage and authentication.
- **QR Code Libraries**: Libraries for generating and scanning QR codes.
- **Gradle 8.7.+**: For dependency management and building the app.
- **Junit and Javadoc**: Used for documentation and testing

---

## Getting Started

### Prerequisites

- **Android Studio** (latest version)
- **Firebase Account**: Set up Firebase in your project and enable Firebase services like Firestore and Firebase Authentication.
- **An Android device or emulator** to run the application.

### Steps to Run Locally

1. **Clone the repository**:

    ```bash
    git clone https://github.com/CMPUT301F24jigsaw/jigsaw-EventHub.git
    
    ```

2. **Open the project in Android Studio**:

    - Open **Android Studio**.
    - Select **Open an Existing Project** and choose the cloned repository folder.

3. **Set up Firebase**:

    - Go to the **Firebase Console** and create a new project.
    - Add **Firebase Firestore** and **Firebase Authentication** to your project.
    - Download the `google-services.json` file and place it in the `app` directory of your project.

4. **Sync your project**:

    - Ensure that your project is synced with Gradle by selecting **File** > **Sync Project with Gradle Files** in Android Studio.

5. **Run the app**:

    - Choose an **emulator** or connect a physical **Android device**.
    - Click the **Run** button to build and launch the app.


### Contributions
