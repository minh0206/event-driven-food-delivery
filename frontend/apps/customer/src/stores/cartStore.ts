import { STORAGE_KEYS } from "@repo/shared/constants";
import { MenuItem } from "@repo/shared/models";
import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";

export interface CartItem extends MenuItem {
  quantity: number;
}

interface CartState {
  items: CartItem[];
  addItem: (item: MenuItem) => void;
  updateQuantity: (id: number, quantity: number) => void;
  removeItem: (id: number) => void;
  clearCart: () => void;
}

export const useCartStore = create<CartState>()(
  persist(
    (set) => ({
      items: [],
      addItem: (menuItem: MenuItem) =>
        set((state) => {
          // If adding item from a different restaurant, clear the cart first
          if (
            state.items.length > 0 &&
            state.items[0].restaurantId !== menuItem.restaurantId
          ) {
            return { items: [{ ...menuItem, quantity: 1 }] };
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
        set((state) => ({
          items: state.items.filter((item) => item.id !== id),
        })),
      clearCart: () => set(() => ({ items: [] })),
    }),
    {
      name: STORAGE_KEYS.CART,
      storage: createJSONStorage(() => sessionStorage),
    }
  )
);
