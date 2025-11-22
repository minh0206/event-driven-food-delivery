import { AxiosError } from "axios";
import { mountStoreDevtool } from "simple-zustand-devtools";
import { create } from "zustand";
import { STORAGE_KEYS } from "../constants";
import { Role } from "../models";
import { User } from "../models/User";
import { userService } from "../services";
import { RegisterUser } from "../services/UserService";
import { tokenLogger } from "../utils/tokenLogger";

type AuthState = {
  token: string | null;
  user: User | null;
  isLoading: boolean;
  isInitialized: boolean;
  initialize: () => Promise<void>;
  login: (email: string, password: string, role: Role) => Promise<void>;
  register: (registerUser: RegisterUser) => Promise<void>;
  refreshToken: () => Promise<void>;
  logout: () => Promise<void>;
};

export const useAuthStore = create<AuthState>()((set, get) => ({
  token: null,
  user: null,
  isLoading: false,
  isInitialized: false,

  initialize: async () => {
    set({ isLoading: true });

    try {
      //Check health
      const health = await userService.checkHealth();
      console.log("init", health);

      // Try to get a session access token
      const accessToken = sessionStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
      tokenLogger.logInitialization(!!accessToken);

      if (accessToken) {
        tokenLogger.logAccessTokenRetrieved(accessToken);
      }

      // If token is present, get the user
      if (accessToken) {
        const user = await userService.getProfile();
        set({ token: accessToken, user });
      }
      set({ isLoading: false, isInitialized: true });
    } catch (error) {
      console.error("Failed to initialize Auth store:", error);
      tokenLogger.logTokenRemoved("initialization_failed");
      sessionStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
      set({ isLoading: false, isInitialized: true });
    }
  },

  login: async (email, password, role) => {
    set({ isLoading: true });

    try {
      const { accessToken } = await userService.loginUser(email, password);
      tokenLogger.logAccessTokenReceived(accessToken, "login");

      sessionStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
      tokenLogger.logAccessTokenStored(accessToken);

      const user = await userService.getProfile();
      if (user.role !== role) {
        console.error("UNAUTHORIZED", user.role !== role);
        throw new AxiosError("You are not authorized to access this resource");
      }

      set(() => ({ accessToken, user, isLoading: false }));
    } catch (error) {
      tokenLogger.logTokenRemoved("logout");
      sessionStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
      set({ isLoading: false });
      throw error;
    }
  },

  register: async (registerUser: RegisterUser) => {
    set({ isLoading: true });

    try {
      const { accessToken } = await userService.registerUser(registerUser);
      tokenLogger.logAccessTokenReceived(accessToken, "register");

      sessionStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
      tokenLogger.logAccessTokenStored(accessToken);

      const user = await userService.getProfile();
      set(() => ({ accessToken, user, isLoading: false }));
    } catch (error) {
      set({ isLoading: false });
      throw error;
    }
  },

  refreshToken: async () => {
    try {
      tokenLogger.logRefreshInitiated("manual");

      // The refreshToken cookie is automatically sent by the browser
      const { accessToken } = await userService.refreshToken();
      tokenLogger.logAccessTokenReceived(accessToken, "refresh");

      // Update the access token in storage
      sessionStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
      tokenLogger.logAccessTokenStored(accessToken);

      set({ token: accessToken });
      tokenLogger.logRefreshSuccess(accessToken);
    } catch (error) {
      console.error("Failed to refresh token:", error);
      tokenLogger.logRefreshFailure(error);

      // If refresh fails, logout the user
      get().logout();
      throw error;
    }
  },

  logout: async () => {
    tokenLogger.logLogout();

    try {
      // Call backend to clear the refresh token cookie
      await userService.logout();
    } catch (error) {
      console.error("Logout error:", error);
    } finally {
      // Clear local storage regardless of backend response
      tokenLogger.logTokenRemoved("logout");
      sessionStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);

      set(() => ({ token: null, user: null }));
    }
  },
}));

if (process.env.NODE_ENV === "development") {
  mountStoreDevtool("Auth Store", useAuthStore);
}
