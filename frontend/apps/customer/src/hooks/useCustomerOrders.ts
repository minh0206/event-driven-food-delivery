import { CACHE_KEYS } from "@repo/shared/constants";
import { useQuery } from "@tanstack/react-query";
import { orderService } from "../services/OrderService";

export const useCustomerOrders = () => {
  return useQuery({
    queryKey: [CACHE_KEYS.CUSTOMER_ORDERS],
    queryFn: () => orderService.getCustomerOrders(),
  });
};
