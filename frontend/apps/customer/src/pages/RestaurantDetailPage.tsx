import {
  Badge,
  Box,
  Heading,
  HStack,
  Image,
  Separator,
  Show,
  Stack,
  Text,
  VStack,
} from "@chakra-ui/react";
import { useMenuItems, useRestaurant } from "@repo/shared/hooks";
import { MenuItem } from "@repo/shared/models";
import { Toaster, toaster } from "@repo/ui/components";
import { useParams } from "react-router-dom";
import MenuItemCard from "../components/MenuItemCard";
import { useCartStore } from "../stores/useCartStore";

export const RestaurantDetailPage = () => {
  const { id } = useParams();
  const { data: menuItems, error: menuError } = useMenuItems(Number(id));
  const { data: restaurant, error: restaurantError } = useRestaurant(
    Number(id)
  );
  const { addItem: addItemToCart } = useCartStore();

  if (menuError) return <div>menuError: {menuError.message}</div>;
  if (restaurantError)
    return <div>restaurantError: {restaurantError.message}</div>;

  const handleAddToCart = (item: MenuItem) => {
    addItemToCart(Number(id), item);
    toaster.success({
      title: "Item added!",
      description: `Added ${item.name}`,
    });
  };

  return (
    <Stack align="center" my={10}>
      <HStack>
        <Image
          mr={10}
          w="400px"
          src="https://down-zl-vn.img.susercontent.com/vn-11134513-7r98o-lsu6zq1im07td5@resize_ss640x400!@crop_w640_h400_cT"
        />
        <VStack align="start">
          <Heading size="3xl">{restaurant?.restaurantName}</Heading>
          <Text color="gray.700">{restaurant?.address}</Text>
          <Badge>{restaurant?.cuisineType}</Badge>
        </VStack>
      </HStack>

      <Stack
        margin={4}
        align="stretch"
        w={{ base: "100%", md: "50%", lg: "40%", xl: "30%" }}
        bg="white"
        p={6}
        borderRadius="md"
        boxShadow="sm"
      >
        {menuItems?.map((item) => (
          <Box key={item.id}>
            <MenuItemCard menuItem={item} onAddToCart={handleAddToCart} />
            <Show when={item.id !== menuItems.at(-1)?.id}>
              <Separator my={2} />
            </Show>
          </Box>
        ))}
      </Stack>
      <Toaster />
    </Stack>
  );
};
