import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { MenuItem } from "../../models";
import { restaurantService } from "../../services/RestaurantService";
import { useAuthStore } from "../useAuthStore";

export const useUpdateMenuItem = () => {
  const { restaurantId } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation<
    MenuItem,
    Error,
    MenuItem,
    { previousMenuItems: MenuItem[] }
  >({
    mutationFn: (menuItem) => restaurantService.updateMenuItem(menuItem),
    onMutate: (menuItem) => {
      const previousMenuItems =
        queryClient.getQueryData<MenuItem[]>(
          CACHE_KEYS.MENU_ITEMS(restaurantId!)
        ) || [];

      queryClient.setQueryData<MenuItem[]>(
        CACHE_KEYS.MENU_ITEMS(restaurantId!),
        (menuItems) =>
          menuItems?.map((item) => (item.id === menuItem.id ? menuItem : item))
      );

      return { previousMenuItems };
    },
    onError: (_error, _variables, context) => {
      if (context) {
        queryClient.setQueryData<MenuItem[]>(
          CACHE_KEYS.MENU_ITEMS(restaurantId!),
          context.previousMenuItems
        );
      }
    },
  });
};
