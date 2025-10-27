import { useQuery } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { Restaurant } from "../../models/";
import { restaurantService } from "../../services/";

export const useRestaurant = (id: number | null) => {
  return useQuery<Restaurant | null, Error>({
    queryKey: [CACHE_KEYS.RESTAURANT, id],
    queryFn: () => restaurantService.getRestaurant(id),
  });
};
