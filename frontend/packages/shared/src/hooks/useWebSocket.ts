// src/hooks/useWebSocket.ts
import {
  Client,
  IMessage,
  StompHeaders,
  StompSubscription,
} from "@stomp/stompjs";
import { useCallback, useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { useAuthStore } from "./useAuthStore";

export const useWebSocket = (url: string) => {
  const token = useAuthStore((state) => state.token);
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [isConnected, setIsConnected] = useState<boolean>(false);
  const [messages, setMessages] = useState<IMessage[]>([]);

  // Memoize the callback functions to prevent unnecessary re-renders
  const onConnect = useCallback(() => {
    setIsConnected(true);
    console.log("Connected to WebSocket: ", url);
  }, []);

  const onDisconnect = useCallback(() => {
    setIsConnected(false);
    console.log("Disconnected from WebSocket");
  }, []);

  const onError = useCallback((error: any) => {
    console.error("WebSocket Error:", error);
  }, []);

  useEffect(() => {
    if (!url) return () => {};

    if (!token) {
      console.error("JWT token is not available. Cannot connect.");
      return () => {};
    }

    const connectHeaders: StompHeaders = {
      Authorization: `Bearer ${token}`,
    };

    const client = new Client({
      webSocketFactory: () => new SockJS(url),
      connectHeaders,
      onConnect: onConnect,
      onDisconnect: onDisconnect,
      onStompError: onError,
      reconnectDelay: 5000,
    });

    client.activate();

    setStompClient(client);

    // Cleanup function to disconnect when the component unmounts
    return () => client.deactivate();
  }, [url, token, onConnect, onDisconnect, onError]);

  const subscribe = useCallback(
    (
      destination: string,
      callback: (message: IMessage) => void
    ): StompSubscription | undefined => {
      if (stompClient?.active) {
        // Return the subscription object so it can be used for unsubscribing
        return stompClient.subscribe(destination, (message) => {
          setMessages((prev) => [...prev, message]);
          callback(message);
        });
      }
      return undefined;
    },
    [stompClient]
  );

  return { isConnected, messages, subscribe };
};
