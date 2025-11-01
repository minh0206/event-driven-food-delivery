import apiClient from "api-client";
import { MenuItem } from "../models/MenuItem";
import { Restaurant } from "../models/Restaurant";
import { RestaurantOrder } from "../models/RestaurantOrder";

interface FetchRestaurantsResponse {
  content: Restaurant[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

class RestaurantService {
  // Public endpoints
  async getAllRestaurants() {
    return (await apiClient.get<FetchRestaurantsResponse>("/restaurants")).data
      .content;
  }

  async getRestaurant(id: number) {
    return (await apiClient.get<Restaurant>(`/restaurants/${id}`)).data;
  }

  async getMenuItems(restaurantId: number) {
    return (
      await apiClient.get<MenuItem[]>(`/restaurants/${restaurantId}/menu`)
    ).data;
  }

  // Private endpoints
  async getRestaurantProfile() {
    return (await apiClient.get<Restaurant>("/restaurants/manage")).data;
  }

  async updateRestaurant(restaurant: Restaurant) {
    return (
      await apiClient.put<Restaurant>(
        `/restaurants/manage/${restaurant.id}`,
        restaurant
      )
    ).data;
  }

  async addMenuItem(restaurantId: number, menuItem: MenuItem) {
    return (
      await apiClient.post<MenuItem>(
        `/restaurants/manage/${restaurantId}/menu`,
        menuItem
      )
    ).data;
  }

  async updateMenuItem(menuItem: MenuItem) {
    return (
      await apiClient.put<MenuItem>(
        `/restaurants/manage/${menuItem.restaurantId}/menu/${menuItem.id}`,
        menuItem
      )
    ).data;
  }

  async deleteMenuItem(restaurantId: number, menuItemId: number) {
    await apiClient.delete(
      `/restaurants/manage/${restaurantId}/menu/${menuItemId}`
    );
  }

  async getRestaurantOrders(): Promise<RestaurantOrder[]> {
    return (
      await apiClient.get<RestaurantOrder[]>("/restaurants/manage/orders")
    ).data;
  }

  async updateRestaurantOrder(order: RestaurantOrder) {
    return (
      await apiClient.put(`/restaurants/manage/orders/${order.orderId}`, order)
    ).data;
  }
}

export const restaurantService = new RestaurantService();
