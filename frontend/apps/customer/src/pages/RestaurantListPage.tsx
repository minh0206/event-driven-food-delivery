import RestaurantCard from "@/components/RestaurantCard";
import useRestaurants from "@/hooks/useRestaurants";

const RestaurantListPage = () => {
  const { restaurants, error } = useRestaurants();

  return (
    <>
      {error && <div>{error}</div>}
      <ul>
        {restaurants.map((restaurant) => (
          <RestaurantCard key={restaurant.id} restaurant={restaurant} />
        ))}
      </ul>
    </>
  );
};

export default RestaurantListPage;
