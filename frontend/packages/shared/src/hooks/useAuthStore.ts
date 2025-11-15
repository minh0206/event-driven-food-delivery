import { mountStoreDevtool } from "simple-zustand-devtools";
import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";
import { STORAGE_KEYS } from "../constants";
import { User } from "../models/User";
import { userService } from "../services";
import { RegisterUser } from "../services/UserService";

type AuthState = {
  token: string | null;
  user: User | null;
  isLoading: boolean;
  isInitialized: boolean;
  initialize: () => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  register: (registerUser: RegisterUser) => Promise<void>;
  logout: () => void;
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      isLoading: false,
      isInitialized: false,

      initialize: async () => {
        set({ isLoading: true });

        try {
          // Try to get the token from sessionStorage
          const token = sessionStorage.getItem("authToken");

          // If token is present, get the user
          if (token) {
            const user = await userService.getProfile();
            set({ token, user });
          }
        } catch (error) {
          console.error("Failed to initialize auth store:", error);
          sessionStorage.removeItem("authToken");
        }

        set({ isLoading: false, isInitialized: true });
      },

      login: async (email, password) => {
        set({ isLoading: true });

        try {
          const { token } = await userService.loginUser(email, password);
          console.log(token);
          sessionStorage.setItem("authToken", token);

          const user = await userService.getProfile();
          set(() => ({ token, user, isLoading: false }));
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      register: async (registerUser: RegisterUser) => {
        set({ isLoading: true });

        try {
          const { token } = await userService.registerUser(registerUser);
          sessionStorage.setItem("authToken", token);

          const user = await userService.getProfile();
          set(() => ({ token, user, isLoading: false }));
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      logout: () => {
        set(() => ({ token: null, user: null }));
        sessionStorage.removeItem("authToken");
      },
    }),
    {
      name: STORAGE_KEYS.AUTH,
      storage: createJSONStorage(() => sessionStorage),
    }
  )
);

if (process.env.NODE_ENV === "development") {
  mountStoreDevtool("Auth Store", useAuthStore);
}
