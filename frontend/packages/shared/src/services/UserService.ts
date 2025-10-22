import apiClient from "api-client";
import { User } from "../models/User";

interface LoginResponse {
  token: string;
  user: User;
}

class UserService {
  async createCustomer(user: User) {
    const request = await apiClient.post<User>("/register/customer", user);
    return request.data;
  }

  async createRestaurantAdmin(user: User) {
    const request = await apiClient.post<User>("/register/restaurant", user);
    return request.data;
  }

  async createDriver(user: User) {
    const request = await apiClient.post<User>("/register/driver", user);
    return request.data;
  }

  async loginUser(email: string, password: string) {
    const request = await apiClient.post<LoginResponse>("/users/login", {
      email,
      password,
    });
    return request.data;
  }

  async getProfile() {
    const request = await apiClient.get<User>("/users/profile");
    return request.data;
  }
}

export const userService = new UserService();
