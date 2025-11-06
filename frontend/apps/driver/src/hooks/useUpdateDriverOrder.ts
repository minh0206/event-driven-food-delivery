import { CACHE_KEYS } from "@repo/shared/constants";
import { OrderStatus } from "@repo/shared/models";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { DriverOrder } from "../models/DriverOrder";
import { driverService } from "../services/DriverService";

export const useUpdateDriverOrder = () => {
  const queryClient = useQueryClient();

  return useMutation<
    void,
    Error,
    OrderStatus,
    { previousOrder: DriverOrder | undefined }
  >({
    mutationFn: (status) => driverService.updateDriverOrderStatus(status),
    onMutate: async (status) => {
      await queryClient.cancelQueries({ queryKey: CACHE_KEYS.DRIVER_ORDER });

      const previousOrder = queryClient.getQueryData<DriverOrder>(
        CACHE_KEYS.DRIVER_ORDER
      );

      queryClient.setQueryData<DriverOrder>(CACHE_KEYS.DRIVER_ORDER, (order) =>
        order ? { ...order, status } : undefined
      );

      return { previousOrder };
    },
    onError: (_error, _variables, context) => {
      if (context?.previousOrder) {
        queryClient.setQueryData<DriverOrder>(
          CACHE_KEYS.DRIVER_ORDER,
          context.previousOrder
        );
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: CACHE_KEYS.DRIVER_STATUS });
      queryClient.invalidateQueries({ queryKey: CACHE_KEYS.DRIVER_ORDER });
    },
  });
};
