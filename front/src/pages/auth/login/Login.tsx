import {
  LockPersonOutlined,
  MailOutline,
  VisibilityOffOutlined,
  VisibilityOutlined,
} from "@mui/icons-material";
import {
  Grid,
  Button,
  Box,
  TextField,
  Typography,
  InputAdornment,
  IconButton,
  useMediaQuery,
  Theme,
  Link,
  Snackbar,
  Alert,
  CircularProgress,
  useTheme,
} from "@mui/material";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm, Controller } from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";

import {
  buttonStyle,
  getContainerStyle,
  getFormStyle,
  getIconStyle,
  getInputStyle,
  getLinkStyle,
  getLogoStyle,
  getTitleStyle,
} from "./Login.styles";
import { api } from "../../../shared/services";

const loginSchema = yup.object().shape({
  email: yup
    .string()
    .email("E-mail inválido")
    .required("O e-mail é obrigatório"),
  password: yup
    .string()
    .min(6, "Mínimo 6 caracteres")
    .required("A senha é obrigatória"),
});

type LoginFormData = yup.InferType<typeof loginSchema>;

export const Login = () => {
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const lgDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("lg"));
  const xxlUp = useMediaQuery((theme: Theme) => theme.breakpoints.up("xxl"));

  const [showPassword, setShowPassword] = useState<"" | "password">("password");
  const [isLoading, setIsLoading] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);

  const navigate = useNavigate();
  const theme = useTheme();

  const inputStyle = getInputStyle(theme);
  const iconsStyles = getIconStyle(theme);
  const linkStyle = getLinkStyle(theme);
  const titleStyle = getTitleStyle(theme, mdDown);
  const formStyle = getFormStyle(smDown);
  const containerStyle = getContainerStyle(mdDown, theme);
  const logoStyle = getLogoStyle(xxlUp, theme);

  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: yupResolver(loginSchema),
  });

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await api.get("/auth/isAuth");
        navigate("/chats", { replace: true });
      } catch (error) {
        console.warn("User is not authenticated", error);
      }
    };

    checkAuth();
  }, [navigate]);

  const toggleShowPassword = () => {
    setShowPassword((prev) => (prev === "password" ? "" : "password"));
  };

  const redirectToResetPassword = () => {
    localStorage.setItem("authSession", "true");
    navigate("/reset-password/email");
  };

  const handleLogin = async (data: LoginFormData) => {
    try {
      setIsLoading(true);
      const loginResponse = await api.post("/auth/login", data);
      if (loginResponse.status === 200) navigate("/chats");
    } catch (error) {
      console.error("Erro ao fazer login:", error);
      setSnackbarOpen(true);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Grid container justifyContent="center" alignItems="center" height="100vh">
      <Grid
        size={{ xs: 12, sm: 12, md: 8, lg: 10 }}
        display="flex"
        justifyContent="center"
        alignItems="start"
        borderRadius={10}
      >
        <Box
          display="flex"
          flexDirection="row"
          alignItems="center"
          justifyContent="flex-start"
          width="100%"
          height={mdDown ? "100vh" : "88vh"}
          sx={containerStyle}
        >
          <Box
            width={lgDown ? "100%" : "60%"}
            display="flex"
            flexDirection="column"
            alignItems="center"
            justifyContent="center"
          >
            <Box
              component="form"
              onSubmit={handleSubmit(handleLogin)}
              sx={formStyle}
            >
              <Typography sx={titleStyle}>Access Your Account</Typography>

              <Controller
                name="email"
                control={control}
                defaultValue=""
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Email"
                    variant="outlined"
                    error={!!errors.email}
                    helperText={errors.email?.message}
                    fullWidth
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start" sx={{ ml: 1 }}>
                          <MailOutline sx={iconsStyles} />
                        </InputAdornment>
                      ),
                    }}
                    sx={inputStyle}
                  />
                )}
              />

              <Controller
                name="password"
                control={control}
                defaultValue=""
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Password"
                    type={showPassword}
                    variant="outlined"
                    error={!!errors.password}
                    helperText={errors.password?.message}
                    fullWidth
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start" sx={{ ml: 1 }}>
                          <LockPersonOutlined sx={iconsStyles} />
                        </InputAdornment>
                      ),
                      endAdornment: (
                        <InputAdornment position="end" sx={{ mr: 1 }}>
                          <IconButton onClick={toggleShowPassword} edge="end">
                            {showPassword === "" ? (
                              <VisibilityOffOutlined sx={iconsStyles} />
                            ) : (
                              <VisibilityOutlined sx={iconsStyles} />
                            )}
                          </IconButton>
                        </InputAdornment>
                      ),
                    }}
                    sx={inputStyle}
                  />
                )}
              />

              <Typography
                fontSize={smDown ? 12 : 14}
                align="left"
                ml={0.5}
                color={theme.palette.text.primary}
              >
                Forgot your password?{" "}
                <Link
                  type="button"
                  onClick={redirectToResetPassword}
                  underline="hover"
                  fontWeight={700}
                  sx={linkStyle}
                >
                  Reset Password
                </Link>
              </Typography>

              <Button
                type="submit"
                variant="contained"
                fullWidth
                disabled={isLoading}
                sx={buttonStyle}
              >
                {isLoading ? (
                  <CircularProgress size={24} color="inherit" />
                ) : (
                  "Access"
                )}
              </Button>

              <Typography
                fontSize={smDown ? 12 : 14}
                align="left"
                ml={0.5}
                color={theme.palette.text.primary}
              >
                Don’t have an account?{" "}
                <Link
                  href="/register"
                  underline="hover"
                  sx={linkStyle}
                  fontWeight={700}
                >
                  Sign Up
                </Link>
              </Typography>
            </Box>
          </Box>

          {!lgDown && (
            <Box
              width="70%"
              display="flex"
              flexDirection="column"
              alignItems="center"
              justifyContent="center"
              mr={5}
            >
              <Typography sx={logoStyle}>Link Line</Typography>
            </Box>
          )}
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
          Invalid Credentials
        </Alert>
      </Snackbar>
    </Grid>
  );
};
