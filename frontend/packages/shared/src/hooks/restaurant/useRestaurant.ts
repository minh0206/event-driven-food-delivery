import { useQuery } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { Restaurant } from "../../models/";
import { restaurantService } from "../../services/";

export const useRestaurant = (id: number) => {
  return useQuery<Restaurant, Error>({
    queryKey: [CACHE_KEYS.RESTAURANT],
    queryFn: () => restaurantService.getRestaurant(id),
  });
};
