import { AxiosError } from "axios";
import { mountStoreDevtool } from "simple-zustand-devtools";
import { create } from "zustand";
import { STORAGE_KEYS } from "../constants";
import { Role } from "../models";
import { User } from "../models/User";
import { userService } from "../services";
import { RegisterUser } from "../services/UserService";

type AuthState = {
  token: string | null;
  user: User | null;
  isLoading: boolean;
  isInitialized: boolean;
  initialize: (role: Role) => Promise<void>;
  login: (email: string, password: string, role: Role) => Promise<void>;
  register: (registerUser: RegisterUser) => Promise<void>;
  logout: () => void;
};

export const useAuthStore = create<AuthState>()((set, get) => ({
  token: null,
  user: null,
  isLoading: false,
  isInitialized: false,

  initialize: async (role: Role) => {
    set({ isLoading: true });

    try {
      //Check health
      await userService.checkHealth();

      // Try to get the token from localStorage
      const token = localStorage.getItem(role);
      console.log(token);

      // If token is present, get the user
      if (token) {
        sessionStorage.setItem(STORAGE_KEYS.AUTH, token);
        const user = await userService.getProfile();
        set({ token, user });
      }
      set({ isLoading: false, isInitialized: true });
    } catch (error) {
      console.error("Failed to initialize Auth store:", error);
      sessionStorage.removeItem(STORAGE_KEYS.AUTH);
      set({ isLoading: false, isInitialized: false });
    }
  },

  login: async (email, password, role) => {
    set({ isLoading: true });

    try {
      const { token } = await userService.loginUser(email, password);
      sessionStorage.setItem(STORAGE_KEYS.AUTH, token);
      console.log("token", token);

      const user = await userService.getProfile();
      if (user.role !== role) {
        console.error("UNAUTHORIZED", user.role !== role);
        throw new AxiosError("You are not authorized to access this resource");
      }

      localStorage.setItem(role, token);
      set(() => ({ token, user, isLoading: false }));
    } catch (error) {
      sessionStorage.removeItem(STORAGE_KEYS.AUTH);
      set({ isLoading: false });
      throw error;
    }
  },

  register: async (registerUser: RegisterUser) => {
    set({ isLoading: true });

    try {
      const { token } = await userService.registerUser(registerUser);
      localStorage.setItem(registerUser.role, token);
      sessionStorage.setItem(STORAGE_KEYS.AUTH, token);

      const user = await userService.getProfile();
      set(() => ({ token, user, isLoading: false }));
    } catch (error) {
      set({ isLoading: false });
      throw error;
    }
  },

  logout: () => {
    sessionStorage.removeItem(STORAGE_KEYS.AUTH);

    const role = get().user?.role;
    if (role) localStorage.removeItem(role);

    set(() => ({ token: null, user: null }));
  },
}));

if (process.env.NODE_ENV === "development") {
  mountStoreDevtool("Auth Store", useAuthStore);
}
