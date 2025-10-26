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
import { LuShoppingCart } from "react-icons/lu";

import CartItemCard from "../components/CartItemCard";
import { useCartStore } from "../stores/cartStore";

const CartPage = () => {
  const cartItems = useCartStore((state) => state.items);

  const subtotal = cartItems.reduce(
    (total, item) => total + item.price * item.quantity,
    0
  );
  const shipping = 0.1 * subtotal; // 10% shipping fee
  const total = subtotal + shipping;

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
          <Button colorScheme="blue" size="lg" mt={4} w="100%">
            Check out
          </Button>
        </VStack>
      </Stack>
    </Container>
  );
};

export default CartPage;
