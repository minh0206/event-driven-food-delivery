import apiClient from "api-client";
import type { MenuItem } from "../models/MenuItem";
import type { Restaurant } from "../models/Restaurant";

interface FetchRestaurantsResponse {
  content: Restaurant[];
  page: any;
}

class RestaurantService {
  getAllRestaurants() {
    const controller = new AbortController();

    const request = apiClient.get<FetchRestaurantsResponse>("/restaurants", {
      signal: controller.signal,
    });

    return { request, cancel: () => controller.abort() };
  }

  getRestaurant(id: string) {
    const controller = new AbortController();

    const request = apiClient.get<Restaurant>(`/restaurants/${id}`, {
      signal: controller.signal,
    });

    return { request, cancel: () => controller.abort() };
  }

  getRestaurantMenu(id: string) {
    const controller = new AbortController();

    const request = apiClient.get<MenuItem[]>(`/restaurants/${id}/menu`, {
      signal: controller.signal,
    });

    return { request, cancel: () => controller.abort() };
  }
}

export default new RestaurantService();
