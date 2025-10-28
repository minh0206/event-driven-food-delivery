import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { MenuItem } from "../../models";
import { restaurantService } from "../../services/RestaurantService";
import { useAuthStore } from "../useAuthStore";

export const useAddMenuItem = () => {
  const { restaurantId } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation<
    MenuItem,
    Error,
    MenuItem,
    { previousMenuItems: MenuItem[] }
  >({
    mutationFn: (menuItem: MenuItem) =>
      restaurantService.addMenuItem(restaurantId!, menuItem),
    onMutate: (menuItem) => {
      const previousMenuItems =
        queryClient.getQueryData<MenuItem[]>(
          CACHE_KEYS.MENU_ITEMS(restaurantId!)
        ) || [];

      queryClient.setQueryData<MenuItem[]>(
        CACHE_KEYS.MENU_ITEMS(restaurantId!),
        (menuItems) => [...(menuItems || []), menuItem]
      );

      return { previousMenuItems };
    },
    onSuccess: (savedMenuItem, oldMenuItem) => {
      queryClient.setQueryData<MenuItem[]>(
        CACHE_KEYS.MENU_ITEMS(savedMenuItem.restaurantId),
        (menuItems) =>
          menuItems?.map((item) =>
            item === oldMenuItem ? savedMenuItem : item
          )
      );
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
