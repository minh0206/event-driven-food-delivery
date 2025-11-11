import {
  Box,
  Button,
  Circle,
  Flex,
  Float,
  HStack,
  Icon,
  LinkBox,
  LinkOverlay,
} from "@chakra-ui/react";
import { UserAvatar } from "@repo/ui/components";
import { LuShoppingCart } from "react-icons/lu";
import { NavLink, useLocation } from "react-router-dom";
import { useCartStore } from "../stores/cartStore";

export const NavBar = () => {
  const location = useLocation();
  const base = ((import.meta as any).env.BASE_URL as string) || "/";
  const localPath = location.pathname.startsWith(base)
    ? location.pathname.slice(base.length - (base.endsWith("/") ? 1 : 0))
    : location.pathname;
  const isHomeActive =
    localPath === "/" || localPath.startsWith("/restaurants");
  const isOrdersActive = localPath.startsWith("/orders");

  const cartItemCount = useCartStore((state) =>
    state.items.reduce((acc, item) => acc + item.quantity, 0)
  );

  return (
    <Flex
      as="header"
      position="sticky"
      top="0"
      zIndex="10" // Ensure the navbar stays above other content
      bg="gray.200"
      justifyContent="space-between"
      p="1"
    >
      <HStack>
        <Button variant="plain" color={isHomeActive ? "black" : "gray"} asChild>
          <NavLink to="/">Home</NavLink>
        </Button>
        <Button
          variant="plain"
          color={isOrdersActive ? "black" : "gray"}
          asChild
        >
          <NavLink to="/orders">Orders</NavLink>
        </Button>
      </HStack>

      <HStack>
        <LinkBox marginRight={4}>
          <Box position="relative">
            <Icon size="lg" color="blue.700">
              <LuShoppingCart />
            </Icon>
            <Float placement="top-end" offsetY={1}>
              <Circle size="5" bg="red" color="white">
                {cartItemCount}
              </Circle>
            </Float>
          </Box>

          <LinkOverlay asChild>
            <NavLink to="/cart" />
          </LinkOverlay>
        </LinkBox>
        <UserAvatar />
      </HStack>
    </Flex>
  );
};
