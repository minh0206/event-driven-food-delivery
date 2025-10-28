import { OrderItem } from "./OrderItem";

export interface Order {
  id: number;
  customerId: number;
  restaurantId: number;
  status: string;
  totalPrice: number;
  items: OrderItem[];
  createdAt: Date;
}
