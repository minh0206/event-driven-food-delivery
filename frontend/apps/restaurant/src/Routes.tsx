import { createBrowserRouter } from "react-router-dom";

import { ErrorPage, LoginPage, ProfilePage, SignUpPage } from "@repo/ui/pages";
import PrivateRoutes from "./components/PrivateRoutes";
import MenuManagementPage from "./pages/MenuManagementPage";
import OrderManagementPage from "./pages/OrderManagementPage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <PrivateRoutes />,
    errorElement: <ErrorPage />,
    children: [
      { index: true, element: <OrderManagementPage /> },
      { path: "/menu", element: <MenuManagementPage /> },
      { path: "/profile", element: <ProfilePage /> },
    ],
  },
  { path: "/login", element: <LoginPage /> },
  { path: "/signup", element: <SignUpPage /> },
]);

export default router;
