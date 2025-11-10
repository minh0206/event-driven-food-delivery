import { Role } from "./Role";

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  restaurantId: number | null;
  driverId: number | null;
}
