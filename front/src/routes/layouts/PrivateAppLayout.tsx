import { Navigate, Outlet } from "react-router-dom";
import { useState, useEffect } from "react";
import { api } from "../../shared/services";
import { Box, CircularProgress } from "@mui/material";

export const PrivateAppLayout = () => {
  const [authenticated, setAuthenticated] = useState<boolean | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await api.get("/auth/isAuth");
        setAuthenticated(true);
      } catch (err) {
        setAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  if (loading) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        height="100vh"
        width="100%"
      >
        <CircularProgress color="primary" size={50} />
      </Box>
    );
  }

  return authenticated ? <Outlet /> : <Navigate to="/login" />;
};
