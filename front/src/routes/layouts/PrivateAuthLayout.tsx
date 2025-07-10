import { Navigate, Outlet } from "react-router-dom";

export const PrivateAuthLayout = () => {
  const token = localStorage.getItem("authSession");

  return token === "true" ? <Outlet /> : <Navigate to="/login" />;
};
