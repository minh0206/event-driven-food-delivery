import useRestaurantMenu from "@/hooks/useRestaurantMenu";
import { useParams } from "react-router-dom";

const RestaurantDetailPage = () => {
  const { id } = useParams();
  const { menu, error } = useRestaurantMenu(id!);

  return (
    <>
      {error && <div>{error}</div>}
      <div>menu: {JSON.stringify(menu)}</div>
    </>
  );
};

export default RestaurantDetailPage;
