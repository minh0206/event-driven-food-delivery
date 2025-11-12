import { OrderStatus } from "./OrderStatus";
import { RestaurantOrderItem } from "./RestaurantOrderItem";

export interface HistoryRestaurantOrder {
  orderId: number;
  finalStatus: OrderStatus;
  totalPrice: number;
  deliveryTimestamp: Date;
  internalNotes: string;
  items: RestaurantOrderItem[];
}
