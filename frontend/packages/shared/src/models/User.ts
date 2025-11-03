export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  restaurantId: number | null;
  driverId: number | null;
}
