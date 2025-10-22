import { createBrowserRouter } from "react-router-dom";

import { ErrorPage, LoginPage, SignUpPage } from "@repo/ui/pages";
import HomePage from "./pages/HomePage";
import { Layout } from "./pages/Layout";
import OrderListPage from "./pages/OrderListPage";
import { RestaurantDetailPage } from "./pages/RestaurantDetailPage";
// ... import other pages

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    errorElement: <ErrorPage />,
    children: [
      { index: true, element: <HomePage /> },
      { path: "restaurants/:id", element: <RestaurantDetailPage /> },
      { path: "orders", element: <OrderListPage /> },
    ],
  },
  { path: "/login", element: <LoginPage /> },
  { path: "/signup", element: <SignUpPage /> },
]);

export default router;
