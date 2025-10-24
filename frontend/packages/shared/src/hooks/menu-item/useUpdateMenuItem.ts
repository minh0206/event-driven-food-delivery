import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CACHE_KEYS } from "../../constants";
import { MenuItem } from "../../models";
import { restaurantService } from "../../services/RestaurantService";

export const useUpdateMenuItem = () => {
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
        queryClient.getQueryData<MenuItem[]>([CACHE_KEYS.MENU_ITEMS]) || [];

      queryClient.setQueryData<MenuItem[]>(
        [CACHE_KEYS.MENU_ITEMS],
        (menuItems) =>
          menuItems?.map((item) => (item.id === menuItem.id ? menuItem : item))
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
