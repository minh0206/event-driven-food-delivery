import { createBrowserRouter, RouterProvider } from "react-router-dom";

import { ErrorPage, LoginPage, ProfilePage, SignUpPage } from "@repo/ui/pages";
import PrivateRoutes from "./components/PrivateRoutes";
import { HistoryPage } from "./pages/HistoryPage";
import { HomePage } from "./pages/HomePage";

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
  { path: "/signup", element: <SignUpPage /> },
]);

export const Routes = () => {
  return <RouterProvider router={router} />;
};
