import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { MenuItem } from "../../models";
import { restaurantService } from "../../services/RestaurantService";
import { useAuthStore } from "../useAuthStore";

export const useAddMenuItem = () => {
  const { restaurantId: restaurantId } = useAuthStore();
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
        queryClient.getQueryData<MenuItem[]>([CACHE_KEYS.MENU_ITEMS]) || [];

      queryClient.setQueryData<MenuItem[]>(
        [CACHE_KEYS.MENU_ITEMS],
        (menuItems) => [...(menuItems || []), menuItem]
      );

      return { previousMenuItems };
    },
    onSuccess: (savedMenuItem, oldMenuItem) => {
      queryClient.setQueryData<MenuItem[]>(
        [CACHE_KEYS.MENU_ITEMS],
        (menuItems) =>
          menuItems?.map((item) =>
            item === oldMenuItem ? savedMenuItem : item
          )
      );
    },
    onError: (_error, _variables, context) => {
      if (context) {
        queryClient.setQueryData<MenuItem[]>(
          [CACHE_KEYS.MENU_ITEMS],
          context.previousMenuItems
        );
      }
    },
  });
};
