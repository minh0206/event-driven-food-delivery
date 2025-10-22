import { SimpleGrid } from "@chakra-ui/react";
import { useRestaurants } from "@repo/shared/hooks";
import { RestaurantCard } from "../components/RestaurantCard";

export const RestaurantListPage = () => {
  const { data: restaurants, error } = useRestaurants();

  if (error) return <div>Error: {error.message}</div>;

  return (
    <>
      <SimpleGrid columns={{ sm: 1, md: 2, lg: 3 }} paddingX="200px" gap={5}>
        {restaurants?.map((restaurant) => (
          <RestaurantCard key={restaurant.id} restaurant={restaurant} />
        ))}
      </SimpleGrid>
    </>
  );
};
