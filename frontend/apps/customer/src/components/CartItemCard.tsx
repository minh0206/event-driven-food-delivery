import {
  Button,
  Flex,
  HStack,
  IconButton,
  Image,
  NumberInput,
  Stack,
  Text,
} from "@chakra-ui/react";
import { LuMinus, LuPlus } from "react-icons/lu";
import { CartItem, useCartStore } from "../stores/cartStore";

const CartItemCard = ({ item }: { item: CartItem }) => {
  const { updateQuantity, removeItem } = useCartStore();

  return (
    <Flex
      gap={3}
      direction={{ base: "column", sm: "row" }}
      align={{ base: "stretch", sm: "flex-start" }}
    >
      <Image
        //   src={item.image}
        alt={item.name}
        boxSize={{ base: "100%", sm: "100px" }}
        maxW={{ base: "150px", sm: "100px" }}
        h={{ base: "150px", sm: "100px" }}
        objectFit="cover"
        borderRadius="md"
        bg="blue.50"
        alignSelf={{ base: "center", sm: "flex-start" }}
        mb={{ base: 2, sm: 0 }}
      />

      <Stack flex="1">
        <Text fontWeight="medium">{item.name}</Text>
        <Text color="gray.600" fontSize="sm">
          Description
        </Text>
        <Text>${item.price.toFixed(2)}</Text>
      </Stack>

      <HStack
        flex="1"
        justify="space-between"
        align={{ base: "center", sm: "flex-start" }}
        w={{ base: "100%", sm: "auto" }}
        mt={{ base: 2, sm: 0 }}
      >
        <Stack>
          <NumberInput.Root
            onValueChange={(quantity) =>
              updateQuantity(item.id, quantity.valueAsNumber)
            }
            defaultValue={item.quantity.toString()}
            min={1}
            max={100}
            unstyled
            spinOnPress={false}
          >
            <HStack gap="2">
              <NumberInput.DecrementTrigger asChild>
                <IconButton variant="outline" size="sm">
                  <LuMinus />
                </IconButton>
              </NumberInput.DecrementTrigger>
              <NumberInput.ValueText
                textAlign="center"
                fontSize="lg"
                minW="3ch"
              />
              <NumberInput.IncrementTrigger asChild>
                <IconButton variant="outline" size="sm">
                  <LuPlus />
                </IconButton>
              </NumberInput.IncrementTrigger>
            </HStack>
          </NumberInput.Root>
          <Button
            variant="subtle"
            colorPalette="red"
            size="sm"
            onClick={() => removeItem(item.id)}
          >
            Remove
          </Button>
        </Stack>

        <Text
          fontWeight="medium"
          fontSize="lg"
          textAlign="right"
          minW={{ base: "auto", sm: "70px" }}
        >
          ${(item.price * item.quantity).toFixed(2)}
        </Text>
      </HStack>
    </Flex>
  );
};

export default CartItemCard;
