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

  async addMenuItem(restaurantId: number, menuItem: MenuItem) {
    const request = await apiClient.post<MenuItem>(
      `/restaurants/${restaurantId}/menu`,
      menuItem
    );
    return request.data;
  }
}

export const restaurantService = new RestaurantService();
