import { useMenuItems } from "@repo/shared/hooks";
import { useParams } from "react-router-dom";

export const RestaurantDetailPage = () => {
  const { id } = useParams();
  const { data: menu, error } = useMenuItems(Number(id));

  if (error) return <div>Error: {error.message}</div>;

  return (
    <>
      <div>menu: {JSON.stringify(menu)}</div>
    </>
  );
};
