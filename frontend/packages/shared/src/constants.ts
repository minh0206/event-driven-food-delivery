export const CACHE_KEYS = {
  RESTAURANTS: ["restaurants"],
  RESTAURANT: (restaurantId: number) => [
    ...CACHE_KEYS.RESTAURANTS,
    restaurantId,
  ],
  MENU_ITEMS: (restaurantId: number) => [
    ...CACHE_KEYS.RESTAURANTS,
    restaurantId,
    "menu-items",
  ],
  CUSTOMER_ORDERS: ["customer-orders"],
  RESTAURANT_ORDERS: ["restaurant-orders"],
  DRIVER_STATUS: ["driver-status"],
};

export const STORAGE_KEYS = {
  CART: "cart-storage",
};
