import { Role } from "@repo/shared/models";
import { PrivateRoutes } from "@repo/ui/components";
import { ErrorPage, LoginPage } from "@repo/ui/pages";
import { createBrowserRouter } from "react-router-dom";
import HomePage from "./pages/HomePage";
import { Layout } from "./pages/Layout";
import { ProfilePage } from "./pages/ProfilePage";

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
      ],
    },
    { path: "login", element: <LoginPage role={Role.SYSTEM_ADMIN} /> },
  ],
  { basename: (import.meta as any).env.BASE_URL }
);

export default router;
