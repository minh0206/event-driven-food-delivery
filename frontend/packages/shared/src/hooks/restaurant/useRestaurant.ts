import { useQuery } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { Restaurant } from "../../models/";
import { restaurantService } from "../../services/";

export const useRestaurant = (restaurantId: number) => {
  return useQuery<Restaurant, Error>({
    enabled: !!restaurantId,
    queryKey: CACHE_KEYS.RESTAURANT(restaurantId),
    queryFn: () => restaurantService.getRestaurant(restaurantId),
  });
};
