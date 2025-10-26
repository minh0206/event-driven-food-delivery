import { Flex, HStack, IconButton, Image, Stack, Text } from "@chakra-ui/react";
import { MenuItem } from "@repo/shared/models";
import { RiAddLargeFill } from "react-icons/ri";
import { useCartStore } from "../stores/cartStore";

const MenuItemCard = ({ menuItem }: { menuItem: MenuItem }) => {
  const { addItem: addItemToCart } = useCartStore();

  return (
    <Flex gap={3} direction={{ base: "column", sm: "row" }} align="stretch">
      <Image
        //   src={item.image}
        alt={menuItem.name}
        // boxSize={{ base: "100px", sm: "100px" }}
        w={{ base: "150px", sm: "100px" }}
        h={{ base: "150px", sm: "100px" }}
        objectFit="cover"
        borderRadius="md"
        bg="blue.50"
        alignSelf={{ base: "center", sm: "flex-start" }}
        mb={{ base: 2, sm: 0 }}
      />

      <Stack flex="1">
        <Text fontWeight="medium">{menuItem.name}</Text>
        <Text color="gray.600" fontSize="sm">
          {menuItem.description}
        </Text>
      </Stack>

      <HStack align="center" mt={{ base: 2, sm: 0 }}>
        <Text
          flex="1"
          fontWeight="medium"
          fontSize="lg"
          textAlign="right"
          minW={{ base: "auto", sm: "70px" }}
        >
          ${menuItem.price.toFixed(2)}
        </Text>

        <IconButton
          aria-label="Add to cart"
          rounded="full"
          variant="solid"
          size="sm"
          colorPalette="green"
          onClick={() => addItemToCart(menuItem)}
        >
          <RiAddLargeFill />
        </IconButton>
      </HStack>
    </Flex>
  );
};

export default MenuItemCard;
