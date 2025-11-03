import apiClient from "api-client";

export interface StatusResponse {
  status: string;
}

class DriverService {
  async getDriverStatus() {
    return (await apiClient.get<StatusResponse>(`/drivers/status`)).data;
  }

  async updateDriverStatus(status: string) {
    return (
      await apiClient.put<StatusResponse>(`/drivers/status`, {
        status: status,
      })
    ).data;
  }
}

export const driverService = new DriverService();
