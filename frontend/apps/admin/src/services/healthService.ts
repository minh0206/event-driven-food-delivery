import apiClient from "api-client";
import { HealthResponse } from "../types/ServiceHealth";

const SERVICES = [
  { name: "User Service", endpoint: "/users/health" },
  { name: "Restaurant Service", endpoint: "/restaurants/health" },
  { name: "Order Service", endpoint: "/orders/health" },
  { name: "Delivery Service", endpoint: "/drivers/health" },
];

export const checkServiceHealth = async (endpoint: string): Promise<"UP" | "DOWN"> => {
  try {
    const response = await apiClient.get<HealthResponse>(endpoint, {
      timeout: 5000,
    });
    return response.data.status === "UP" ? "UP" : "DOWN";
  } catch (error) {
    return "DOWN";
  }
};

export const getAllServicesHealth = async () => {
  const healthChecks = SERVICES.map(async (service) => {
    const status = await checkServiceHealth(service.endpoint);
    return {
      name: service.name,
      status,
      endpoint: service.endpoint,
    };
  });

  return Promise.all(healthChecks);
};

export { SERVICES };

