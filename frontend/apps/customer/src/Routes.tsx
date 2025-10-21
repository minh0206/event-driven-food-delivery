import { createBrowserRouter } from "react-router-dom";

import {
  ErrorPage,
  Layout,
  LoginPage,
  RestaurantDetailPage,
  SignUpPage,
} from "@repo/ui";
import HomePage from "./pages/HomePage";
import OrderListPage from "./pages/OrderListPage";
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
