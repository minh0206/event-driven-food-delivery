import { useQuery } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { MenuItem } from "../../models/MenuItem";
import { restaurantService } from "../../services/RestaurantService";

export const useMenuItems = (restaurantId: number) => {
  return useQuery<MenuItem[], Error>({
    enabled: !!restaurantId,
    queryKey: CACHE_KEYS.MENU_ITEMS(restaurantId),
    queryFn: () => restaurantService.getMenuItems(restaurantId),
  });
};
