import { create } from "zustand";
import { Restaurant } from "../models";
import { User } from "../models/User";
import { restaurantService, userService } from "../services";

type State = {
  token: string | null;
  user: User | null;
  restaurant: Restaurant | null;
  isLoading: boolean;
};

type Action = {
  initialize: () => void;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
};

export const useAuthStore = create<State & Action>((set, get) => ({
  token: null,
  user: null,
  restaurant: null,
  isLoading: true,

  initialize: async () => {
    set({ isLoading: true });

    try {
      // Try to get the token from localStorage
      const token = localStorage.getItem("authToken");

      // If token is present, get the user
      if (token) {
        const user = await userService.getProfile();
        set({ user, token });

        if (user.role === "RESTAURANT_ADMIN") {
          const restaurant = await restaurantService.getRestaurantProfile();
          set({ restaurant });
        }
      }
    } catch (error) {
      console.error("Failed to initialize auth store:", error);
    }

    set({ isLoading: false });
  },

  login: async (email, password) => {
    set({ isLoading: true });

    const response = await userService.loginUser(email, password);
    const { token, user } = response;
    set(() => ({ token, user }));
    localStorage.setItem("authToken", token!);

    set({ isLoading: false });
  },

  logout: () => {
    set(() => ({ token: null, user: null }));
    localStorage.removeItem("authToken");
  },
}));
