import { CACHE_KEYS } from "@repo/shared/constants";
import { useQuery } from "@tanstack/react-query";
import { getAllTokens } from "../services/tokenService";

export const useTokens = () => {
  return useQuery({
    queryKey: CACHE_KEYS.TOKENS,
    queryFn: getAllTokens,
    refetchInterval: 30000, // Refetch every 30 seconds
  });
};
