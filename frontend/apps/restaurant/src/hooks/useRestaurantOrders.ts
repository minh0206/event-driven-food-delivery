import { CACHE_KEYS } from "@repo/shared/constants";
import { RestaurantOrder } from "@repo/shared/models";
import { restaurantService } from "@repo/shared/services";
import { useQuery } from "@tanstack/react-query";

export const useRestaurantOrders = () => {
  return useQuery<RestaurantOrder[], Error>({
    queryKey: CACHE_KEYS.RESTAURANT_ORDERS,
    queryFn: () => restaurantService.getRestaurantOrders(),
  });
};
