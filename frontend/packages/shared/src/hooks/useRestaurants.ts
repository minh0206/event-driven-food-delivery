import { useQuery } from "@tanstack/react-query";
import { Restaurant } from "../models/Restaurant";
import { restaurantService } from "../services/RestaurantService";

export const useRestaurants = () => {
  return useQuery<Restaurant[], Error>({
    queryKey: ["restaurants"],
    queryFn: restaurantService.getAllRestaurants,
  });
};
