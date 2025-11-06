import { OrderItem, OrderStatus } from "@repo/shared/models";

export interface DriverOrder {
  id: number;
  customerId: number;
  restaurantId: number;
  status: OrderStatus;
  totalPrice: number;
  items: OrderItem[];
  createdAt: Date;
}
