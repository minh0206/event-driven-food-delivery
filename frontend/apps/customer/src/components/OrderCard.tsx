import { Box, Heading, List, Text } from "@chakra-ui/react";
import { useMenuItems } from "@repo/shared/hooks";
import { Order } from "../models/Order";

const OrderCard = ({ order }: { order: Order }) => {
  const { data: menuItems } = useMenuItems(order.restaurantId);

  return (
    <Box my={2} p={4} shadow="md" borderWidth="1px" borderRadius="md">
      <Heading as="h2" size="md">
        Order ID: {order.id}
      </Heading>
      <Text>Status: {order.status}</Text>
      <Text>Total Price: ${order.totalPrice?.toFixed(2)}</Text>
      <Heading as="h3" size="sm" mt={2}>
        Items:
      </Heading>
      <List.Root pl={4}>
        {order.items?.map((item) => (
          <List.Item key={item.id}>
            {
              menuItems?.find((menuItem) => menuItem.id === item.menuItemId)
                ?.name
            }{" "}
            (x{item.quantity}) - ${item.price.toFixed(2)}
          </List.Item>
        ))}
      </List.Root>
    </Box>
  );
};

export default OrderCard;
