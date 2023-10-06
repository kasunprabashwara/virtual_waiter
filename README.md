
# Virtual Waiter Android App

## Overview

Virtual Waiter is a pivotal module within the Hotel and Restaurant Management Information System (MIS), a comprehensive project consisting of four interconnected modules. This Android application is designed to elevate the dining experience by digitizing the ordering process in a restaurant. Developed using Android Studio and Java, the app seamlessly integrates with Firebase as the backend database, ensuring real-time communication and efficient data management.

### Explore Our Other Modules
Feel free to explore our other modules for a comprehensive Hotel and Restaurant MIS experience! 🏨🍽️
- [Restaurant Order Manager App](https://github.com/thiva-k/Restaurant-Order-Manager)
- [Web Front End for Customers](https://github.com/thiva-k/Hotel-Web-App)
- [Hotel Management App](https://github.com/Wathmiv/Hotel-Management)

## Features

### 1. Table Configuration
- The system allows the tablet to be configured with the table number where it is placed.
- Waiters can configure the tablet to the last used table number or change it using their login credentials.

### 2. Table Status Differentiation
- Customers can differentiate between booked and available tables.
- Pre-booked tables show a booked screen, while available tables display the food menu.

### 3. Menu Display
- The system displays the menu, available items, and offers on the tablet.
- Menu items are categorized for easy navigation (appetizers, main courses, desserts, beverages).
![Untitled](https://github.com/kasunprabashwara/virtual_waiter/assets/115882176/e33cc850-6336-4260-89d2-168d3ee06423)

### 4. Order Placement
- Customers can place orders in two ways:
  - **Virtual Waiter Interface:** Customers can tap on item cards, add details, and confirm the order.
  - **Through a Waiter:** Waiters, equipped with tablets, can add orders by providing table numbers and food items.

### 5. Multiple Orders in One Session
- Customers can select and place multiple orders in a single session until they decide to check out.

### 6. Real-time Order Updates
- The system provides real-time updates on the status of each order:
  - Ordered
  - Preparing
  - Prepared
  - Delivering
  - Delivered

### 7. Database Updates
- The system updates the database when a customer places an order.
- Database updates occur when chefs update the order status, reflecting changes in real-time.

### 8. Session Management
- Customers can end the session by tapping the checkout button.
- They can leave feedback, rate their dining experience (1-5), and write a review if desired.
- The session automatically ends if there is no user input for 1 minute, allowing the app to reset for the next customer.

## Installation

1. Clone the repository.
   ```bash
   git clone https://github.com/your-username/virtual-waiter-app.git
   ```

2. Open the project in Android Studio.

3. Configure Firebase:
   - Create a Firebase project and add the configuration file to the app.
   - Set up Firebase Authentication for waiter login.

4. Build and run the app on an Android device or emulator.

Feel free to reach out if you have any questions or issues! Happy dining with Virtual Waiter! 🍽️
