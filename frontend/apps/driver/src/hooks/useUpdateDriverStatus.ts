import { CACHE_KEYS } from "@repo/shared/constants";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { driverService } from "../services/DriverService";

const useUpdateDriverStatus = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationKey: CACHE_KEYS.DRIVER_STATUS,
    mutationFn: (status: string) => driverService.updateDriverStatus(status),
    onSuccess: (status) => {
      queryClient.setQueryData(CACHE_KEYS.DRIVER_STATUS, status);
    },
  });
};

export default useUpdateDriverStatus;
