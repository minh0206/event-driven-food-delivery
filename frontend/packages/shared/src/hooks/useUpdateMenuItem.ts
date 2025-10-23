import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CACHE_KEYS } from "../constants";
import { MenuItem } from "../models";
import { restaurantService } from "../services/RestaurantService";
import { useAuthStore } from "./useAuthStore";

export const useUpdateMenuItem = () => {
  const { restaurant } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation<
    MenuItem,
    Error,
    { menuItemId: number; menuItem: MenuItem },
    { previousMenuItems: MenuItem[] }
  >({
    mutationFn: ({ menuItemId, menuItem }) =>
      restaurantService.updateMenuItem(restaurant!.id!, menuItemId, menuItem),
    onMutate: ({ menuItemId, menuItem }) => {
      const previousMenuItems =
        queryClient.getQueryData<MenuItem[]>([CACHE_KEYS.MENU_ITEMS]) || [];

      queryClient.setQueryData<MenuItem[]>(
        [CACHE_KEYS.MENU_ITEMS],
        (menuItems) =>
          menuItems?.map((item) => (item.id === menuItemId ? menuItem : item))
      );

      return { previousMenuItems };
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
