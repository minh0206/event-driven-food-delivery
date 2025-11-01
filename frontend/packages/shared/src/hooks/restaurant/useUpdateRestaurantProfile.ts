import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { Restaurant } from "../../models";
import { restaurantService } from "../../services/RestaurantService";

export const useUpdateRestaurantProfile = () => {
  const queryClient = useQueryClient();

  return useMutation<Restaurant, Error, Restaurant>({
    mutationFn: (restaurant) => restaurantService.updateRestaurant(restaurant),
    onSuccess: (savedRestaurant) => {
      queryClient.setQueryData<Restaurant>(
        CACHE_KEYS.RESTAURANT_PROFILE,
        savedRestaurant
      );
    },
  });
};
