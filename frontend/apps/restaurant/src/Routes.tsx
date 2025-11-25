import { createBrowserRouter } from "react-router-dom";

import { Role } from "@repo/shared/models";
import { ErrorPage, LoginPage, SignUpPage } from "@repo/ui/pages";

import { PrivateRoutes } from "@repo/ui/components";
import { Layout } from "./pages/Layout";
import MenuManagementPage from "./pages/MenuManagementPage";
import OrderManagementPage from "./pages/OrderManagementPage";
import { ProfilePage } from "./pages/ProfilePage";

const router = createBrowserRouter(
  [
    {
      path: "/",
      element: (
        <PrivateRoutes>
          <Layout />
        </PrivateRoutes>
      ),
      errorElement: <ErrorPage />,
      children: [
        { index: true, element: <OrderManagementPage /> },
        { path: "menu", element: <MenuManagementPage /> },
        { path: "profile", element: <ProfilePage /> },
      ],
    },
    { path: "login", element: <LoginPage role={Role.RESTAURANT_ADMIN} /> },
    { path: "signup", element: <SignUpPage role={Role.RESTAURANT_ADMIN} /> },
  ],
  { basename: (import.meta as any).env.BASE_URL }
);

export default router;
