import { useAuthStore } from "@repo/shared/hooks";
import { Role } from "@repo/shared/models";
import { LoadingSpinner } from "@repo/ui/components";
import { ReactNode, useEffect } from "react";
import { Navigate } from "react-router-dom";

export const PrivateRoutes = ({
  navigateTo = "login",
  expectedRole,
  children,
}: {
  navigateTo?: string;
  expectedRole?: Role;
  children?: ReactNode;
}) => {
  const { user, isLoading, isInitialized, initialize } = useAuthStore();

  useEffect(() => {
    if (!isInitialized) initialize(expectedRole);
  }, [initialize, isInitialized, expectedRole]);

  if (isLoading && !isInitialized) return <LoadingSpinner />; // If the user is loading, show a loading state.

  if (!isLoading && isInitialized && !user)
    return <Navigate to={navigateTo} replace />; // If the user is not authenticated, redirect to login.

  return <>{children}</>; // If the user is authenticated, render the child route.
};
