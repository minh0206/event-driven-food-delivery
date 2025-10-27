import { CartItem } from "./CartItem";

export interface Order {
  restaurantId: number;
  items: CartItem[];
}
