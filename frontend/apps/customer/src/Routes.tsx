import { createBrowserRouter } from "react-router-dom";

import { ErrorPage, LoginPage, ProfilePage, SignUpPage } from "@repo/ui/pages";
import PrivateRoutes from "./components/PrivateRoutes";
import OrderListPage from "./pages/OrderListPage";
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
    ],
  },
  { path: "/login", element: <LoginPage /> },
  { path: "/signup", element: <SignUpPage /> },
]);

export default router;
