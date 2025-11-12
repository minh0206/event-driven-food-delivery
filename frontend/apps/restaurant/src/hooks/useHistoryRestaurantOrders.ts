import { CACHE_KEYS } from "@repo/shared/constants";
import { HistoryRestaurantOrder } from "@repo/shared/models";
import { restaurantService } from "@repo/shared/services";
import { useQuery } from "@tanstack/react-query";

export const useHistoryRestaurantOrders = () => {
  return useQuery<HistoryRestaurantOrder[], Error>({
    queryKey: CACHE_KEYS.HISTORY_RESTAURANT_ORDERS,
    queryFn: restaurantService.getHistoryRestaurantOrders,
  });
};
