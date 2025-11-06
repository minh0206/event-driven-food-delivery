import { Button, Card, Heading, List } from "@chakra-ui/react";
import { useAuthStore, useMenuItems } from "@repo/shared/hooks";
import { RestaurantOrder } from "@repo/shared/models";
import { Toaster, toaster } from "@repo/ui/components";
import { OrderStatus } from "../../../../packages/shared/src/models/OrderStatus";
import { useUpdateRestaurantOrder } from "../hooks/useUpdateRestaurantOrder";

const RestaurantOrderCard = ({ order }: { order: RestaurantOrder }) => {
  const { user } = useAuthStore();
  const { data: menuItems } = useMenuItems(user!.restaurantId!);
  const updateOrder = useUpdateRestaurantOrder();

  const handleUpdateStatus = async (status: OrderStatus) => {
    try {
      await updateOrder.mutateAsync({ ...order, status });
      toaster.success({ title: `Order status updated to ${status}` });
    } catch (error) {
      console.error("Error updating order status:", error);
      toaster.error({ title: "Error updating order status" });
    }
  };

  return (
    <>
      <Card.Root size="sm" shadow="md" borderWidth="1px" borderRadius="md">
        <Card.Header>
          <Card.Title fontSize="lg" fontWeight="bold">
            Order ID: {order.orderId}
          </Card.Title>
          <Card.Description>Status: {order.status}</Card.Description>
        </Card.Header>

        <Card.Body>
          <Heading as="h3" size="sm" mt={2}>
            Items:
          </Heading>
          <List.Root as="ol" pl={4}>
            {order.items?.map((item) => (
              <List.Item key={item.menuItemId}>
                {
                  menuItems?.find((menuItem) => menuItem.id === item.menuItemId)
                    ?.name
                }{" "}
                (x{item.quantity})
              </List.Item>
            ))}
          </List.Root>
        </Card.Body>

        <Card.Footer justifyContent="flex-start">
          {order.status === OrderStatus.PENDING && (
            <>
              <Button
                variant="solid"
                colorPalette="red"
                onClick={() => handleUpdateStatus(OrderStatus.REJECTED)}
              >
                Reject
              </Button>
              <Button
                variant="solid"
                colorPalette="green"
                onClick={() => handleUpdateStatus(OrderStatus.ACCEPTED)}
              >
                Accept
              </Button>
            </>
          )}
          {order.status === OrderStatus.ACCEPTED && (
            <Button
              variant="solid"
              colorPalette="green"
              onClick={() => handleUpdateStatus(OrderStatus.READY_FOR_PICKUP)}
            >
              Ready for Pickup
            </Button>
          )}
        </Card.Footer>
      </Card.Root>
      <Toaster />
    </>
  );
};

export default RestaurantOrderCard;
