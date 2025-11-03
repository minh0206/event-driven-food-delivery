import { CACHE_KEYS } from "@repo/shared/constants";
import { Restaurant } from "@repo/shared/models";
import { restaurantService } from "@repo/shared/services";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useUpdateRestaurant = () => {
  const queryClient = useQueryClient();

  return useMutation<Restaurant, Error, Restaurant>({
    mutationFn: (restaurant) => restaurantService.updateRestaurant(restaurant),
    onSuccess: (savedRestaurant) => {
      queryClient.setQueryData<Restaurant>(
        CACHE_KEYS.RESTAURANT(savedRestaurant.id),
        savedRestaurant
      );
    },
  });
};
