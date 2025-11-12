import {
  Alert,
  Box,
  Heading,
  Spinner,
  Tabs,
  Text,
  VStack,
} from "@chakra-ui/react";
import { useWebSocket } from "@repo/shared/hooks";
import { useEffect } from "react";
import HistoryRestaurantOrderCard from "../components/HistoryRestaurantOrderCard";
import RestaurantOrderCard from "../components/RestaurantOrderCard";
import { useHistoryRestaurantOrders } from "../hooks/useHistoryRestaurantOrders";
import { useOngoingRestaurantOrders } from "../hooks/useOngoingRestaurantOrders";

const ORDER_UPDATES_ENDPOINT = "/user/queue/order-updates";

const OrderManagementPage = () => {
  const {
    data: ongoingOrders,
    isLoading: isLoadingOngoing,
    error: errorOngoing,
    refetch: refetchOngoing,
  } = useOngoingRestaurantOrders();

  const {
    data: historyOrders,
    isLoading: isLoadingHistory,
    error: errorHistory,
  } = useHistoryRestaurantOrders();

  const { isConnected, subscribe } = useWebSocket("http://localhost:8082/ws");

  const renderHistoryContent = () => {
    if (isLoadingHistory) {
      return (
        <>
          <Spinner size="lg" />
          <Text mt={2}>Loading history...</Text>
        </>
      );
    }
    if (errorHistory) {
      return (
        <Alert.Root status="error">
          <Alert.Indicator />
          <Alert.Content>
            Error loading orders: {errorHistory?.message}
          </Alert.Content>
        </Alert.Root>
      );
    }
    if (historyOrders && historyOrders.length > 0) {
      return (
        <VStack align="stretch">
          {historyOrders.map((order) => (
            <HistoryRestaurantOrderCard key={order.orderId} order={order} />
          ))}
        </VStack>
      );
    }
    return <Text>No orders history.</Text>;
  };

  const renderOngoingContent = () => {
    if (isLoadingOngoing) {
      return (
        <>
          <Spinner size="lg" />
          <Text mt={2}>Loading orders...</Text>
        </>
      );
    }
    if (errorOngoing) {
      return (
        <Alert.Root status="error">
          <Alert.Indicator />
          <Alert.Content>
            Error loading orders: {errorOngoing?.message}
          </Alert.Content>
        </Alert.Root>
      );
    }
    if (ongoingOrders && ongoingOrders.length > 0) {
      return (
        <VStack align="stretch">
          {ongoingOrders.map((order) => (
            <RestaurantOrderCard key={order.orderId} order={order} />
          ))}
        </VStack>
      );
    }
    return <Text>No ongoing orders.</Text>;
  };

  useEffect(() => {
    if (!isConnected) return;

    const subscription = subscribe(ORDER_UPDATES_ENDPOINT, async (message) => {
      // Refetch orders to update the list
      await refetchOngoing();
      console.log("Order updated", message.body);
    });
    // Return a cleanup function to unsubscribe
    return () => subscription?.unsubscribe();
  }, [isConnected, subscribe]);

  return (
    <Box p={4}>
      <Heading as="h1" size="xl" mb={4}>
        Order Management
      </Heading>
      <Tabs.Root lazyMount unmountOnExit defaultValue="ongoing">
        <Tabs.List>
          <Tabs.Trigger value="ongoing">Ongoing</Tabs.Trigger>
          <Tabs.Trigger value="history">History</Tabs.Trigger>
        </Tabs.List>
        <Tabs.Content value="ongoing">{renderOngoingContent()}</Tabs.Content>
        <Tabs.Content value="history">{renderHistoryContent()}</Tabs.Content>
      </Tabs.Root>
    </Box>
  );
};

export default OrderManagementPage;
