import { Alert, Box, Heading, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNotification, useWebSocket } from "@repo/shared/hooks";
import { Toaster, toaster } from "@repo/ui/components";
import { useEffect } from "react";
import OrderCard from "../components/OrderCard";
import { useCustomerOrders } from "../hooks/useCustomerOrders";

const ORDER_UPDATES_ENDPOINT = "/user/queue/order-updates";

const OrderListPage = () => {
  const { data: orders, isLoading, error, refetch } = useCustomerOrders();
  const { isConnected, subscribe } = useWebSocket("http://localhost:8083/ws");
  const { permissionStatus, requestPermission, notify, isSupported } =
    useNotification();

  useEffect(() => {
    if (permissionStatus === "default") requestPermission();

    if (!isConnected) return;

    const subscription = subscribe(ORDER_UPDATES_ENDPOINT, async () => {
      // Refetch orders to update the list
      await refetch();

      // Only attempt to notify if permission is granted
      if (permissionStatus === "granted") {
        notify("Order updated!", {
          body: `Your order has been updated`,
        });
        toaster.success({
          title: "Order updated!",
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
    <Box p={4}>
      <Heading as="h1" size="xl" mb={4}>
        Your Orders
      </Heading>
      {orders ? (
        <VStack align="stretch">
          {orders.map((order) => (
            <OrderCard key={order.id} order={order} />
          ))}
        </VStack>
      ) : (
        <Text>No orders found.</Text>
      )}
      <Toaster />
    </Box>
  );
};

export default OrderListPage;
