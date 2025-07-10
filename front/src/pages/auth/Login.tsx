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
  Paper,
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
} from "@mui/material";
import { useEffect, useState } from "react";
import { useAppThemeContext } from "../../shared/contexts";
import { api } from "../../shared/services";
import { useNavigate } from "react-router-dom";
import { useForm, Controller } from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";

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

  const { themeName } = useAppThemeContext();
  const navigate = useNavigate();

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

      const loginPayload = {
        email: data.email,
        password: data.password,
      };

      const loginResponse = await api.post("/auth/login", loginPayload);

      if (loginResponse.status === 200) navigate("/chats");
    } catch (error) {
      console.error("Erro ao fazer login:", error);
      setSnackbarOpen(true);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Grid
      container
      justifyContent="center"
      alignItems="center"
      height="100vh"
      width="100%"
    >
      <Grid
        size={{ xs: 12, sm: 12, md: 8, lg: 10 }}
        display="flex"
        justifyContent="center"
        alignItems="start"
        borderRadius={10}
      >
        <Paper
          component={Box}
          display="flex"
          flexDirection="row"
          alignItems="center"
          justifyContent="flex-start"
          boxShadow={3}
          width="100%"
          height={mdDown ? "100vh" : "88vh"}
          sx={{
            borderRadius: mdDown ? 0 : 5,
            overflow: "hidden",
            boxShadow: mdDown ? 0 : 10,
            backgroundColor: mdDown ? "transparent" : "primary.paper",
            padding: 4,
            gap: 5,
          }}
        >
          <Box
            width={lgDown ? "100%" : "60%"}
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
              component="form"
              onSubmit={handleSubmit(handleLogin)}
            >
              <Typography
                fontSize={mdDown ? 28 : 32}
                fontWeight={900}
                align="center"
                color={themeName === "light" ? "primary.main" : "white"}
              >
                Access Your Account
              </Typography>

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
                          <MailOutline
                            color="primary"
                            sx={{ width: 25, height: 25 }}
                          />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      mb: 2,
                      "& .MuiOutlinedInput-root": {
                        borderRadius: 3,
                        height: 55,
                        "& fieldset": { borderColor: "primary.main" },
                      },
                    }}
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
                          <LockPersonOutlined
                            color="primary"
                            sx={{ width: 25, height: 25 }}
                          />
                        </InputAdornment>
                      ),
                      endAdornment: (
                        <InputAdornment position="end" sx={{ mr: 1 }}>
                          <IconButton onClick={toggleShowPassword} edge="end">
                            {showPassword === "" ? (
                              <VisibilityOffOutlined
                                sx={{
                                  color: "primary.main",
                                  width: 30,
                                  height: 30,
                                }}
                              />
                            ) : (
                              <VisibilityOutlined
                                sx={{
                                  color: "primary.main",
                                  width: 30,
                                  height: 30,
                                }}
                              />
                            )}
                          </IconButton>
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      mt: -5,
                      "& .MuiOutlinedInput-root": {
                        borderRadius: 3,
                        height: 55,
                        "& fieldset": { borderColor: "primary.main" },
                      },
                    }}
                  />
                )}
              />

              <Typography
                fontSize={smDown ? 12 : 14}
                fontWeight={500}
                align="left"
                ml={0.5}
                mt={smDown ? -4 : -6}
                mb={smDown ? -4 : -6}
              >
                Forgot your password?{" "}
                <Link
                  component="button"
                  type="button"
                  onClick={redirectToResetPassword}
                  underline="hover"
                  sx={{ color: "primary.light" }}
                >
                  Reset Password
                </Link>
              </Typography>

              <Button
                type="submit"
                variant="contained"
                color="primary"
                fullWidth
                disabled={isLoading}
                sx={{
                  marginTop: 0,
                  borderRadius: 2,
                  paddingY: 1.4,
                  marginBottom: smDown ? -4 : -5,
                }}
              >
                {isLoading ? (
                  <CircularProgress size={24} color="inherit" />
                ) : (
                  <Typography fontSize={12} fontWeight={500}>
                    Access
                  </Typography>
                )}
              </Button>

              <Typography
                fontSize={smDown ? 12 : 14}
                fontWeight={500}
                align="left"
                ml={0.5}
              >
                Don’t have an account?{" "}
                <Link
                  href="/register"
                  underline="hover"
                  sx={{ color: "primary.light" }}
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
              <Typography
                color={themeName === "light" ? "primary.main" : "white"}
                sx={{
                  fontFamily: '"Irish Grover", cursive',
                  fontSize: xxlUp ? 150 : 110,
                }}
              >
                Link Line
              </Typography>
            </Box>
          )}
        </Paper>
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
