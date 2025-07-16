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
  useTheme,
} from "@mui/material";
import { useEffect, useRef, useState } from "react";
import { useForm } from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";

import { api } from "../../../shared/services";
import { useNavigate } from "react-router-dom";
import { useEmail } from "../../../shared/contexts/EmailContext";
import {
  getContainerStyle,
  getIconStyle,
  getInputStyle,
  getLogoStyle,
  getTitleStyle,
} from "./Register.styles";

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

type FormData = yup.InferType<typeof schema>;

export const Register = () => {
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const lgDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("lg"));
  const xxlUp = useMediaQuery((theme: Theme) => theme.breakpoints.up("xxl"));
  const usernameRef = useRef<HTMLInputElement>(null);
  const { setEmail } = useEmail();
  const navigate = useNavigate();
  const theme = useTheme();

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [showPassword, setShowPassword] = useState<"" | "password">("password");
  const [showConfirmPassword, setShowConfirmPassword] = useState<
    "" | "password"
  >("password");
  const [loading, setLoading] = useState(false);

  const inputStyle = getInputStyle(theme);
  const iconStyle = getIconStyle(theme);
  const contaienrStyle = getContainerStyle(mdDown, theme);
  const logoStyle = getLogoStyle(xxlUp, theme);
  const titleStyle = getTitleStyle(theme);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await api.get("/auth/isAuth");
        navigate("/chats", { replace: true });
      } catch {}
    };
    checkAuth();
    usernameRef.current?.focus();
  }, [navigate]);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({ resolver: yupResolver(schema) });

  const onSubmit = async (data: FormData) => {
    try {
      setLoading(true);
      const response = await api.post("/auth/email-confirmation/send", {
        username: data.username.toLowerCase(),
        email: data.email,
        password: data.password,
      });

      if (response.status === 200) {
        setEmail(data.email);
        localStorage.setItem("authSession", "true");
        setTimeout(() => navigate("/email-verification"), 1000);
      }
    } catch (error: any) {
      setErrorMessage(error.response?.data.message || "Erro desconhecido");
      setSnackbarOpen(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Grid container justifyContent="center" alignItems="center" height="100vh">
      <Grid size={{ xs: 12, sm: 12, md: 8, lg: 10 }}>
        <Box
          display="flex"
          flexDirection={mdDown ? "column" : "row"}
          justifyContent="center"
          alignItems="stretch"
          height={mdDown ? "100vh" : "88vh"}
          sx={contaienrStyle}
        >
          {!lgDown && (
            <Box
              width="50%"
              display="flex"
              flexDirection="column"
              justifyContent="center"
              alignItems="center"
              ml={4}
            >
              <Typography sx={logoStyle}>Link Line</Typography>
            </Box>
          )}

          <Box
            flex={1}
            display="flex"
            flexDirection="column"
            justifyContent="center"
            alignItems="center"
            px={5}
            gap={5}
          >
            <Typography
              fontSize={mdDown ? 26 : 30}
              fontWeight={900}
              mt={mdDown ? -8 : 0}
              align="center"
              color={titleStyle}
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
              {[
                {
                  name: "username",
                  label: "Username",
                  icon: <PersonOutline sx={iconStyle} />,
                  ref: usernameRef,
                },
                {
                  name: "email",
                  label: "Email",
                  icon: <MailOutline sx={iconStyle} />,
                },
                {
                  name: "password",
                  label: "Password",
                  icon: <LockPersonOutlined sx={iconStyle} />,
                  type: showPassword,
                  endIcon: (
                    <IconButton
                      onClick={() =>
                        setShowPassword((p) =>
                          p === "password" ? "" : "password"
                        )
                      }
                    >
                      {showPassword === "" ? (
                        <VisibilityOffOutlined sx={iconStyle} />
                      ) : (
                        <VisibilityOutlined sx={iconStyle} />
                      )}
                    </IconButton>
                  ),
                },
                {
                  name: "confirmPassword",
                  label: "Confirm Password",
                  icon: <LockPersonOutlined sx={iconStyle} />,
                  type: showConfirmPassword,
                  endIcon: (
                    <IconButton
                      onClick={() =>
                        setShowConfirmPassword((p) =>
                          p === "password" ? "" : "password"
                        )
                      }
                    >
                      {showConfirmPassword === "" ? (
                        <VisibilityOffOutlined sx={iconStyle} />
                      ) : (
                        <VisibilityOutlined sx={iconStyle} />
                      )}
                    </IconButton>
                  ),
                },
              ].map((field, i) => (
                <TextField
                  key={i}
                  {...register(field.name as keyof FormData)}
                  inputRef={field.ref}
                  label={field.label}
                  type={field.type || "text"}
                  variant="outlined"
                  fullWidth
                  error={!!errors[field.name as keyof FormData]}
                  helperText={
                    errors[field.name as keyof FormData]?.message as string
                  }
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start" sx={{ ml: 1 }}>
                        {field.icon}
                      </InputAdornment>
                    ),
                    endAdornment: field.endIcon && (
                      <InputAdornment position="end">
                        {field.endIcon}
                      </InputAdornment>
                    ),
                  }}
                  sx={inputStyle}
                />
              ))}

              <Button
                type="submit"
                variant="contained"
                disabled={loading}
                fullWidth
                sx={{
                  borderRadius: 3,
                  py: 1.4,
                  "&:hover": {
                    backgroundColor: theme.palette.primary.dark,
                  },
                }}
              >
                {loading ? (
                  <CircularProgress size={24} sx={{ color: "white" }} />
                ) : (
                  "Register"
                )}
              </Button>

              <Typography
                align="left"
                mt={-2}
                sx={{ color: theme.palette.text.primary }}
              >
                Already have an account?{" "}
                <Link
                  href="/login"
                  fontWeight={500}
                  underline="hover"
                  color={theme.palette.primary.light}
                >
                  Sign In
                </Link>
              </Typography>
            </Box>
          </Box>
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
          {errorMessage}
        </Alert>
      </Snackbar>
    </Grid>
  );
};
