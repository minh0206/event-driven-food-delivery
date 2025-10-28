import apiClient from "api-client";
import { Order } from "../models/Order";
import { OrderItem } from "../models/OrderItem";

export interface CreateOrderRequest {
  restaurantId: number;
  items: Omit<OrderItem, "id">[];
}

class OrderService {
  async createOrder(order: CreateOrderRequest): Promise<Order> {
    const request = await apiClient.post<Order>("/orders", order);
    return request.data;
  }

  async getCustomerOrders(): Promise<Order[]> {
    return (await apiClient.get<Order[]>("/orders")).data;
  }

  async getRestaurantOrders(restaurantId: number): Promise<Order[]> {
    return (await apiClient.get<Order[]>("/orders/restaurants/" + restaurantId))
      .data;
  }
}

export const orderService = new OrderService();
