import { Role } from "../models";
import { User } from "../models/User";
import { apiClient } from "./api-client";

interface LoginResponse {
  token: string;
}

export interface RegisterUser {
  role: Role;
  email: string;
  password: string;
  firstName: string;
  lastName?: string;
  restaurantName?: string;
  address?: string;
  cuisineType?: string;
}

class UserService {
  async checkHealth() {
    return (await apiClient.get("/users/actuator/health")).data;
  }

  async registerUser(registerUser: RegisterUser) {
    switch (registerUser.role) {
      case Role.CUSTOMER:
        return (
          await apiClient.post<LoginResponse>(
            "/users/register/customer",
            registerUser
          )
        ).data;
      case Role.RESTAURANT_ADMIN:
        return (
          await apiClient.post<LoginResponse>(
            "/users/register/restaurant",
            registerUser
          )
        ).data;
      case Role.DRIVER:
        return (
          await apiClient.post<LoginResponse>(
            "/users/register/driver",
            registerUser
          )
        ).data;
      default:
        throw new Error("Invalid role");
    }
  }

  async loginUser(email: string, password: string) {
    return (
      await apiClient.post<LoginResponse>("/users/login", {
        email,
        password,
      })
    ).data;
  }

  async getProfile() {
    const request = await apiClient.get<User>("/users/profile");
    return request.data;
  }
}

export const userService = new UserService();
