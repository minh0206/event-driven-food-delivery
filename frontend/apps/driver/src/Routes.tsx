import { createBrowserRouter, RouterProvider } from "react-router-dom";

import { Role } from "@repo/shared/models";
import { PrivateRoutes } from "@repo/ui/components";
import { ErrorPage, LoginPage, SignUpPage } from "@repo/ui/pages";
import { HistoryPage } from "./pages/HistoryPage";
import { HomePage } from "./pages/HomePage";
import { Layout } from "./pages/Layout";
import { ProfilePage } from "./pages/ProfilePage";

const router = createBrowserRouter(
  [
    {
      path: "/",
      element: (
        <PrivateRoutes role={Role.DRIVER}>
          <Layout />
        </PrivateRoutes>
      ),
      errorElement: <ErrorPage />,
      children: [
        { index: true, element: <HomePage /> },
        { path: "history", element: <HistoryPage /> },
        { path: "profile", element: <ProfilePage /> },
      ],
    },
    { path: "login", element: <LoginPage role={Role.DRIVER} /> },
    { path: "signup", element: <SignUpPage role={Role.DRIVER} /> },
  ],
  { basename: (import.meta as any).env.BASE_URL }
);

export const Routes = () => {
  return <RouterProvider router={router} />;
};
