import { useQuery } from "@tanstack/react-query";
import { MenuItem } from "../models/MenuItem";
import { restaurantService } from "../services/RestaurantService";

export const useMenuItems = (id: number) => {
  return useQuery<MenuItem[], Error>({
    queryKey: ["menu-items"],
    queryFn: () => restaurantService.getMenuItems(id),
  });
};
