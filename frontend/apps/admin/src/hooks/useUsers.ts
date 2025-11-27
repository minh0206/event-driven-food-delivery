import { useQuery } from "@tanstack/react-query";
import { getAllUsers } from "../services/userService";

export const useUsers = () => {
  return useQuery({
    queryKey: ["users"],
    queryFn: getAllUsers,
  });
};
