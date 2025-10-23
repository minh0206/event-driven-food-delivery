import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CACHE_KEYS } from "../constants";
import { MenuItem } from "../models";
import { restaurantService } from "../services/RestaurantService";
import { useAuthStore } from "./useAuthStore";

export const useDeleteMenuItem = () => {
  const { restaurant } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation<
    void,
    Error,
    { menuItemId: number },
    { previousMenuItems: MenuItem[] }
  >({
    mutationFn: ({ menuItemId }) =>
      restaurantService.deleteMenuItem(restaurant!.id!, menuItemId),
    onMutate: ({ menuItemId }) => {
      const previousMenuItems =
        queryClient.getQueryData<MenuItem[]>([CACHE_KEYS.MENU_ITEMS]) || [];

      queryClient.setQueryData<MenuItem[]>(
        [CACHE_KEYS.MENU_ITEMS],
        (menuItems) => menuItems?.filter((item) => item.id !== menuItemId)
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
