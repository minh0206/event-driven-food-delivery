// apps/customer/src/Routes.tsx
import { Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
// ... import other pages

const AppRoutes = () => (
  <Routes>
    <Route path="/" element={<HomePage />} />
    <Route path="/login" element={<LoginPage />} />
    {/* Define other routes here */}
  </Routes>
);
export default AppRoutes;
