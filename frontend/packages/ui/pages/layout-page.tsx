import { Outlet } from "react-router-dom";
import { NavBar } from "../components/nav-bar";

export const Layout = () => {
  return (
    <>
      <NavBar />
      <Outlet />
    </>
  );
};
