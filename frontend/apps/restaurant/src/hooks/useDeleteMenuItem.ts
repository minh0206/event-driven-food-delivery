import { CACHE_KEYS } from "@repo/shared/constants";
import { MenuItem } from "@repo/shared/models";
import { restaurantService } from "@repo/shared/services";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useDeleteMenuItem = (restaurantId: number) => {
  const queryClient = useQueryClient();

  return useMutation<
    void,
    Error,
    { menuItemId: number },
    { previousMenuItems: MenuItem[] }
  >({
    mutationFn: ({ menuItemId }) =>
      restaurantService.deleteMenuItem(restaurantId, menuItemId),
    onMutate: ({ menuItemId }) => {
      const previousMenuItems =
        queryClient.getQueryData<MenuItem[]>(
          CACHE_KEYS.MENU_ITEMS(restaurantId)
        ) || [];

      queryClient.setQueryData<MenuItem[]>(
        CACHE_KEYS.MENU_ITEMS(restaurantId),
        (menuItems) => menuItems?.filter((item) => item.id !== menuItemId)
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
