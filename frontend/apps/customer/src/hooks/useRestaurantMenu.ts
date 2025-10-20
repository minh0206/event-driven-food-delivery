import { type MenuItem } from "@/models/MenuItem";
import restaurantService from "@/services/RestaurantService";
import { CanceledError } from "axios";
import { useEffect, useState } from "react";

const useRestaurantMenu = (id: string) => {
  const [menu, setMenu] = useState<MenuItem[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // fetch menu
    const { request, cancel } = restaurantService.getRestaurantMenu(id);

    request
      .then((response) => {
        setMenu(response.data); // set menu
      })
      .catch((error) => {
        if (error instanceof CanceledError) return;
        setError(error.message);
      });

    return () => cancel();
  }, []);

  return { menu, error };
};

export default useRestaurantMenu;
