import { CACHE_KEYS } from "@repo/shared/constants";
import { useQuery } from "@tanstack/react-query";
import { driverService } from "../services/DriverService";

const useDriverOrder = () => {
  return useQuery({
    queryKey: CACHE_KEYS.DRIVER_ORDER,
    queryFn: driverService.getDriverOrder,
    retry: (failureCount, error) => {
      // Don't retry if it's a 404 error
      if (error.message.includes("404")) {
        return false;
      }
      // Retry up to 3 times for other errors
      return failureCount < 3;
    },
  });
};

export default useDriverOrder;
