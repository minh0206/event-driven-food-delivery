import { CACHE_KEYS } from "@repo/shared/constants";
import { MenuItem } from "@repo/shared/models";
import { restaurantService } from "@repo/shared/services";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useAddMenuItem = (restaurantId: number) => {
  const queryClient = useQueryClient();

  return useMutation<
    MenuItem,
    Error,
    MenuItem,
    { previousMenuItems: MenuItem[] }
  >({
    mutationFn: (menuItem: MenuItem) =>
      restaurantService.addMenuItem(restaurantId, menuItem),
    onMutate: (menuItem) => {
      const previousMenuItems =
        queryClient.getQueryData<MenuItem[]>(
          CACHE_KEYS.MENU_ITEMS(restaurantId)
        ) || [];

      queryClient.setQueryData<MenuItem[]>(
        CACHE_KEYS.MENU_ITEMS(restaurantId),
        (menuItems) => [...(menuItems || []), menuItem]
      );

      return { previousMenuItems };
    },
    onSuccess: (savedMenuItem, oldMenuItem) => {
      queryClient.setQueryData<MenuItem[]>(
        CACHE_KEYS.MENU_ITEMS(restaurantId),
        (menuItems) =>
          menuItems?.map((item) =>
            item === oldMenuItem ? savedMenuItem : item
          )
      );
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
