import type { Restaurant } from "@/models/Restaurant";
import { Badge, Box, Card, HStack } from "@chakra-ui/react";
import { Link } from "react-router-dom";

const RestaurantCard = ({ restaurant }: { restaurant: Restaurant }) => {
  return (
    <>
      <Card.Root flexDirection="row" overflow="hidden" maxW="xl">
        <Box>
          <Card.Body>
            <Card.Title mb="2">
              <Link to={`/restaurants/${restaurant.id}`}>
                {restaurant.name}
              </Link>
            </Card.Title>
            <Card.Description>{restaurant.address}</Card.Description>
            <HStack mt="4">
              <Badge>Hot</Badge>
              <Badge>Caffeine</Badge>
            </HStack>
          </Card.Body>
        </Box>
      </Card.Root>
    </>
  );
};

export default RestaurantCard;
