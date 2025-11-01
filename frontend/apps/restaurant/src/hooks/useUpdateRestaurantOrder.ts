import { CACHE_KEYS } from "@repo/shared/constants";
import { RestaurantOrder } from "@repo/shared/models";
import { restaurantService } from "@repo/shared/services";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useUpdateRestaurantOrder = () => {
  const queryClient = useQueryClient();

  return useMutation<
    void,
    Error,
    RestaurantOrder,
    { previousOrders: RestaurantOrder[] }
  >({
    mutationFn: (order) => restaurantService.updateRestaurantOrder(order),
    onMutate: (order) => {
      const previousOrders =
        queryClient.getQueryData<RestaurantOrder[]>(
          CACHE_KEYS.RESTAURANT_ORDERS
        ) || [];

      queryClient.setQueryData<RestaurantOrder[]>(
        CACHE_KEYS.RESTAURANT_ORDERS,
        (orders) =>
          orders?.map((item) =>
            item.orderId === order.orderId ? order : item
          ) || []
      );

      return { previousOrders: previousOrders };
    },
    onError: (_error, _variables, context) => {
      if (context) {
        console.log("Error updating order status:", context);

        queryClient.setQueryData<RestaurantOrder[]>(
          CACHE_KEYS.RESTAURANT_ORDERS,
          context.previousOrders
        );
      }
    },
  });
};
