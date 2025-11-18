import { createBrowserRouter } from "react-router-dom";

import { Role } from "@repo/shared/models";
import { ErrorPage, LoginPage, SignUpPage } from "@repo/ui/pages";

import { PrivateRoutes } from "@repo/ui/components";
import CartPage from "./pages/CartPage";
import { Layout } from "./pages/Layout";
import OrderListPage from "./pages/OrderListPage";
import { ProfilePage } from "./pages/ProfilePage";
import { RestaurantDetailPage } from "./pages/RestaurantDetailPage";
import { RestaurantListPage } from "./pages/RestaurantListPage";

const router = createBrowserRouter(
  [
    {
      path: "/",
      element: (
        <PrivateRoutes role={Role.CUSTOMER}>
          <Layout />
        </PrivateRoutes>
      ),
      errorElement: <ErrorPage />,
      children: [
        { index: true, element: <RestaurantListPage /> },
        { path: "restaurants/:id", element: <RestaurantDetailPage /> },
        { path: "orders", element: <OrderListPage /> },
        { path: "profile", element: <ProfilePage /> },
        { path: "cart", element: <CartPage /> },
      ],
    },
    { path: "login", element: <LoginPage role={Role.CUSTOMER} /> },
    { path: "signup", element: <SignUpPage role={Role.CUSTOMER} /> },
  ],
  { basename: (import.meta as any).env.BASE_URL }
);

export default router;
