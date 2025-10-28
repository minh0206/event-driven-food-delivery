import { useQuery } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { Restaurant } from "../../models/";
import { restaurantService } from "../../services";

export const useRestaurants = () => {
  return useQuery<Restaurant[], Error>({
    queryKey: [CACHE_KEYS.RESTAURANTS],
    queryFn: restaurantService.getAllRestaurants,
  });
};
