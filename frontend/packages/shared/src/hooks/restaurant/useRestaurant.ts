import { useQuery } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { Restaurant } from "../../models/Restaurant";
import { restaurantService } from "../../services/RestaurantService";

export const useRestaurant = () => {
  return useQuery<Restaurant, Error>({
    queryKey: [CACHE_KEYS.RESTAURANT],
    queryFn: restaurantService.getRestaurantProfile,
  });
};
