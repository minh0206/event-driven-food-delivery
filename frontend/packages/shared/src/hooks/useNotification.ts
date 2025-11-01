import { useCallback, useEffect, useState } from "react";

// A type for notification options, which can be extended if needed
type NotificationOptions = {
  body?: string;
  icon?: string;
  tag?: string;
  silent?: boolean;
};

// A custom hook for the browser Notifications API
export const useNotification = () => {
  const [permissionStatus, setPermissionStatus] =
    useState<NotificationPermission>("default");

  useEffect(() => {
    // Check if the Notifications API is supported by the browser
    if (!isSupported) {
      console.error("This browser does not support desktop notifications.");
      return;
    }

    // Get the initial permission status
    setPermissionStatus(Notification.permission);
  }, []);

  // Function to request permission from the user
  const requestPermission = useCallback(async () => {
    if (Notification.permission === "granted") {
      setPermissionStatus("granted");
      return;
    }

    // Check for previous permission status before requesting again
    if (Notification.permission !== "denied") {
      const permission = await Notification.requestPermission();
      setPermissionStatus(permission);
    }
  }, []);

  // Function to show a notification
  const notify = useCallback(
    (title: string, options: NotificationOptions = {}) => {
      // Check if permission is granted before attempting to show a notification
      if (permissionStatus === "granted") {
        return new Notification(title, options);
      }
    },
    [permissionStatus]
  );

  // A helper function to check if notifications are supported
  const isSupported = "Notification" in globalThis;

  return {
    permissionStatus,
    requestPermission,
    notify,
    isSupported,
  };
};
