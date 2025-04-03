class Displaynotification {
    constructor() {
        this.notifications = []; // stores all notifications
    }

    // adds a new notification message
    addNotification(message) {
        const timestamp = new Date().toLocaleTimeString(); // this is done to get the time for the notification
        const notification = `[${timestamp}] ${message}`;
        this.notifications.push(notification);

        // display the notification in the console
        this.displayNotification(notification);
    }

    // this displays a notification in the console
    displayNotification(notification) {
        console.log(notification);
    }

    // return the notifications
    getNotifications() {
        return this.notifications;
    }

}