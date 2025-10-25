import { Button, HStack } from "@chakra-ui/react";
import { UserAvatar } from "@repo/ui/components";
import { NavLink, useLocation } from "react-router-dom";

export const NavBar = () => {
  const location = useLocation();
  const isHomeActive =
    location.pathname === "/" || location.pathname.startsWith("/restaurants");
  const isMenuActive = location.pathname.startsWith("/menu");

  return (
    <HStack bg="gray.200" p="1" justifyContent="space-between">
      <HStack>
        <Button variant="plain" color={isHomeActive ? "black" : "gray"} asChild>
          <NavLink to="/">Home</NavLink>
        </Button>
        <Button variant="plain" color={isMenuActive ? "black" : "gray"} asChild>
          <NavLink to="/menu">Menu</NavLink>
        </Button>
      </HStack>

      <UserAvatar />
    </HStack>
  );
};
