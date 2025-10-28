import { Alert, Box, Heading, Spinner, Text } from "@chakra-ui/react";
import OrderCard from "../components/OrderCard";
import { useCustomerOrders } from "../hooks/useCustomerOrders";

const OrderListPage = () => {
  const { data: orders, isLoading, error } = useCustomerOrders();

  if (isLoading) return <Spinner size="xl" />;
  if (error)
    return (
      <Alert.Root status="error">
        <Alert.Indicator />
        <Alert.Title>Error: {error.message}</Alert.Title>
      </Alert.Root>
    );

  return (
    <Box mx={6} my={2}>
      <Heading as="h1" size="xl" mb={4}>
        Your Orders
      </Heading>
      {orders?.length === 0 ? (
        <Text>No orders found.</Text>
      ) : (
        <Box>
          {orders?.map((order) => (
            <OrderCard key={order.id} order={order} />
          ))}
        </Box>
      )}
    </Box>
  );
};

export default OrderListPage;
