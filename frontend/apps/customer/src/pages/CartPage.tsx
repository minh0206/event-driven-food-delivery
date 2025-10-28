import {
  Box,
  Button,
  Container,
  EmptyState,
  Heading,
  HStack,
  Separator,
  Show,
  Stack,
  Text,
  VStack,
} from "@chakra-ui/react";
import { toaster, Toaster } from "@repo/ui/components";
import { LuShoppingCart } from "react-icons/lu";

import { useRestaurant } from "@repo/shared/hooks";
import { orderService } from "@repo/shared/services";
import CartItemCard from "../components/CartItemCard";
import { useCartStore } from "../stores/cartStore";

const CartPage = () => {
  const { items: cartItems, clearCart } = useCartStore();
  const restaurantId = cartItems[0]?.restaurantId;
  const { data: restaurant } = useRestaurant(restaurantId);

  const subtotal = cartItems.reduce(
    (total, item) => total + item.price * item.quantity,
    0
  );
  const shipping = 0.1 * subtotal; // 10% shipping fee
  const total = subtotal + shipping;

  const handlePlaceOrder = () => {
    // Place order logic
    orderService
      .createOrder({
        restaurantId,
        items: cartItems.map((item) => ({
          menuItemId: item.id,
          quantity: item.quantity,
          price: item.price,
        })),
      })
      .then((response) => {
        toaster.success({
          title: "Order placed successfully!",
        });
        console.log("Order placed successfully:", response);
        clearCart();
      })
      .catch((error) => {
        toaster.error({
          title: "Failed to place order. Please try again.",
        });
        console.error("Error placing order:", error);
      });
  };

  return (
    <Container maxW="container.lg" py={8} px={{ base: 4, md: 8 }}>
      <Heading as="h1" fontSize="2xl" mb={6}>
        Cart
      </Heading>

      <Stack
        direction={{ base: "column", md: "row" }}
        margin={{ base: 6, md: 8 }}
        align="flex-start"
      >
        <VStack
          margin={4}
          align="stretch"
          w={{ base: "100%", md: "70%" }}
          bg="white"
          p={6}
          borderRadius="md"
          boxShadow="sm"
        >
          <Show
            when={cartItems.length > 0}
            fallback={
              <EmptyState.Root>
                <EmptyState.Content>
                  <EmptyState.Indicator>
                    <LuShoppingCart />
                  </EmptyState.Indicator>
                  <VStack textAlign="center">
                    <EmptyState.Title>Your cart is empty</EmptyState.Title>
                    <EmptyState.Description>
                      Explore our restaurants and add items to your cart
                    </EmptyState.Description>
                  </VStack>
                </EmptyState.Content>
              </EmptyState.Root>
            }
          >
            <Heading as="h2" fontSize="xl" mb={5}>
              {restaurant?.name}
            </Heading>
            {cartItems.map((item) => (
              <Box key={item.id}>
                <CartItemCard item={item} />

                <Show when={item.id !== cartItems[cartItems.length - 1].id}>
                  <Separator my={4} />
                </Show>
              </Box>
            ))}
          </Show>
        </VStack>

        <VStack
          margin={4}
          align="stretch"
          w={{ base: "100%", md: "30%" }}
          bg="white"
          p={6}
          borderRadius="md"
          boxShadow="sm"
          position="sticky"
          top="20px"
        >
          <Heading as="h2" fontSize="xl" mb={2}>
            Order Summary
          </Heading>
          <HStack justify="space-between">
            <Text>Subtotal</Text>
            <Text>${subtotal.toFixed(2)}</Text>
          </HStack>
          <HStack justify="space-between">
            <Text>Shipping</Text>
            <Text color="blue.500" fontWeight="medium">
              {shipping === 0 ? "Free" : `$${shipping.toFixed(2)}`}
            </Text>
          </HStack>
          <Separator />
          <HStack justify="space-between">
            <Text fontWeight="bold">Total</Text>
            <Text fontWeight="bold" color="blue.500">
              ${total.toFixed(2)}
            </Text>
          </HStack>
          <Button
            disabled={cartItems.length === 0}
            onClick={handlePlaceOrder}
            colorPalette="green"
            variant="subtle"
            size="lg"
            mt={4}
            w="100%"
          >
            Place order
          </Button>
        </VStack>
      </Stack>

      <Toaster />
    </Container>
  );
};

export default CartPage;
