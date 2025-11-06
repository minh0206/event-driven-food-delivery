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
import { OrderStatus } from "@repo/shared/models";
import { useEffect, useMemo, useState } from "react";
import RestaurantOrderCard from "../components/RestaurantOrderCard";
import { useRestaurantOrders } from "../hooks/useRestaurantOrders";

const ORDER_UPDATES_ENDPOINT = "/user/queue/order-updates";

const OrderManagementPage = () => {
  const { data: orders, isLoading, error, refetch } = useRestaurantOrders();

  const { isConnected, subscribe } = useWebSocket("http://localhost:8082/ws");
  const [activeTab, setActiveTab] = useState<string>("ongoing");

  const ongoingOrders = useMemo(
    () =>
      orders?.filter(
        (o) =>
          ![
            OrderStatus.DELIVERED,
            OrderStatus.CANCELLED,
            OrderStatus.REJECTED,
          ].includes(o.status)
      ) ?? [],
    [orders]
  );

  const historyOrders = useMemo(() => {
    if (activeTab !== "history") return [];
    return (
      orders?.filter((o) =>
        [
          OrderStatus.DELIVERED,
          OrderStatus.CANCELLED,
          OrderStatus.REJECTED,
        ].includes(o.status)
      ) ?? []
    );
  }, [orders, activeTab]);

  const renderHistoryContent = () => {
    if (isLoading) {
      return (
        <>
          <Spinner size="lg" />
          <Text mt={2}>Loading history...</Text>
        </>
      );
    }
    if (error) {
      return (
        <Alert.Root status="error">
          <Alert.Indicator />
          <Alert.Content>Error loading orders: {error?.message}</Alert.Content>
        </Alert.Root>
      );
    }
    if (historyOrders && historyOrders.length > 0) {
      return (
        <VStack align="stretch">
          {historyOrders.map((order) => (
            <RestaurantOrderCard key={order.orderId} order={order} />
          ))}
        </VStack>
      );
    }
    return <Text>No orders history.</Text>;
  };

  const renderOngoingContent = () => {
    if (isLoading) {
      return (
        <>
          <Spinner size="lg" />
          <Text mt={2}>Loading orders...</Text>
        </>
      );
    }
    if (error) {
      return (
        <Alert.Root status="error">
          <Alert.Indicator />
          <Alert.Content>Error loading orders: {error?.message}</Alert.Content>
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
      await refetch();
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
      <Tabs.Root
        lazyMount
        defaultValue="ongoing"
        onValueChange={(details) => setActiveTab(details.value)}
      >
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
