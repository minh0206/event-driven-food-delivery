import {
  Badge,
  Card,
  HStack,
  Image,
  LinkBox,
  LinkOverlay,
} from "@chakra-ui/react";
import { Restaurant } from "@repo/shared/models";
import { Link } from "react-router-dom";

export const RestaurantCard = ({ restaurant }: { restaurant: Restaurant }) => {
  return (
    <LinkBox>
      <Card.Root w="270px" overflow="hidden" size="sm">
        <Image
          h="120px"
          src="https://down-zl-vn.img.susercontent.com/vn-11134513-7r98o-lsu6zq1im07td5@resize_ss640x400!@crop_w640_h400_cT"
        />
        <Card.Body>
          <Card.Title mb="1">
            <LinkOverlay asChild>
              <Link to={`/restaurants/${restaurant.id}`}>
                {restaurant.name}
              </Link>
            </LinkOverlay>
          </Card.Title>
          <Card.Description>{restaurant.address}</Card.Description>
          <HStack mt="2">
            <Badge>{restaurant.cuisineType}</Badge>
          </HStack>
        </Card.Body>
      </Card.Root>
    </LinkBox>
  );
};
