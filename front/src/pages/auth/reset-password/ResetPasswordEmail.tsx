import {
  Box,
  Button,
  Grid,
  Snackbar,
  TextField,
  Theme,
  Typography,
  useMediaQuery,
  Alert,
  CircularProgress,
  InputAdornment,
  useTheme,
} from "@mui/material";
import { useEffect, useRef, useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { useNavigate } from "react-router-dom";
import { MailOutline } from "@mui/icons-material";

import { api } from "../../../shared/services";

const schema = yup.object({
  email: yup.string().email().required("Email is required"),
});

export const ResetPasswordEmail = () => {
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));

  const navigate = useNavigate();
  const emailRef = useRef<HTMLInputElement>(null);
  const theme = useTheme();

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isResendDisabled, setIsResendDisabled] = useState(false);
  const [resendTimer, setResendTimer] = useState(30);

  type FormData = yup.InferType<typeof schema>;

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    let timer: any;
    if (isResendDisabled && resendTimer > 0) {
      timer = setTimeout(() => {
        setResendTimer((prev) => prev - 1);
      }, 1000);
    } else if (resendTimer === 0) {
      setIsResendDisabled(false);
      setResendTimer(30);
    }
    return () => clearTimeout(timer);
  }, [isResendDisabled, resendTimer]);

  useEffect(() => {
    emailRef.current?.focus();
  }, []);

  const onSubmit = async (data: FormData) => {
    const sessionToken = localStorage.getItem("authSession");

    if (!sessionToken) {
      console.warn("Nenhum token de sessão encontrado.");
      return;
    }

    setIsLoading(true);
    try {
      const payload = {
        email: data.email,
      };

      await api.post("/auth/reset-password/send", payload);

      localStorage.setItem("email", data.email);

      navigate("/reset-password/code");
    } catch (error: any) {
      console.error("Erro ao verificar o código:", error);
      setSnackbarOpen(true);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Grid
      container
      spacing={0}
      justifyContent="center"
      alignItems="center"
      height="100vh"
      width="100%"
      px={2}
    >
      <Grid
        size={{ xs: 12, sm: 12, md: 8, lg: 5 }}
        display="flex"
        justifyContent="center"
        alignItems="start"
      >
        <Box
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          boxShadow={3}
          height={"60vh"}
          sx={{
            borderRadius: mdDown ? 0 : 3,
            overflowY: "auto",
            boxShadow: mdDown ? 0 : 4,
            maxWidth: 500,
            px: 2,
            backgroundColor: mdDown ? "none" : theme.palette.background.paper,
          }}
        >
          <form onSubmit={handleSubmit(onSubmit)} style={{ width: "100%" }}>
            <Box
              width="100%"
              display="flex"
              flexDirection="column"
              alignItems="center"
              justifyContent="center"
            >
              <Box
                width="100%"
                display="flex"
                flexDirection="column"
                gap={smDown ? 6 : 8}
                paddingX={smDown ? 2 : 5}
              >
                <Typography
                  fontSize={mdDown ? 28 : 30}
                  fontWeight={900}
                  mb={-5}
                  align="center"
                  color={
                    theme.palette.mode === "light" ? "primary.main" : "white"
                  }
                >
                  Enter Your Email
                </Typography>

                <Typography
                  fontSize={mdDown ? 14 : 16}
                  mb={-2}
                  fontWeight={400}
                  align="center"
                  color={theme.palette.mode === "light" ? "black" : "white"}
                >
                  A code will be sent to your email address for resetting your
                  password.
                </Typography>

                <TextField
                  id="email"
                  label="Email"
                  {...register("email")}
                  inputRef={emailRef}
                  error={!!errors.email}
                  helperText={errors.email?.message}
                  variant="outlined"
                  fullWidth
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start" sx={{ ml: 1 }}>
                        <MailOutline
                          sx={{ color: theme.palette.primary.main }}
                        />
                      </InputAdornment>
                    ),
                  }}
                />

                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  fullWidth
                  sx={{
                    paddingY: 1.5,
                  }}
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <CircularProgress size={26} sx={{ color: "white" }} />
                  ) : (
                    <Typography fontWeight={500}>Continue</Typography>
                  )}
                </Button>
              </Box>
            </Box>
          </form>
        </Box>
      </Grid>

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={() => setSnackbarOpen(false)}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={() => setSnackbarOpen(false)}
          severity="error"
          variant="filled"
          sx={{ width: "100%", color: "white", backgroundColor: "red" }}
        >
          Unable to continue. Please try again.
        </Alert>
      </Snackbar>
    </Grid>
  );
};
