import { Avatar, Menu, Portal } from "@chakra-ui/react";
import { useAuthStore } from "@repo/shared/hooks";
import { Link } from "react-router-dom";

export const UserAvatar = () => {
  const { user, logout } = useAuthStore();

  return (
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
  );
};
