import { OrderStatus } from "./OrderStatus";
import { RestaurantOrderItem } from "./RestaurantOrderItem";

export interface RestaurantOrder {
  orderId: number;
  status: OrderStatus;
  receivedAt: Date;
  items: RestaurantOrderItem[];
}
