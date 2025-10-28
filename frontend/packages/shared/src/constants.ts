export const CACHE_KEYS = {
  RESTAURANTS: ["restaurants"],
  RESTAURANT_PROFILE: ["restaurant-profile"],
  MENU_ITEMS: (restaurantId: number) => [
    ...CACHE_KEYS.RESTAURANTS,
    restaurantId,
    "menu-items",
  ],
  CUSTOMER_ORDERS: ["customer-orders"],
  RESTAURANT_ORDERS: ["restaurant-orders"],
};

export const STORAGE_KEYS = {
  CART: "cart-storage",
};
