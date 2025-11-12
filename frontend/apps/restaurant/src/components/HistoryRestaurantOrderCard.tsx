import { Card, Heading, List } from "@chakra-ui/react";
import { useAuthStore, useMenuItems } from "@repo/shared/hooks";
import { HistoryRestaurantOrder } from "@repo/shared/models";
import { Toaster } from "@repo/ui/components";

const HistoryRestaurantOrderCard = ({
  order,
}: {
  order: HistoryRestaurantOrder;
}) => {
  const { user } = useAuthStore();
  const { data: menuItems } = useMenuItems(user!.restaurantId!);

  return (
    <>
      <Card.Root size="sm" shadow="md" borderWidth="1px" borderRadius="md">
        <Card.Header>
          <Card.Title fontSize="lg" fontWeight="bold">
            Order ID: {order.orderId}
          </Card.Title>
          <Card.Description>Status: {order.finalStatus}</Card.Description>
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
          {/* No action buttons for historical orders */}
        </Card.Footer>
      </Card.Root>
      <Toaster />
    </>
  );
};

export default HistoryRestaurantOrderCard;
