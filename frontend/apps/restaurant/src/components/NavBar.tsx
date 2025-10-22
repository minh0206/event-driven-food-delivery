import { Button, HStack, Text } from "@chakra-ui/react";
import { useAuthStore } from "@repo/shared/hooks";
import { NavLink, useLocation } from "react-router-dom";

export const NavBar = () => {
  const location = useLocation();
  const { user, logout } = useAuthStore();

  const isHomeActive =
    location.pathname === "/" || location.pathname.startsWith("/restaurants");
  const isMenuActive = location.pathname.startsWith("/menu");

  return (
    <HStack bg="gray.200" p="1">
      <Button variant="plain" color={isHomeActive ? "black" : "gray"} asChild>
        <NavLink to="/">Home</NavLink>
      </Button>
      <Button variant="plain" color={isMenuActive ? "black" : "gray"} asChild>
        <NavLink to="/menu">Menu</NavLink>
      </Button>
      <Text>{JSON.stringify(user)}</Text>
      <Button onClick={logout}>Logout</Button>
    </HStack>
  );
};
