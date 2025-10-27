import { MenuItem } from "@repo/shared/models";
import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";

export interface CartItem {
  menuItemId: number;
  name: string;
  description: string;
  price: number;
  quantity: number;
}

interface CartState {
  restaurantId: number | null;
  items: CartItem[];
  addItem: (item: MenuItem) => void;
  updateQuantity: (menuItemId: number, quantity: number) => void;
  removeItem: (menuItemId: number) => void;
  clearCart: () => void;
}

export const useCartStore = create<CartState>()(
  persist(
    (set) => ({
      restaurantId: null,
      items: [],
      addItem: (menuItem: MenuItem) =>
        set((state) => {
          // If adding item from a different restaurant, clear the cart first
          if (
            state.restaurantId !== null &&
            state.restaurantId !== menuItem.restaurantId
          ) {
            return {
              restaurantId: menuItem.restaurantId,
              items: [
                {
                  menuItemId: menuItem.id,
                  name: menuItem.name,
                  description: menuItem.description,
                  quantity: 1,
                  price: menuItem.price,
                },
              ],
            };
          }

          // Increment quantity if item already exists
          const existingItem = state.items.find(
            (cartItem) => cartItem.menuItemId === menuItem.id
          );

          if (existingItem) {
            return {
              restaurantId: menuItem.restaurantId,
              items: state.items.map((cartItem) =>
                cartItem.menuItemId === existingItem.menuItemId
                  ? { ...cartItem, quantity: cartItem.quantity + 1 }
                  : cartItem
              ),
            };
          }

          // Add new item if it doesn't exist
          return {
            restaurantId: menuItem.restaurantId,
            items: [
              ...state.items,
              {
                menuItemId: menuItem.id,
                name: menuItem.name,
                description: menuItem.description,
                quantity: 1,
                price: menuItem.price,
              },
            ],
          };
        }),
      updateQuantity: (menuItemId, quantity) =>
        set((state) => ({
          items: state.items.map((cartItem) =>
            cartItem.menuItemId === menuItemId
              ? { ...cartItem, quantity }
              : cartItem
          ),
        })),
      removeItem: (menuItemId) =>
        set((state) => ({
          items: state.items.filter(
            (cartItem) => cartItem.menuItemId !== menuItemId
          ),
        })),
      clearCart: () =>
        set(() => ({
          restaurantId: null,
          items: [],
        })),
    }),
    {
      name: "cart-storage",
      storage: createJSONStorage(() => sessionStorage),
    }
  )
);
