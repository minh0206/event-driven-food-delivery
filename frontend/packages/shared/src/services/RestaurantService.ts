import apiClient from "api-client";
import { HistoryRestaurantOrder } from "../models";
import { MenuItem } from "../models/MenuItem";
import { Restaurant } from "../models/Restaurant";
import { RestaurantOrder } from "../models/RestaurantOrder";

interface PaginatedResponse<T> {
  content: T[];
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
    return (await apiClient.get<PaginatedResponse<Restaurant>>("/restaurants"))
      .data.content;
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

  async updateMenuItem(restaurantId: number, menuItem: MenuItem) {
    return (
      await apiClient.put<MenuItem>(
        `/restaurants/manage/${restaurantId}/menu/${menuItem.id}`,
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

  async getHistoryRestaurantOrders(): Promise<HistoryRestaurantOrder[]> {
    return (
      await apiClient.get<PaginatedResponse<HistoryRestaurantOrder>>(
        "/restaurants/manage/orders/history"
      )
    ).data.content;
  }

  async updateRestaurantOrder(order: RestaurantOrder) {
    return (
      await apiClient.put(`/restaurants/manage/orders/${order.orderId}`, order)
    ).data;
  }
}

export const restaurantService = new RestaurantService();
