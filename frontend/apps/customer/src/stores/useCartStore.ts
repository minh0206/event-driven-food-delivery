import { STORAGE_KEYS } from "@repo/shared/constants";
import { MenuItem } from "@repo/shared/models";
import { mountStoreDevtool } from "simple-zustand-devtools";
import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";

export interface CartItem extends MenuItem {
  quantity: number;
}

interface CartState {
  restaurantId: number | null;
  items: CartItem[];
  addItem: (restaurantId: number, menuItem: MenuItem) => void;
  updateQuantity: (id: number, quantity: number) => void;
  removeItem: (id: number) => void;
  clearCart: () => void;
}

export const useCartStore = create<CartState>()(
  persist(
    (set) => ({
      restaurantId: null,
      items: [],
      addItem: (restaurantId: number, menuItem: MenuItem) =>
        set((state) => {
          // If adding item from a different restaurant, clear the cart first
          if (state.restaurantId !== restaurantId) {
            return {
              restaurantId,
              items: [{ ...menuItem, quantity: 1 }],
            };
          }

          // Increment quantity if item already exists
          const existingItem = state.items.find(
            (cartItem) => cartItem.id === menuItem.id
          );

          if (existingItem) {
            return {
              items: state.items.map((item) =>
                item.id === existingItem.id
                  ? { ...item, quantity: item.quantity + 1 }
                  : item
              ),
            };
          }

          // Add new item if it doesn't exist
          return { items: [...state.items, { ...menuItem, quantity: 1 }] };
        }),
      updateQuantity: (id, quantity) =>
        set((state) => ({
          items: state.items.map((item) =>
            item.id === id ? { ...item, quantity } : item
          ),
        })),
      removeItem: (id) =>
        set((state) => {
          const items = state.items.filter((item) => item.id !== id);
          return items.length > 0
            ? {
                items: items,
              }
            : { restaurantId: null, items: [] };
        }),
      clearCart: () => set(() => ({ restaurantId: null, items: [] })),
    }),
    {
      name: STORAGE_KEYS.CART,
      storage: createJSONStorage(() => sessionStorage),
    }
  )
);

if (process.env.NODE_ENV === "development") {
  mountStoreDevtool("Cart Store", useCartStore);
}
