import { createBrowserRouter, RouterProvider } from "react-router-dom";

import { Role } from "@repo/shared/models";
import { ErrorPage, LoginPage, SignUpPage } from "@repo/ui/pages";
import PrivateRoutes from "./components/PrivateRoutes";
import { HistoryPage } from "./pages/HistoryPage";
import { HomePage } from "./pages/HomePage";
import { ProfilePage } from "./pages/ProfilePage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <PrivateRoutes />,
    errorElement: <ErrorPage />,
    children: [
      { index: true, element: <HomePage /> },
      { path: "/history", element: <HistoryPage /> },
      { path: "/profile", element: <ProfilePage /> },
    ],
  },
  { path: "/login", element: <LoginPage /> },
  { path: "/signup", element: <SignUpPage role={Role.DRIVER} /> },
]);

export const Routes = () => {
  return <RouterProvider router={router} />;
};
