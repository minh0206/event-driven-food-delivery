import {
  Flex,
  Heading,
  HStack,
  IconButton,
  Image,
  Text,
  VStack,
} from "@chakra-ui/react";
import { MenuItem } from "@repo/shared/models";
import { RiAddLargeFill } from "react-icons/ri";
import { useCartStore } from "../stores/cartStore";

const MenuItemCard = ({ menuItem }: { menuItem: MenuItem }) => {
  const { addItem: addItemToCart } = useCartStore();

  return (
    <Flex
      gap={3}
      justify="space-between"
      direction={{ base: "column", lg: "row" }}
      align={{ base: "flex-end", lg: "center" }}
    >
      <HStack align="flex-start">
        <Image
          padding={3}
          w="100px"
          h="100px"
          src="https://down-zl-vn.img.susercontent.com/vn-11134513-7r98o-lsu6zq1im07td5@resize_ss640x400!@crop_w640_h400_cT"
        />
        <VStack mt="3" align="flex-start">
          <Heading size="md">{menuItem.name}</Heading>
          <Text textStyle="xs" color="gray.500">
            {menuItem.description}
          </Text>
        </VStack>
      </HStack>

      <HStack mr="2">
        <Heading size="md" mr="2">
          ${menuItem.price}
        </Heading>

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
