import { Role } from "@repo/shared/models";
import { PrivateRoutes } from "@repo/ui/components";
import { ErrorPage, LoginPage } from "@repo/ui/pages";
import { createBrowserRouter } from "react-router-dom";
import HomePage from "./pages/HomePage";
import { Layout } from "./pages/Layout";
import { ProfilePage } from "./pages/ProfilePage";
import UsersPage from "./pages/UsersPage";

const router = createBrowserRouter(
  [
    {
      path: "/",
      element: (
        <PrivateRoutes expectedRole={Role.SYSTEM_ADMIN}>
          <Layout />
        </PrivateRoutes>
      ),
      errorElement: <ErrorPage />,
      children: [
        { index: true, element: <HomePage /> },
        { path: "profile", element: <ProfilePage /> },
        { path: "users", element: <UsersPage /> },
      ],
    },
    { path: "login", element: <LoginPage role={Role.SYSTEM_ADMIN} /> },
  ],
);

export default router;
