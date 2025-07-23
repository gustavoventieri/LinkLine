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
  IconButton,
  useTheme,
} from "@mui/material";
import { useEffect, useRef, useState } from "react";
import {
  LockPersonOutlined,
  VisibilityOffOutlined,
  VisibilityOutlined,
} from "@mui/icons-material";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { useNavigate } from "react-router-dom";

import { api } from "../../../../shared/services";
import {
  getContainerStyle,
  getIconStyle,
  getInputStyle,
} from "./ResetPasswordUpdate.styles";

const schema = yup.object({
  password: yup
    .string()
    .required("Password is required")
    .min(6, "Password must be at least 6 characters"),
  confirmPassword: yup
    .string()
    .required("Confirm your password")
    .oneOf([yup.ref("password")], "Passwords must match"),
});

export const ResetPasswordUpdate = () => {
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const navigate = useNavigate();
  const theme = useTheme();

  const emailRef = useRef<HTMLInputElement>(null);
  const [showPassword, setShowPassword] = useState<"" | "password">("password");
  const [showConfirmPassword, setShowConfirmPassword] = useState<
    "" | "password"
  >("password");

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isResendDisabled, setIsResendDisabled] = useState(false);
  const [resendTimer, setResendTimer] = useState(30);

  const inputStyle = getInputStyle(theme);
  const containerStyle = getContainerStyle(mdDown, theme);
  const iconStyle = getIconStyle(theme);

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
    const email = localStorage.getItem("email");
    if (!sessionToken || !email) {
      console.warn("Nenhum token de sessão encontrado ou email.");
      return;
    }

    setIsLoading(true);
    try {
      const payload = {
        email: email,
        password: data.confirmPassword,
      };

      const response = await api.put("/auth/reset-password/update", payload);

      if (response.status === 200) {
        localStorage.removeItem("authSession");
        localStorage.removeItem("email");

        navigate("/login");
      }

      navigate("/login");
    } catch (error: any) {
      console.error("Erro ao verificar o código:", error);
      setSnackbarOpen(true);
    } finally {
      setIsLoading(false);
    }
  };

  const toggleShowPassword = () => {
    setShowPassword((prev) => (prev === "password" ? "" : "password"));
  };

  const toggleConfirmShowPassword = () => {
    setShowConfirmPassword((prev) => (prev === "password" ? "" : "password"));
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
        borderRadius={10}
      >
        <Box
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          boxShadow={3}
          width="100%"
          height={"60vh"}
          sx={containerStyle}
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
                  fontSize={mdDown ? 20 : 40}
                  fontWeight={900}
                  mb={-3}
                  align="center"
                  color={
                    theme.palette.mode === "light"
                      ? theme.palette.primary.main
                      : "white"
                  }
                >
                  Update Your Password
                </Typography>

                <TextField
                  {...register("password")}
                  label="Password"
                  type={showPassword}
                  variant="outlined"
                  fullWidth
                  error={!!errors.password}
                  helperText={errors.password?.message as string}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start" sx={{ ml: 1 }}>
                        <LockPersonOutlined sx={iconStyle} />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position="end" sx={{ mr: 1 }}>
                        <IconButton onClick={toggleShowPassword}>
                          {showPassword === "" ? (
                            <VisibilityOffOutlined sx={iconStyle} />
                          ) : (
                            <VisibilityOutlined sx={iconStyle} />
                          )}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                  sx={inputStyle}
                />
                <TextField
                  {...register("confirmPassword")}
                  label="Confirm Password"
                  type={showConfirmPassword}
                  variant="outlined"
                  fullWidth
                  error={!!errors.confirmPassword}
                  helperText={errors.confirmPassword?.message as string}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start" sx={{ ml: 1 }}>
                        <LockPersonOutlined sx={iconStyle} />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position="end" sx={{ mr: 1 }}>
                        <IconButton onClick={toggleConfirmShowPassword}>
                          {showConfirmPassword === "" ? (
                            <VisibilityOffOutlined sx={iconStyle} />
                          ) : (
                            <VisibilityOutlined sx={iconStyle} />
                          )}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                  sx={inputStyle}
                />

                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  fullWidth
                  sx={{
                    marginTop: mdDown ? -1 : -4,
                    borderRadius: 3,
                    paddingY: 1.8,
                    "&:hover": {
                      backgroundColor: "primary.dark",
                    },
                  }}
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <CircularProgress size={26} sx={{ color: "white" }} />
                  ) : (
                    <Typography fontSize={14} fontWeight={500}>
                      Update
                    </Typography>
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
