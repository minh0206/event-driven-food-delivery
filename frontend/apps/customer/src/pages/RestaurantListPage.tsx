import { Center, Grid, Heading, Stack } from "@chakra-ui/react";
import { useRestaurants } from "@repo/shared/hooks";
import { RestaurantCard } from "../components/RestaurantCard";

export const RestaurantListPage = () => {
  const { data: restaurants, error } = useRestaurants();

  if (error) return <div>Error: {error.message}</div>;

  return (
    <Center>
      <Stack marginY={5}>
        <Heading size="3xl" marginBottom={10} textAlign="center">
          Restaurants
        </Heading>
        <Grid
          templateColumns={{
            base: "repeat(1, 1fr)",
            md: "repeat(2, 1fr)",
            lg: "repeat(3, 1fr)",
            xl: "repeat(4, 1fr)",
            "2xl": "repeat(5, 1fr)",
          }}
          gap={5}
        >
          {restaurants?.map((restaurant) => (
            <RestaurantCard key={restaurant.id} restaurant={restaurant} />
          ))}
        </Grid>
      </Stack>
    </Center>
  );
};
