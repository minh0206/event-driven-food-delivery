import { Button, Flex, HStack } from "@chakra-ui/react";
import { UserAvatar } from "@repo/ui/components";
import { NavLink, useLocation } from "react-router-dom";

export const NavBar = () => {
  const location = useLocation();
  const isDeliveriesActive =
    location.pathname === "/" || location.pathname.startsWith("/deliveries");
  const isHistoryActive = location.pathname.startsWith("/history");

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
        <Button
          variant="plain"
          color={isDeliveriesActive ? "black" : "gray"}
          asChild
        >
          <NavLink to="/">Deliveries</NavLink>
        </Button>
        <Button
          variant="plain"
          color={isHistoryActive ? "black" : "gray"}
          asChild
        >
          <NavLink to="/history">History</NavLink>
        </Button>
      </HStack>

      <HStack>
        <UserAvatar />
      </HStack>
    </Flex>
  );
};
