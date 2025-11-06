import {
  Box,
  Button,
  Card,
  Heading,
  List,
  Stack,
  Text,
} from "@chakra-ui/react";
import { useMenuItems } from "@repo/shared/hooks";
import { OrderStatus } from "@repo/shared/models";
import { DriverOrder } from "../models/DriverOrder";

interface ButtonProps {
  disabled?: boolean;
  colorPalette?: string;
  text: string;
  onClick?: () => void;
}

const DriverOrderCard = ({
  order,
  onUpdateStatus,
}: {
  order: DriverOrder;
  onUpdateStatus?: (newStatus: OrderStatus) => void;
}) => {
  const getButtonProps = (): ButtonProps => {
    switch (order.status) {
      case OrderStatus.READY_FOR_PICKUP:
        return {
          colorPalette: "blue",
          text: "Start Delivery",
          onClick: () => onUpdateStatus?.(OrderStatus.IN_TRANSIT),
        };
      case OrderStatus.IN_TRANSIT:
        return {
          colorPalette: "green",
          text: "Mark as Delivered",
          onClick: () => onUpdateStatus?.(OrderStatus.DELIVERED),
        };
      default:
        return {
          disabled: true,
          text: "Order is being processed",
        };
    }
  };

  const { data: menuItems, isLoading } = useMenuItems(order.restaurantId);

  if (isLoading) {
    return <Text>Loading order details...</Text>;
  }

  return (
    <Card.Root
      size="sm"
      shadow="md"
      borderWidth="1px"
      borderRadius="md"
      w="100%"
    >
      <Card.Header>
        <Card.Title fontSize="lg" fontWeight="bold">
          Order ID: {order.id}
        </Card.Title>
        <Card.Description>Status: {order.status}</Card.Description>
      </Card.Header>

      <Card.Body>
        <Stack gap={4}>
          <Box>
            <Heading as="h3" size="sm" mb={2}>
              Customer ID: {order.customerId}
            </Heading>
            <Text fontSize="sm" color="gray.600">
              Restaurant ID: {order.restaurantId}
            </Text>
          </Box>

          <Box>
            <Heading as="h3" size="sm" mt={2}>
              Items:
            </Heading>
            <List.Root as="ol" pl={4}>
              {order.items?.map((item) => (
                <List.Item key={item.menuItemId}>
                  {
                    menuItems?.find(
                      (menuItem) => menuItem.id === item.menuItemId
                    )?.name
                  }{" "}
                  (x{item.quantity}) - ${item.price.toFixed(2)}
                </List.Item>
              ))}
            </List.Root>
          </Box>

          <Text fontSize="medium" fontWeight="medium">
            Total: ${order.totalPrice.toFixed(2)}
          </Text>
        </Stack>
      </Card.Body>

      <Card.Footer justifyContent="flex-start">
        <Button
          size="sm"
          disabled={getButtonProps()?.disabled}
          colorPalette={getButtonProps()?.colorPalette}
          onClick={getButtonProps()?.onClick}
        >
          {getButtonProps().text}
        </Button>
      </Card.Footer>
    </Card.Root>
  );
};

export default DriverOrderCard;
