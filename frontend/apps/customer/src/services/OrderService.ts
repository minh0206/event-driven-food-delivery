import apiClient from "api-client";
import { CartItem } from "../models/CartItem";
import { Order } from "../models/Order";

interface OrderResponse {
  id: number;
  customerId: number;
  restaurantId: number;
  status: string;
  totalPrice: number;
  createdAt: string;
  items: CartItem[];
}

class OrderService {
  async placeOrder(order: Order): Promise<OrderResponse> {
    const request = await apiClient.post<OrderResponse>("/orders", order);
    return request.data;
  }
}

export const orderService = new OrderService();
