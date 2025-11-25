import { apiClient } from "@repo/shared/services";
import { RefreshToken } from "../models/RefreshToken";

export const getAllTokens = async (): Promise<RefreshToken[]> => {
  const response = await apiClient.get<RefreshToken[]>("/users/tokens");
  return response.data;
};

export const pruneTokens = async (): Promise<string> => {
  const response = await apiClient.delete<string>("/users/tokens/prune");
  return response.data;
};
