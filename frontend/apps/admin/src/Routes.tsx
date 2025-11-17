import { ErrorPage, LoginPage } from "@repo/ui/pages";
import { createBrowserRouter } from "react-router-dom";
import PrivateRoutes from "./components/PrivateRoutes";
import HomePage from "./pages/HomePage";
import { ProfilePage } from "./pages/ProfilePage";

const router = createBrowserRouter(
  [
    {
      path: "/",
      element: <PrivateRoutes />,
      errorElement: <ErrorPage />,
      children: [
        { index: true, element: <HomePage /> },
        { path: "profile", element: <ProfilePage /> },
      ],
    },
    { path: "login", element: <LoginPage showSignup={false} /> },
  ],
  { basename: (import.meta as any).env.BASE_URL }
);

export default router;
