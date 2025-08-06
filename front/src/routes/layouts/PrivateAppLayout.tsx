import { Navigate, Outlet } from "react-router-dom";
import { useState, useEffect } from "react";
import { api } from "../../shared/services";
import { Box, CircularProgress } from "@mui/material";
import { useUsername } from "../../shared/contexts/UsernameContext";

export const PrivateAppLayout = () => {
  const [authenticated, setAuthenticated] = useState<boolean | null>(null);
  const [loading, setLoading] = useState(true);
  const { setUsername } = useUsername();

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await api.get("/auth/isAuth");
        setAuthenticated(true);
        setUsername(response.data.username);
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

  return authenticated ? <Outlet /> : <Navigate to="/" />;
};
