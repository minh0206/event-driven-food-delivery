import { CACHE_KEYS } from "@repo/shared/constants";
import { Order } from "@repo/shared/models";
import { orderService } from "@repo/shared/services";
import { useQuery } from "@tanstack/react-query";

export const useCustomerOrders = () => {
  return useQuery<Order[], Error>({
    queryKey: [CACHE_KEYS.CUSTOMER_ORDERS],
    queryFn: orderService.getCustomerOrders,
  });
};
