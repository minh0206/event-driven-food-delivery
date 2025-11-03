export interface Restaurant {
  id: number;
  restaurantName: string;
  address: string | undefined;
  cuisineType: string | undefined;
  ownerId: number;
}
