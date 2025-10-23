import {
  Button,
  Center,
  Heading,
  HStack,
  Input,
  SimpleGrid,
  Table,
} from "@chakra-ui/react";
import {
  useAuthStore,
  useDeleteMenuItem,
  useMenuItems,
} from "@repo/shared/hooks";
import { Toaster, toaster } from "@repo/ui/components";
import AddMenuItemDialog from "../components/AddMenuItemDialog";
import UpdateMenuItemDialog from "../components/UpdateMenuItemDialog";

const MenuManagementPage = () => {
  const { restaurant } = useAuthStore();
  const { data: menuItems, error } = useMenuItems(restaurant!.id);
  const deleteMenuItem = useDeleteMenuItem();

  const handleAddMenuItem = (error?: Error) => {
    if (!error) {
      toaster.success({
        title: "Menu item added",
      });
    } else {
      toaster.error({
        title: "Error adding menu item",
      });
    }
  };

  const handleUpdateMenuItem = (error?: Error) => {
    if (!error) {
      toaster.success({
        title: "Menu item updated",
      });
    } else {
      toaster.error({
        title: "Error updating menu item",
      });
    }
  };

  const handleDeleteMenuItem = async (menuItemId: number) => {
    try {
      await deleteMenuItem.mutateAsync({ menuItemId });
      toaster.success({
        title: "Menu item deleted",
      });
    } catch (error) {
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
          <AddMenuItemDialog
            onSuccess={handleAddMenuItem}
            onError={handleAddMenuItem}
          />
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
                        onSuccess={handleUpdateMenuItem}
                        onError={handleUpdateMenuItem}
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
