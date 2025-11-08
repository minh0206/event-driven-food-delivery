import { mountStoreDevtool } from "simple-zustand-devtools";
import { create } from "zustand";
import { User } from "../models/User";
import { userService } from "../services";

type AuthState = {
  token: string | null;
  user: User | null;
  isLoading: boolean;
  isInitialized: boolean;
  initialize: () => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  token: null,
  user: null,
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
        set({ token, user });
      }
    } catch (error) {
      console.error("Failed to initialize auth store:", error);
    }

    set({ isLoading: false, isInitialized: true });
  },

  login: async (email, password) => {
    set({ isLoading: true });

    const { token } = await userService.loginUser(email, password);
    localStorage.setItem("authToken", token);

    const user = await userService.getProfile();
    set(() => ({ token, user, isLoading: false }));
  },

  logout: () => {
    set(() => ({ token: null, user: null }));
    localStorage.removeItem("authToken");
  },
}));

if (process.env.NODE_ENV === "development") {
  mountStoreDevtool("Auth Store", useAuthStore);
}
