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
import { CartItem } from "../stores/cartStore";

const CartItemCard = ({
  item,
  onUpdateQuantity,
  onRemoveItem,
}: {
  item: CartItem;
  onUpdateQuantity: (id: number, quantity: number) => void;
  onRemoveItem: (id: number) => void;
}) => {
  return (
    <Flex gap={3} direction={{ base: "column", md: "row" }} align="stretch">
      <Image
        //   src={item.image}
        alt={item.name}
        w={{ base: "150px", sm: "100px" }}
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
          {item.description}
        </Text>
      </Stack>

      <HStack align="center" mt={{ base: 2, sm: 0 }}>
        <Stack>
          <NumberInput.Root
            onValueChange={(quantity) =>
              onUpdateQuantity(item.id, quantity.valueAsNumber)
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
            onClick={() => onRemoveItem(item.id)}
          >
            Remove
          </Button>
        </Stack>

        <Text
          flex="1"
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
