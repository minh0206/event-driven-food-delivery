import { Alert, Box, Heading, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNotification, useWebSocket } from "@repo/shared/hooks";
import { useEffect } from "react";
import RestaurantOrderCard from "../components/RestaurantOrderCard";
import { useRestaurantOrders } from "../hooks/useRestaurantOrders";

const ORDER_PLACED_ENDPOINT = "/user/queue/order-placed";

const OrderManagementPage = () => {
  const { data: orders, isLoading, error, refetch } = useRestaurantOrders();
  const { isConnected, subscribe } = useWebSocket("http://localhost:8082/ws");
  const { permissionStatus, requestPermission, notify, isSupported } =
    useNotification();

  useEffect(() => {
    if (permissionStatus === "default") requestPermission();

    if (!isConnected) return;

    const subscription = subscribe(ORDER_PLACED_ENDPOINT, async () => {
      // Refetch orders to update the list
      await refetch();

      // Only attempt to notify if permission is granted
      if (permissionStatus === "granted") {
        notify("New order", {
          body: `You have a new order!`,
        });
      } else {
        // Prompt user to grant permission first
        alert("Please enable notification permission.");
      }
    });
    // Return a cleanup function to unsubscribe
    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
    };
  }, [isConnected, subscribe]);

  // Only render notification controls if the browser supports the API
  if (!isSupported) {
    return <p>Your browser does not support notifications.</p>;
  }

  if (isLoading) {
    return (
      <Box p={4}>
        <Spinner size="xl" />
        <Text mt={4}>Loading orders...</Text>
      </Box>
    );
  }

  if (error) {
    return (
      <Box p={4}>
        <Alert.Root status="error">
          <Alert.Indicator />
          <Alert.Content>Error loading orders: {error?.message}</Alert.Content>
        </Alert.Root>
      </Box>
    );
  }

  return (
    <>
      <div>
        {permissionStatus === "denied" && (
          <p>
            Notifications are blocked. Please enable them in your browser
            settings.
          </p>
        )}
      </div>
      <Box p={4}>
        <Heading as="h1" size="xl" mb={4}>
          Order Management
        </Heading>
        {orders ? (
          <VStack align="stretch">
            {orders.map((order) => (
              <RestaurantOrderCard key={order.orderId} order={order} />
            ))}
          </VStack>
        ) : (
          <Text>No orders found for this restaurant.</Text>
        )}
      </Box>
    </>
  );
};

export default OrderManagementPage;
