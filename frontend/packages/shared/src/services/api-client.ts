import { STORAGE_KEYS } from "@repo/shared/constants";
import { tokenLogger } from "@repo/shared/utils/tokenLogger";
import axios from "axios";

export const apiClient = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Enable cookies
});

// Request interceptor: Add the access token to every request
apiClient.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      tokenLogger.logAccessTokenAttached(config.url || "unknown", token);
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor: Automatically refresh token on 401
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: Error | null, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Skip refresh for public endpoints
    const publicEndpoints = [
      "/users/login",
      "/users/register",
      "/users/actuator",
      // "/users/refresh",
    ];
    const isPublicEndpoint = publicEndpoints.some((endpoint) =>
      originalRequest.url?.includes(endpoint)
    );

    // If error is 401 and we haven't tried to refresh yet and it's not a public endpoint
    if (
      error.response?.status === 401 &&
      !originalRequest._retry &&
      !isPublicEndpoint
    ) {
      tokenLogger.log401Error(originalRequest.url, false);

      if (isRefreshing) {
        // If already refreshing, queue this request
        tokenLogger.logRequestQueued(
          originalRequest.url,
          failedQueue.length + 1
        );
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return apiClient(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;
      tokenLogger.logRefreshInitiated("401_error");

      try {
        // Call refresh endpoint (cookie is sent automatically)
        const { data } = await apiClient.post<{ accessToken: string }>(
          "/users/refresh"
        );
        const newToken = data.accessToken;
        tokenLogger.logAccessTokenReceived(newToken, "refresh");

        // Update token in storage
        sessionStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, newToken);
        tokenLogger.logAccessTokenStored(newToken);

        // Update Authorization header
        apiClient.defaults.headers.common.Authorization = `Bearer ${newToken}`;
        originalRequest.headers.Authorization = `Bearer ${newToken}`;

        // Process queued requests
        processQueue(null, newToken);
        tokenLogger.logRefreshSuccess(newToken, failedQueue.length);

        // Retry original request
        return apiClient(originalRequest);
      } catch (refreshError) {
        // Refresh failed, clear auth and redirect to login
        tokenLogger.logRefreshFailure(refreshError);
        processQueue(refreshError as Error, null);
        tokenLogger.logTokenRemoved("refresh_failed");
        sessionStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);

        // Optionally redirect to login or dispatch logout action
        // window.location.href = '/login';

        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);
