import { create } from "zustand";
import { User } from "../models/User";
import { restaurantService, userService } from "../services";

type AuthState = {
  token: string | null;
  user: User | null;
  restaurantId: number | null;
  isLoading: boolean;
  isInitialized: boolean;
  initialize: () => void;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  token: null,
  user: null,
  restaurantId: null,
  isLoading: false,
  isInitialized: false,

  initialize: async () => {
    set({ isLoading: true });

    try {
      // Try to get the token from localStorage
      const token = localStorage.getItem("authToken");

      // If token is present, get the user
      if (token) {
        const user = await userService.getProfile();
        set({ user, token });

        // If the user is a restaurant admin, get the restaurant id
        if (user.role === "RESTAURANT_ADMIN") {
          const restaurant = await restaurantService.getRestaurantProfile();
          set({ restaurantId: restaurant.id });
        }
      }
    } catch (error) {
      console.error("Failed to initialize auth store:", error);
    }

    set({ isLoading: false, isInitialized: true });
  },

  login: async (email, password) => {
    set({ isLoading: true });

    const response = await userService.loginUser(email, password);
    const { token, user } = response;
    set(() => ({ token, user }));
    localStorage.setItem("authToken", token!);

    // If the user is a restaurant admin, get the restaurant id
    if (user.role === "RESTAURANT_ADMIN") {
      const restaurant = await restaurantService.getRestaurantProfile();
      set({ restaurantId: restaurant.id });
    }

    set({ isLoading: false });
  },

  logout: () => {
    set(() => ({ token: null, user: null }));
    localStorage.removeItem("authToken");
  },
}));
