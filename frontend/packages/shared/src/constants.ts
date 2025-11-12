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
  HISTORY_RESTAURANT_ORDERS: ["history-restaurant-orders"],
  DRIVER_STATUS: ["driver-status"],
  DRIVER_ORDER: ["driver-order"],
};

export const STORAGE_KEYS = {
  CART: "cart-storage",
};
