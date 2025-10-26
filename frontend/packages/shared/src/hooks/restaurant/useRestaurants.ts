import { useQuery } from "@tanstack/react-query";
import { Restaurant } from "../../models/";
import { restaurantService } from "../../services";

export const useRestaurants = () => {
  return useQuery<Restaurant[], Error>({
    queryKey: ["restaurants"],
    queryFn: restaurantService.getAllRestaurants,
  });
};
