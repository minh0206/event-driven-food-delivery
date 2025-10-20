import type { Restaurant } from "@/models/Restaurant";
import restaurantService from "@/services/RestaurantService";
import { CanceledError } from "axios";
import { useEffect, useState } from "react";

const useRestaurants = () => {
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const { request, cancel } = restaurantService.getAllRestaurants();

    request
      .then((response) => setRestaurants(response.data.content))
      .catch((error) => {
        if (error instanceof CanceledError) return;
        setError(error.message);
      });

    return () => cancel();
  }, []);

  return { restaurants, error };
};

export default useRestaurants;
