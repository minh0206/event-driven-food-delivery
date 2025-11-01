import { useAuthStore } from "@repo/shared/hooks";
import { useEffect } from "react";
import { Navigate } from "react-router-dom";
import { Layout } from "../pages/Layout";

const PrivateRoutes = () => {
  const { user, isLoading, isInitialized, initialize } = useAuthStore();

  useEffect(() => {
    if (!isInitialized) initialize();
  }, [initialize]);

  // If the user is loading, show a loading state.
  if (isLoading || !isInitialized) return <div>Loading user data...</div>;

  // If the user is not authenticated, redirect to login.
  if (!user) return <Navigate to="/login" replace />;

  // If the user is authenticated, render the child route.
  return <Layout />;
};
export default PrivateRoutes;