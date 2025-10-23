import {
  Center,
  Heading,
  HStack,
  Input,
  SimpleGrid,
  Table,
} from "@chakra-ui/react";
import { useAuthStore } from "@repo/shared/hooks";
import { MenuItem } from "@repo/shared/models";
import { restaurantService } from "@repo/shared/services";
import { useQuery } from "@tanstack/react-query";
import AddMenuItemDialog from "../components/AddMenuItemDialog";

const MenuManagementPage = () => {
  const { user, restaurant } = useAuthStore();

  const { data: menuItems, error } = useQuery<MenuItem[], Error>({
    queryKey: ["menu-items"],
    queryFn: () => restaurantService.getMenuItems(restaurant!.id),
  });

  if (error) {
    return <div>Error: {error.message}</div>;
  }

  return (
    <>
      <SimpleGrid paddingX="5" gap={4}>
        <Heading size="3xl">Menu</Heading>

        <HStack justifyContent="space-between">
          <AddMenuItemDialog />
          <Input width="1/4" placeholder="Search" />
        </HStack>

        <Center>
          <Table.Root size="lg">
            <Table.Header>
              <Table.Row>
                <Table.ColumnHeader>Name</Table.ColumnHeader>
                <Table.ColumnHeader>Price</Table.ColumnHeader>
              </Table.Row>
            </Table.Header>
            <Table.Body>
              {menuItems?.map((item) => (
                <Table.Row key={item.id}>
                  <Table.Cell>{item.name}</Table.Cell>
                  <Table.Cell>{item.price}</Table.Cell>
                </Table.Row>
              ))}
            </Table.Body>
          </Table.Root>
        </Center>
      </SimpleGrid>
    </>
  );
};

export default MenuManagementPage;
