import {
  Button,
  Center,
  Heading,
  HStack,
  Input,
  SimpleGrid,
  Table,
} from "@chakra-ui/react";
import { useAuthStore, useMenuItems } from "@repo/shared/hooks";
import { MenuItem } from "@repo/shared/models";
import { Toaster, toaster } from "@repo/ui/components";
import AddMenuItemDialog from "../components/AddMenuItemDialog";
import UpdateMenuItemDialog from "../components/UpdateMenuItemDialog";
import { useAddMenuItem } from "../hooks/useAddMenuItem";
import { useDeleteMenuItem } from "../hooks/useDeleteMenuItem";
import { useUpdateMenuItem } from "../hooks/useUpdateMenuItem";

const MenuManagementPage = () => {
  const { user } = useAuthStore();
  const { data: menuItems, error } = useMenuItems(user!.restaurantId!);

  // Mutations
  const addMenuItem = useAddMenuItem(user!.restaurantId!);
  const updateMenuItem = useUpdateMenuItem(user!.restaurantId!);
  const deleteMenuItem = useDeleteMenuItem(user!.restaurantId!);

  const handleAddMenuItem = async (
    item: MenuItem,
    successCallback: () => void
  ) => {
    try {
      await addMenuItem.mutateAsync(item);
      toaster.success({
        title: "Menu item added",
      });
      successCallback();
    } catch {
      toaster.error({
        title: "Error adding menu item",
      });
    }
  };

  const handleUpdateMenuItem = async (
    item: MenuItem,
    successCallback: () => void,
    errorCallback: () => void
  ) => {
    try {
      await updateMenuItem.mutateAsync(item);
      toaster.success({
        title: "Menu item updated",
      });
      successCallback();
    } catch {
      toaster.error({
        title: "Error updating menu item",
      });
      errorCallback();
    }
  };

  const handleDeleteMenuItem = async (menuItemId: number) => {
    try {
      await deleteMenuItem.mutateAsync({ menuItemId });
      toaster.success({
        title: "Menu item deleted",
      });
    } catch {
      toaster.error({
        title: "Error deleting menu item",
      });
    }
  };

  if (error) {
    return <div>Error: {error.message}</div>;
  }

  return (
    <>
      <SimpleGrid paddingX="5" gap={4}>
        <Heading size="3xl">Menu</Heading>

        <HStack justifyContent="space-between">
          <AddMenuItemDialog onAddMenuItem={handleAddMenuItem} />
          <Input width="1/4" placeholder="Search" />
        </HStack>

        <Center>
          <Table.Root size="lg">
            <Table.Header>
              <Table.Row>
                <Table.ColumnHeader>Name</Table.ColumnHeader>
                <Table.ColumnHeader>Description</Table.ColumnHeader>
                <Table.ColumnHeader>Price</Table.ColumnHeader>
                <Table.ColumnHeader></Table.ColumnHeader>
              </Table.Row>
            </Table.Header>
            <Table.Body>
              {menuItems?.map((item) => (
                <Table.Row key={item.id}>
                  <Table.Cell>{item.name}</Table.Cell>
                  <Table.Cell>{item.description}</Table.Cell>
                  <Table.Cell>{item.price}</Table.Cell>
                  <Table.Cell textAlign="end">
                    <HStack justify="end">
                      <UpdateMenuItemDialog
                        menuItem={item}
                        onUpdateMenuItem={handleUpdateMenuItem}
                      />
                      <Button
                        colorPalette="red"
                        onClick={() => handleDeleteMenuItem(item.id)}
                      >
                        Delete
                      </Button>
                    </HStack>
                  </Table.Cell>
                </Table.Row>
              ))}
            </Table.Body>
          </Table.Root>
        </Center>
      </SimpleGrid>
      <Toaster />
    </>
  );
};

export default MenuManagementPage;
