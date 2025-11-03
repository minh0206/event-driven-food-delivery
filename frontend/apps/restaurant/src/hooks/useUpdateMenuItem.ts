import { CACHE_KEYS } from "@repo/shared/constants";
import { MenuItem } from "@repo/shared/models";
import { restaurantService } from "@repo/shared/services";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useUpdateMenuItem = (restaurantId: number) => {
  const queryClient = useQueryClient();

  return useMutation<
    MenuItem,
    Error,
    MenuItem,
    { previousMenuItems: MenuItem[] }
  >({
    mutationFn: (menuItem) =>
      restaurantService.updateMenuItem(restaurantId, menuItem),
    onMutate: (menuItem) => {
      const previousMenuItems =
        queryClient.getQueryData<MenuItem[]>(
          CACHE_KEYS.MENU_ITEMS(restaurantId)
        ) || [];

      queryClient.setQueryData<MenuItem[]>(
        CACHE_KEYS.MENU_ITEMS(restaurantId),
        (menuItems) =>
          menuItems?.map((item) => (item.id === menuItem.id ? menuItem : item))
      );

      return { previousMenuItems };
    },
    onError: (_error, _variables, context) => {
      if (context) {
        queryClient.setQueryData<MenuItem[]>(
          CACHE_KEYS.MENU_ITEMS(restaurantId),
          context.previousMenuItems
        );
      }
    },
  });
};
