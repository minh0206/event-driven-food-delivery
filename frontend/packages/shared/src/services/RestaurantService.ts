import apiClient from "api-client";
import { MenuItem } from "../models/MenuItem";
import { Restaurant } from "../models/Restaurant";

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
    const request =
      await apiClient.get<FetchRestaurantsResponse>("/restaurants");
    return request.data.content;
  }

  async getRestaurant(id: number) {
    const request = await apiClient.get<Restaurant>(`/restaurants/${id}`);
    return request.data;
  }

  async getMenuItems(restaurantId: number) {
    const request = await apiClient.get<MenuItem[]>(
      `/restaurants/${restaurantId}/menu`
    );
    return request.data;
  }

  // Private endpoints
  async getRestaurantProfile() {
    const request = await apiClient.get<Restaurant>("/restaurants/manage");
    return request.data;
  }

  async updateRestaurant(restaurant: Restaurant) {
    const request = await apiClient.put<Restaurant>(
      `/restaurants/manage/${restaurant.id}`,
      restaurant
    );
    return request.data;
  }

  async addMenuItem(restaurantId: number, menuItem: MenuItem) {
    const request = await apiClient.post<MenuItem>(
      `/restaurants/manage/${restaurantId}/menu`,
      menuItem
    );
    return request.data;
  }

  async updateMenuItem(
    restaurantId: number,
    menuItemId: number,
    menuItem: MenuItem
  ) {
    const request = await apiClient.put<MenuItem>(
      `/restaurants/manage/${restaurantId}/menu/${menuItemId}`,
      menuItem
    );
    return request.data;
  }

  async deleteMenuItem(restaurantId: number, menuItemId: number) {
    await apiClient.delete(
      `/restaurants/manage/${restaurantId}/menu/${menuItemId}`
    );
  }
}

export const restaurantService = new RestaurantService();
