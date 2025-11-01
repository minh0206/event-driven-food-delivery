import { Card, Heading, List, Text } from "@chakra-ui/react";
import { useMenuItems } from "@repo/shared/hooks";
import { Order } from "@repo/shared/models";

const OrderCard = ({ order }: { order: Order }) => {
  const { data: menuItems } = useMenuItems(order.restaurantId);

  return (
    <Card.Root size="sm" shadow="md" borderWidth="1px" borderRadius="md">
      <Card.Header>
        <Card.Title fontSize="lg" fontWeight="bold">
          Order ID: {order.id}
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
              (x{item.quantity}) - ${item.price.toFixed(2)}
            </List.Item>
          ))}
        </List.Root>
      </Card.Body>
      <Card.Footer>
        <Text fontSize="medium" fontWeight="medium">
          Total: ${order.totalPrice.toFixed(2)}
        </Text>
      </Card.Footer>
    </Card.Root>
  );
};

export default OrderCard;
