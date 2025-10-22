import { createBrowserRouter } from "react-router-dom";

import { ErrorPage, LoginPage, SignUpPage } from "@repo/ui/pages";
import PrivateRoutes from "./components/PrivateRoutes";
import MenuManagementPage from "./pages/MenuManagementPage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <PrivateRoutes />,
    errorElement: <ErrorPage />,
    children: [
      { index: true, element: <MenuManagementPage /> },
      { path: "/menu", element: <MenuManagementPage /> },
    ],
  },
  { path: "/login", element: <LoginPage /> },
  { path: "/signup", element: <SignUpPage /> },
]);

export default router;
