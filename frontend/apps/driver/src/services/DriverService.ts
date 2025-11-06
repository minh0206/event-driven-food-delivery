import { OrderStatus } from "@repo/shared/models";
import apiClient from "api-client";
import { DriverOrder } from "../models/DriverOrder";
import { DriverStatus } from "../models/DriverStatus";

export interface StatusResponse {
  status: DriverStatus;
}

class DriverService {
  async getDriverStatus() {
    return (await apiClient.get<StatusResponse>(`/drivers/status`)).data;
  }

  async updateDriverStatus(status: DriverStatus) {
    return (
      await apiClient.put<StatusResponse>(`/drivers/status`, {
        status: status,
      })
    ).data;
  }

  async getDriverOrder() {
    const driverOrder = (await apiClient.get<DriverOrder>(`/drivers/order`))
      .data;
    return driverOrder;
  }

  async updateDriverOrderStatus(status: OrderStatus): Promise<void> {
    if (status === OrderStatus.IN_TRANSIT) {
      await apiClient.post(`/drivers/order/pickup`);
    } else if (status === OrderStatus.DELIVERED) {
      await apiClient.post(`/drivers/order/complete`);
    } else {
      throw new Error("Invalid order status");
    }
  }
}

export const driverService = new DriverService();
