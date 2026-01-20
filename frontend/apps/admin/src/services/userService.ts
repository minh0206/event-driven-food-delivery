import { User } from "@repo/shared/models";
import { apiClient } from "@repo/shared/services";

export const getAllUsers = async (): Promise<User[]> => {
  const response = await apiClient.get<User[]>("/users");
  return response.data;
};
