import {
  Badge,
  Card,
  HStack,
  Image,
  LinkBox,
  LinkOverlay,
} from "@chakra-ui/react";
import { Link } from "react-router-dom";
import type { Restaurant } from "../models/Restaurant";

export const RestaurantCard = ({ restaurant }: { restaurant: Restaurant }) => {
  return (
    <>
      <LinkBox>
        <Card.Root flexDirection="row" overflow="hidden" maxW="xl">
          <Image
            objectFit="cover"
            maxW="130px"
            src="https://images.unsplash.com/photo-1667489022797-ab608913feeb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHw5fHx8ZW58MHx8fHw%3D&auto=format&fit=crop&w=800&q=60"
            alt="Caffe Latte"
          />
          <Card.Body>
            <Card.Title mb="2">
              <LinkOverlay asChild>
                <Link to={`/restaurants/${restaurant.id}`}>
                  {restaurant.name}
                </Link>
              </LinkOverlay>
            </Card.Title>
            <Card.Description>{restaurant.address}</Card.Description>
            <HStack mt="4">
              <Badge>Hot</Badge>
              <Badge>Caffeine</Badge>
            </HStack>
          </Card.Body>
        </Card.Root>
      </LinkBox>
    </>
  );
};
