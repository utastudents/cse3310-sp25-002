class DisplayNotification {
  constructor() {
    this.notificationElement = document.getElementById("notification");
    this.timeout = null;
  }

  displayNotification(message, type = "info") {
    if (!this.notificationElement) {
      console.error("Notification element not found");
      return;
    }

    // Clear previous timeout if exists
    if (this.timeout) {
      clearTimeout(this.timeout);
      this.timeout = null;
    }

    // Set message and styling
    this.notificationElement.textContent = message;
    this.notificationElement.className = "notification-" + type;
    this.notificationElement.style.display = "block";

    // Auto-hide after 5 seconds
    this.timeout = setTimeout(() => {
      this.notificationElement.style.display = "none";
      this.timeout = null;
    }, 5000);
  }

  display(message, type = "info") {
    this.displayNotification(message, type);
  }
}