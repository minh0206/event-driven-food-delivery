import { OrderItem } from "./OrderItem";
import { OrderStatus } from "./OrderStatus";

export interface Order {
  id: number;
  customerId: number;
  restaurantId: number;
  status: OrderStatus;
  totalPrice: number;
  items: OrderItem[];
  createdAt: Date;
}
