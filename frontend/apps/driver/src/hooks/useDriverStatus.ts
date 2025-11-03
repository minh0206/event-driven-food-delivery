import { CACHE_KEYS } from "@repo/shared/constants";
import { useQuery } from "@tanstack/react-query";
import { driverService } from "../services/DriverService";

const useDriverStatus = () => {
  return useQuery({
    queryKey: CACHE_KEYS.DRIVER_STATUS,
    queryFn: driverService.getDriverStatus,
  });
};

export default useDriverStatus;
