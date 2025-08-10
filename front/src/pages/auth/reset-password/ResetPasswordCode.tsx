import {
  Box,
  Button,
  Grid,
  Link,
  Snackbar,
  TextField,
  Theme,
  Typography,
  useMediaQuery,
  Alert,
  CircularProgress,
  useTheme,
} from "@mui/material";
import { useEffect, useRef, useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";

import { useAppThemeContext } from "../../../shared/contexts";
import { useNavigate } from "react-router-dom";
import { api } from "../../../shared/services";

const schema = yup.object({
  verificationCode: yup
    .string()
    .required("Code is required")
    .length(6, "Code must be 6 digits"),
});

type FormData = yup.InferType<typeof schema>;

export const ResetPasswordCode = () => {
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const navigate = useNavigate();
  const { themeName } = useAppThemeContext();
  const theme = useTheme();

  const inputsRef = useRef<Array<HTMLInputElement | null>>([]);
  const [code, setCode] = useState<string[]>(["", "", "", "", "", ""]);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isResendDisabled, setIsResendDisabled] = useState(false);
  const [resendTimer, setResendTimer] = useState(30);

  const {
    register,
    handleSubmit,
    clearErrors,
    setValue,
    formState: { errors },
  } = useForm<FormData>({
    resolver: yupResolver(schema),
    mode: "onChange",
  });

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await api.get("/auth/isAuth");
        navigate("/chats", { replace: true });
      } catch {}
    };
    checkAuth();
  }, []);

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

  const handleInputChange = (index: number, value: string) => {
    if (!/^\d?$/.test(value)) return;

    const newCode = [...code];
    newCode[index] = value;
    setCode(newCode);

    setValue("verificationCode", newCode.join(""));
    clearErrors("verificationCode");

    if (value && index < 5) {
      inputsRef.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent, index: number) => {
    if (e.key === "Backspace" && !code[index] && index > 0) {
      const newCode = [...code];
      newCode[index - 1] = "";
      setCode(newCode);
      setValue("verificationCode", newCode.join(""));
      inputsRef.current[index - 1]?.focus();
    }
  };

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
        code: data.verificationCode,
      };

      const response = await api.post("/auth/reset-password/verify", payload);
      if (response.status === 200) navigate("/reset-password/update");
    } catch (error) {
      console.error("Erro ao verificar o código:", error);
      setSnackbarOpen(true);
    } finally {
      setIsLoading(false);
    }
  };

  const onResendCode = async () => {
    const sessionToken = localStorage.getItem("authSession");
    const email = localStorage.getItem("email");
    if (!sessionToken || !email) {
      console.warn("Nenhum token de sessão encontrado ou email.");
      return;
    }

    setIsResendDisabled(true);
    try {
      await api.post("/auth/reset-password/resend", { email: email });
    } catch (error) {
      console.error("Erro ao reenviar o código:", error);
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
        alignItems="center"
      >
        <Box
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          boxShadow={3}
          maxWidth={500}
          height={"50vh"}
          sx={{
            borderRadius: smDown ? 0 : 3,
            overflow: "hidden",
            boxShadow: mdDown ? 0 : 3,
            backgroundColor: mdDown ? "none" : theme.palette.background.paper,
          }}
        >
          <form onSubmit={handleSubmit(onSubmit)} style={{ width: "100%" }}>
            <input type="hidden" {...register("verificationCode")} />

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
                  mb={mdDown ? -2 : -5}
                  align="center"
                  color={themeName === "light" ? "primary.main" : "white"}
                >
                  Reset Password
                </Typography>

                <Typography
                  fontSize={mdDown ? 14 : 18}
                  mb={mdDown ? -2 : -5}
                  fontWeight={400}
                  align="center"
                  color={themeName === "light" ? "black" : "white"}
                >
                  Check your email — we’ve sent you a code to reset your
                  password.
                </Typography>

                <Box display="flex" justifyContent="center" gap={1}>
                  {[0, 1, 2, 3, 4, 5].map((index) => (
                    <TextField
                      key={index}
                      inputRef={(el) => (inputsRef.current[index] = el)}
                      inputProps={{
                        maxLength: 1,
                        inputMode: "numeric",
                        pattern: "[0-9]*",
                        style: { textAlign: "center" },
                      }}
                      sx={{
                        width: {
                          xs: "2.5rem",
                          sm: "3.0rem",
                          md: "3.0rem",
                        },
                        "& input": {
                          fontSize: {
                            xs: "2rem",
                            sm: "2.5rem",
                            md: "2.5rem",
                          },
                          padding: 0,
                        },
                      }}
                      value={code[index] || ""}
                      onChange={(e) => handleInputChange(index, e.target.value)}
                      onKeyDown={(e) => handleKeyDown(e, index)}
                      error={!!errors.verificationCode}
                    />
                  ))}
                </Box>

                {errors.verificationCode && (
                  <Typography
                    variant="caption"
                    color="error"
                    align="center"
                    sx={{ mt: -6, mr: 1 }}
                  >
                    {errors.verificationCode.message}
                  </Typography>
                )}

                <Typography
                  align="left"
                  ml={0.5}
                  mt={mdDown ? -2 : -5}
                  sx={{
                    color: theme.palette.mode === "dark" ? "white" : "black",
                  }}
                >
                  Didn’t receive yet?{" "}
                  <Link
                    component="button"
                    onClick={onResendCode}
                    color="primary"
                    underline="hover"
                    sx={{
                      color: isResendDisabled ? "gray" : "primary.light",
                      mt: -0.3,
                    }}
                    disabled={isResendDisabled}
                  >
                    Resend code
                  </Link>
                  {isResendDisabled && (
                    <Typography component="span" color="error" ml={1}>
                      ({resendTimer}s)
                    </Typography>
                  )}
                </Typography>

                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  fullWidth
                  sx={{
                    marginTop: -4,
                    paddingY: 1.8,
                    "&:hover": {
                      backgroundColor: "primary.dark",
                    },
                  }}
                  disabled={isLoading || code.some((c) => c === "")}
                >
                  {isLoading ? (
                    <CircularProgress size={26} sx={{ color: "white" }} />
                  ) : (
                    <Typography fontWeight={500}>Verify</Typography>
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
          Unable to verify your code. Please try again.
        </Alert>
      </Snackbar>
    </Grid>
  );
};
