import {
  LockPersonOutlined,
  MailOutline,
  PersonOutline,
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
  CircularProgress,
  Snackbar,
  Alert,
} from "@mui/material";
import { useEffect, useRef, useState } from "react";
import { useAppThemeContext } from "../../shared/contexts";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { api } from "../../shared/services";
import { useNavigate } from "react-router-dom";
import { useEmail } from "../../shared/contexts/EmailContext";

// Definindo o schema de validação
const schema = yup.object().shape({
  username: yup.string().required("Username is required"),
  email: yup.string().email("Invalid email").required("Email is required"),
  password: yup
    .string()
    .required("Password is required")
    .min(6, "Password must be at least 6 characters"),
  confirmPassword: yup
    .string()
    .required("Confirm your password")
    .oneOf([yup.ref("password")], "Passwords must match"),
});

export const Register = () => {
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const lgDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("lg"));
  const xxlUp = useMediaQuery((theme: Theme) => theme.breakpoints.up("xxl"));
  const navigate = useNavigate();
  const usernameRef = useRef<HTMLInputElement>(null);
  const { themeName } = useAppThemeContext();
  const { setEmail } = useEmail();

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [showPassword, setShowPassword] = useState<"" | "password">("password");
  const [showConfirmPassword, setShowConfirmPassword] = useState<
    "" | "password"
  >("password");

  const [loading, setLoading] = useState(false); // Estado para controle do loading

  type FormData = yup.InferType<typeof schema>;

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await api.get("/auth/isAuth");
        navigate("/chats", { replace: true });
      } catch (error) {}
    };

    checkAuth();
  }, [navigate]);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data: FormData) => {
    try {
      setLoading(true); // Ativar o loading ao começar a requisição
      const payload = {
        name: data.username.toLowerCase(),
        email: data.email,
        password: data.password,
      };

      const response = await api.post("/auth/email-confirmation/send", payload);

      if (response.status === 200) {
        await setEmail(data.email);
        localStorage.setItem("authSession", "true");
        setTimeout(() => {
          navigate("/email-verification", { replace: true });
        }, 1000);
      }
    } catch (error: any) {
      console.error(
        "Erro ao enviar código:",
        error.response?.data.error || error.message
      );
      setErrorMessage(error.response?.data.message);
      setSnackbarOpen(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    usernameRef.current?.focus();
  }, []);

  const inputStyle = {
    "& .MuiOutlinedInput-root": {
      borderRadius: 3,
      height: 55,
      "& fieldset": {
        borderColor: "primary.main",
      },
    },
    "& .MuiInputLabel-root.Mui-focused": {
      color: "primary.main",
    },
    "& .MuiInputLabel-shrink": {
      color: themeName === "light" ? "primary.text" : "white",
    },
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
      justifyContent="center"
      alignItems="center"
      height="100vh"
      width="100%"
    >
      <Grid size={{ xs: 12, sm: 12, md: 8, lg: 10 }}>
        <Paper
          component={Box}
          display="flex"
          flexDirection={mdDown ? "column" : "row"}
          alignItems="stretch"
          justifyContent="center"
          height={mdDown ? "100vh" : "88vh"}
          width="100%"
          sx={{
            borderRadius: mdDown ? 0 : 5,
            overflow: "hidden",
            boxShadow: mdDown ? 0 : 10,
            backgroundColor: mdDown ? "primary.default" : "primary.paper",
          }}
        >
          {!lgDown && (
            <Box
              width="50%"
              display="flex"
              flexDirection="column"
              alignItems="center"
              justifyContent="center"
              ml={4}
            >
              <Typography
                color={themeName === "light" ? "primary.main" : "white"}
                sx={{
                  fontFamily: '"Irish Grover", cursive',
                  fontSize: xxlUp ? 150 : 90,
                }}
              >
                Link Line
              </Typography>
            </Box>
          )}

          <Box
            flex={1}
            display="flex"
            flexDirection="column"
            justifyContent="center"
            alignItems="center"
            px={5}
            width="100%"
            gap={5}
          >
            <Typography
              fontSize={mdDown ? 26 : 30}
              fontWeight={900}
              mt={mdDown ? -8 : 0}
              align="center"
              color={themeName === "light" ? "primary.main" : "white"}
            >
              Register Account
            </Typography>

            <Box
              component="form"
              onSubmit={handleSubmit(onSubmit)}
              width="90%"
              display="flex"
              flexDirection="column"
              gap={4}
            >
              <TextField
                {...register("username")}
                inputRef={usernameRef}
                label="Username"
                variant="outlined"
                fullWidth
                error={!!errors.username}
                helperText={errors.username?.message as string}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start" sx={{ ml: 1 }}>
                      <PersonOutline
                        color="primary"
                        sx={{ width: 26, height: 26 }}
                      />
                    </InputAdornment>
                  ),
                }}
                sx={inputStyle}
              />
              <TextField
                {...register("email")}
                label="Email"
                variant="outlined"
                fullWidth
                error={!!errors.email}
                helperText={errors.email?.message as string}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start" sx={{ ml: 1 }}>
                      <MailOutline
                        color="primary"
                        sx={{ width: 26, height: 26 }}
                      />
                    </InputAdornment>
                  ),
                }}
                sx={inputStyle}
              />
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
                      <LockPersonOutlined
                        color="primary"
                        sx={{ width: 26, height: 26 }}
                      />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end" sx={{ mr: 1 }}>
                      <IconButton onClick={toggleShowPassword}>
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
                      <LockPersonOutlined
                        color="primary"
                        sx={{ width: 26, height: 26 }}
                      />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end" sx={{ mr: 1 }}>
                      <IconButton onClick={toggleConfirmShowPassword}>
                        {showConfirmPassword === "" ? (
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
                sx={inputStyle}
              />

              <Button
                type="submit"
                variant="contained"
                disabled={loading}
                fullWidth
                sx={{
                  borderRadius: 3,
                  py: 1.4,
                  "&:hover": {
                    backgroundColor: "primary.dark",
                  },
                }}
              >
                {loading ? (
                  <CircularProgress size={24} sx={{ color: "white" }} />
                ) : (
                  "Register"
                )}
              </Button>

              <Typography fontSize={14} fontWeight={500} align="left" mt={-2}>
                Already have an account?{" "}
                <Link
                  fontWeight={500}
                  href="/login"
                  color="primary.light"
                  underline="hover"
                >
                  Sign In
                </Link>
              </Typography>
            </Box>
          </Box>
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
          {errorMessage}
        </Alert>
      </Snackbar>
    </Grid>
  );
};
