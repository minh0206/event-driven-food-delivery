export interface ServiceHealth {
  name: string;
  status: "UP" | "DOWN" | "UNKNOWN";
  endpoint: string;
}

export interface HealthResponse {
  status: string;
}
