import { createBrowserRouter } from "react-router-dom";

import { Role } from "@repo/shared/models";
import { ErrorPage, LoginPage, SignUpPage } from "@repo/ui/pages";
import PrivateRoutes from "./components/PrivateRoutes";
import CartPage from "./pages/CartPage";
import OrderListPage from "./pages/OrderListPage";
import { ProfilePage } from "./pages/ProfilePage";
import { RestaurantDetailPage } from "./pages/RestaurantDetailPage";
import { RestaurantListPage } from "./pages/RestaurantListPage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <PrivateRoutes />,
    errorElement: <ErrorPage />,
    children: [
      { index: true, element: <RestaurantListPage /> },
      { path: "restaurants/:id", element: <RestaurantDetailPage /> },
      { path: "orders", element: <OrderListPage /> },
      { path: "profile", element: <ProfilePage /> },
      { path: "cart", element: <CartPage /> },
    ],
  },
  { path: "/login", element: <LoginPage /> },
  { path: "/signup", element: <SignUpPage role={Role.CUSTOMER} /> },
]);

export default router;
