import { Avatar, Button, HStack, Menu, Portal } from "@chakra-ui/react";
import { useAuthStore } from "@repo/shared/hooks";
import { Link, NavLink, useLocation } from "react-router-dom";

export const NavBar = () => {
  const location = useLocation();
  const { user, logout } = useAuthStore();

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

      <Menu.Root>
        <Menu.Trigger marginRight="1" rounded="full" focusRing="outside">
          <Avatar.Root size="sm" variant="solid">
            <Avatar.Fallback>
              {user?.firstName?.[0] || ""}
              {user?.lastName?.[0] || ""}
            </Avatar.Fallback>
          </Avatar.Root>
        </Menu.Trigger>
        <Portal>
          <Menu.Positioner>
            <Menu.Content>
              <Menu.Item value="profile" asChild>
                <Link to="/profile">Profile</Link>
              </Menu.Item>
              <Menu.Item value="logout" onClick={logout}>
                Logout
              </Menu.Item>
            </Menu.Content>
          </Menu.Positioner>
        </Portal>
      </Menu.Root>
    </HStack>
  );
};
