import { Button, Flex, HStack } from "@chakra-ui/react";
import { UserAvatar } from "@repo/ui/components";
import { NavLink, useLocation } from "react-router-dom";

export const AdminNavBar = () => {
  const location = useLocation();
  const base = ((import.meta as any).env.BASE_URL as string) || "/";
  const localPath = location.pathname.startsWith(base)
    ? location.pathname.slice(base.length - (base.endsWith("/") ? 1 : 0))
    : location.pathname;
  const isHomeActive = localPath === "/";
  const isUsersActive = localPath === "/users";

  return (
    <Flex
      as="header"
      position="sticky"
      top="0"
      zIndex="10"
      bg="gray.200"
      justifyContent="space-between"
      p="1"
    >
      <HStack>
        <Button variant="plain" color={isHomeActive ? "black" : "gray"} asChild>
          <NavLink to="/">Home</NavLink>
        </Button>
        <Button variant="plain" color={isUsersActive ? "black" : "gray"} asChild>
          <NavLink to="/users">Users</NavLink>
        </Button>
      </HStack>

      <HStack>
        <UserAvatar />
      </HStack>
    </Flex>
  );
};
