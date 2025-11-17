import { Outlet } from "react-router-dom";
import { AdminNavBar } from "../components/AdminNavBar";

export const Layout = () => {
  return (
    <>
      <AdminNavBar />
      <Outlet />
    </>
  );
};
