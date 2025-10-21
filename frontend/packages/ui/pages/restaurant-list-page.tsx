import { SimpleGrid } from "@chakra-ui/react";
import { RestaurantCard } from "../components/restaurant-card";
import useRestaurants from "../hooks/useRestaurants";

export const RestaurantListPage = () => {
  const { restaurants, error } = useRestaurants();

  return (
    <>
      {error && <div>{error}</div>}
      <SimpleGrid columns={{ sm: 1, md: 2, lg: 3 }} paddingX="200px" gap={5}>
        {restaurants.map((restaurant) => (
          <RestaurantCard key={restaurant.id} restaurant={restaurant} />
        ))}
      </SimpleGrid>
    </>
  );
};
