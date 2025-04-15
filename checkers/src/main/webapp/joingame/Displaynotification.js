class Displaynotification {
    constructor() {
      this.notificationBox = document.getElementById("notification");
    }
  
    addNotification(message) {
      const time = new Date().toLocaleTimeString();
      const entry = document.createElement("p");
      entry.innerHTML = `<strong>${time}</strong>: ${message}`;
      this.notificationBox.appendChild(entry);
    }
  }
  