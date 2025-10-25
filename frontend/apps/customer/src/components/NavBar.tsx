import { Button, HStack } from "@chakra-ui/react";
import { UserAvatar } from "@repo/ui/components";
import { NavLink, useLocation } from "react-router-dom";

export const NavBar = () => {
  const location = useLocation();
  const isHomeActive =
    location.pathname === "/" || location.pathname.startsWith("/restaurants");
  const isOrdersActive = location.pathname.startsWith("/orders");

  return (
    <HStack bg="gray.200" p="1" justifyContent="space-between">
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

      <UserAvatar />
    </HStack>
  );
};
