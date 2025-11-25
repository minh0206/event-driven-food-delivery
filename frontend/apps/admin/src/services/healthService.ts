import { apiClient } from "@repo/shared/services";
import { HealthResponse } from "../models/ServiceHealth";

const SERVICES = [
  { name: "API Gateway", endpoint: "/actuator/health" },
  { name: "User Service", endpoint: "/users/actuator/health" },
  { name: "Restaurant Service", endpoint: "/restaurants/actuator/health" },
  { name: "Order Service", endpoint: "/orders/actuator/health" },
  { name: "Delivery Service", endpoint: "/drivers/actuator/health" },
];

export const checkServiceHealth = async (
  endpoint: string
): Promise<"UP" | "DOWN"> => {
  try {
    const response = await apiClient.get<HealthResponse>(endpoint, {
      timeout: 5000,
    });
    return response.data.status === "UP" ? "UP" : "DOWN";
  } catch {
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
