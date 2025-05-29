# SmartFarm - Agricultural Management Mobile App 🌾📱

SmartFarm is an intelligent mobile application designed to help farmers manage their agricultural activities efficiently. The app offers interactive tools for managing farms, monitoring crops and animals, tracking yields, visualizing data on a map, and receiving smart notifications — all in one place.

## 🚀 Features

- 🔐 **Secure Authentication**
  - Register and log in with email verification and JWT authentication.
  
- 🧑‍🌾 **Farmer Profile**
  - Create and update personal farmer profiles with detailed information.

- 🌍 **Farm Management**
  - Add, edit, and delete multiple farms.
  - Track surface area, soil type, and location using OpenStreetMap integration.

- 🌱 **Crop & Animal Tracking**
  ![Cloud Animation](media/Animation - 1746052288224.gif)
  - Manage crops and animals per farm.
  - Monitor health status, quantity, and history.

- 📊 **Yield and Loss Tracking**
  - Calculate yield per hectare, monthly and annual statistics.
  - Track abnormal losses and total production per category.

- 📆 **Task Reminders & Health Alerts**
  - Get notified for health issues in animals/crops.
  - Receive smart reminders when no follow-up is done for 7+ days.

- 📡 **Weather Forecast**
  - View 5–7 day forecasts with animated weather UI.
  - Navigate between days and hours using custom SeekBar & buttons.

- 🗺️ **Interactive Map**
  - Draw and mark plots on the map.
  - Save polygonal zones, calculate surface, and show legends.
  - Drag markers and zoom for better interactivity.

- 🧠 **Integrated AI Assistant**
  - Ask agricultural questions and receive custom answers based on your farm data.
  
- 🧹 **Trash System (Recycle Bin)**
  - Swipe-to-delete features with a recycle bin to restore accidentally deleted data.

- 📚 **Help Center**
  - Built-in FAQ with common agricultural problems and solutions.

## 🛠️ Tech Stack

- **Frontend:** Kotlin, Android Studio
- **Backend:** Node.js (Express), PostgreSQL
- **Authentication:** JWT
- **Map:** OpenStreetMap (osmdroid)
- **Weather:** OpenWeatherMap API
- **AI Assistant:** Custom endpoint `/askAI`

## 📦 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/smartfarm.git
