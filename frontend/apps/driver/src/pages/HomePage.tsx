import { Box, Heading, Stack, Switch, Text, VStack } from "@chakra-ui/react";
import { CACHE_KEYS } from "@repo/shared/constants";
import { useWebSocket } from "@repo/shared/hooks";
import { OrderStatus } from "@repo/shared/models";
import { Toaster, toaster } from "@repo/ui/components";
import { useQueryClient } from "@tanstack/react-query";
import { useEffect } from "react";
import DriverOrderCard from "../components/DriverOrderCard";
import useDriverOrder from "../hooks/useDriverOrder";
import useDriverStatus from "../hooks/useDriverStatus";
import { useUpdateDriverOrder } from "../hooks/useUpdateDriverOrder";
import useUpdateDriverStatus from "../hooks/useUpdateDriverStatus";
import { DriverStatus } from "../models/DriverStatus";

const ORDER_UPDATES_ENDPOINT = "/user/queue/order-updates";

export const HomePage = () => {
  const { isConnected, subscribe } = useWebSocket("/ws/driver");
  const { data, refetch: refetchStatus } = useDriverStatus();
  const { data: driverOrder, refetch: refetchOrder, error } = useDriverOrder();
  const updateDriverStatus = useUpdateDriverStatus();
  const updateDriverOrder = useUpdateDriverOrder();
  const isAvailable = data?.status !== DriverStatus.OFFLINE;
  const queryClient = useQueryClient();

  const handleOnUpdateStatus = async (newStatus: OrderStatus) => {
    await updateDriverOrder.mutateAsync(newStatus);
    toaster.success({ title: "Order status updated!" });
    if (newStatus === OrderStatus.DELIVERED) {
      await queryClient.resetQueries({
        queryKey: CACHE_KEYS.DRIVER_ORDER,
      });
    }
  };

  useEffect(() => {
    if (!isConnected) return;

    const subscription = subscribe(ORDER_UPDATES_ENDPOINT, async (message) => {
      console.log("Order status updated:", message.body);
      // Refetch the order
      refetchOrder();
      refetchStatus();

      toaster.success({
        title: "Order status updated!",
      });
    });

    // Return a cleanup function to unsubscribe
    return () => subscription?.unsubscribe();
  }, [isConnected, subscribe]);

  return (
    <Box p={4}>
      <Heading as="h1" size="xl">
        Driver Home Page
      </Heading>
      <Text>Welcome to the driver application!</Text>
      <Stack direction="row" align="center" mt={4}>
        <Text minWidth="115px">Status: {data?.status}</Text>
        <Switch.Root
          disabled={data?.status === DriverStatus.ON_DELIVERY}
          checked={isAvailable}
          onCheckedChange={async (e) =>
            await updateDriverStatus.mutateAsync(
              e.checked ? DriverStatus.AVAILABLE : DriverStatus.OFFLINE
            )
          }
        >
          <Switch.HiddenInput />
          <Switch.Control>
            <Switch.Thumb />
          </Switch.Control>
          <Switch.Label srOnly>Toggle Driver Status</Switch.Label>
        </Switch.Root>
      </Stack>
      {/* Current Order Section */}
      {driverOrder && !error ? (
        <VStack align="stretch" mt={6} gap={4}>
          <Heading as="h2" size="lg">
            Current Delivery
          </Heading>
          <DriverOrderCard
            order={driverOrder}
            onUpdateStatus={handleOnUpdateStatus}
          />
        </VStack>
      ) : (
        <Text mt={6} color="gray.600">
          No active deliveries. Wait for an order to be assigned.
        </Text>
      )}
      <Toaster />
    </Box>
  );
};
