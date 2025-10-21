import { useParams } from "react-router-dom";
import useRestaurantMenu from "../hooks/useRestaurantMenu";

export const RestaurantDetailPage = () => {
  const { id } = useParams();
  const { menu, error } = useRestaurantMenu(id!);

  return (
    <>
      {error && <div>{error}</div>}
      <div>menu: {JSON.stringify(menu)}</div>
    </>
  );
};
