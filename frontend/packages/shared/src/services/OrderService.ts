import { apiClient } from "@repo/shared/services";
import { Order } from "../models/Order";
import { OrderItem } from "../models/OrderItem";

class OrderService {
  async createOrder(
    restaurantId: number,
    items: Omit<OrderItem, "id">[]
  ): Promise<Order> {
    const request = await apiClient.post<Order>("/orders", {
      restaurantId,
      items,
    });
    return request.data;
  }

  async getCustomerOrders(): Promise<Order[]> {
    return (await apiClient.get<Order[]>("/orders")).data;
  }
}

export const orderService = new OrderService();
