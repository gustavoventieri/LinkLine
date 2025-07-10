import {
  Box,
  Button,
  Grid,
  Link,
  Paper,
  Snackbar,
  TextField,
  Theme,
  Typography,
  useMediaQuery,
  Alert,
  CircularProgress,
} from "@mui/material";
import { useEffect, useRef, useState } from "react";
import { useAppThemeContext } from "../../../shared/contexts";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { useNavigate } from "react-router-dom";
import { api } from "../../../shared/services";
import { useEmail } from "../../../shared/contexts/EmailContext";

// Schema de validação
const schema = yup.object({
  verificationCode: yup
    .string()
    .required("Code is required")
    .length(6, "Code must be 6 digits"),
});

export const EmailVerification = () => {
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));

  const navigate = useNavigate();
  const { email } = useEmail();
  const { themeName } = useAppThemeContext();
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
  } = useForm({
    resolver: yupResolver(schema),
    mode: "onChange", // validar enquanto digita, opcional
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

    // Sincroniza valor do código com react-hook-form
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

      // Atualiza form com novo código
      setValue("verificationCode", newCode.join(""));

      inputsRef.current[index - 1]?.focus();
    }
  };

  const onSubmit = async (data: { verificationCode: string }) => {
    const fullCode = data.verificationCode;

    const sessionToken = localStorage.getItem("authSession");
    if (!sessionToken) {
      console.warn("Nenhum token de sessão encontrado.");
      return;
    }

    setIsLoading(true);
    try {
      const verifyEmailPayload = { email, code: fullCode };

      const verifyEmailResponse = await api.post(
        "/auth/email-confirmation/verify",
        verifyEmailPayload
      );

      if (verifyEmailResponse.status === 200) {
        const registerUserResponse = await api.post("/auth/register", {
          email,
          code: fullCode,
          avatarUrl: "asdfghjkgfds",
        });

        if (registerUserResponse.status === 200) {
          localStorage.removeItem("authSession");
          navigate("/chats", { replace: true });
        }
      }
    } catch (error) {
      console.error("Erro ao verificar o código:", error);
      setSnackbarOpen(true);
    } finally {
      setIsLoading(false);
    }
  };

  const onResendCode = async () => {
    const sessionToken = localStorage.getItem("authSession");
    if (!sessionToken) {
      console.warn("Nenhum token de sessão encontrado.");
      return;
    }

    setIsResendDisabled(true);
    try {
      await api.post("/auth/email-confirmation/resend", { email });
    } catch (error) {
      console.error("Erro ao reenviar o código:", error);
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
        size={{ xs: 12, sm: 8, md: 8, lg: 5 }}
        display="flex"
        justifyContent="center"
        flexDirection="column"
        alignItems="center"
        borderRadius={1}
      >
        <Paper
          component={Box}
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          boxShadow={3}
          width="80%"
          height={"50vh"}
          sx={{
            borderRadius: smDown ? 0 : 3,
            overflow: "hidden",
            boxShadow: smDown ? 0 : 10,
            backgroundColor: smDown ? "transparent" : "primary.paper",
          }}
        >
          <form onSubmit={handleSubmit(onSubmit)} style={{ width: "100%" }}>
            {/* input escondido para react-hook-form */}
            <input type="hidden" {...register("verificationCode")} />

            <Box
              width="100%"
              display="flex"
              flexDirection="column"
              alignItems="center"
              justifyContent="center"
            >
              <Box
                width={smDown ? "100%" : "80%"}
                display="flex"
                flexDirection="column"
                gap={8}
              >
                <Typography
                  fontSize={mdDown ? 24 : 28}
                  fontWeight={900}
                  mb={-5}
                  align="center"
                  color={themeName === "light" ? "primary.main" : "white"}
                >
                  Verifying Your Email
                </Typography>

                <Typography
                  fontSize={mdDown ? 12 : 16}
                  mb={-5}
                  fontWeight={400}
                  align="center"
                  color={themeName === "light" ? "black" : "white"}
                >
                  Check your email — we’ve sent you a code to verify your email!
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
                  fontSize={11}
                  fontWeight={500}
                  align="left"
                  ml={0.5}
                  mt={-5}
                >
                  Didn’t receive yet?{" "}
                  <Link
                    component="button"
                    fontWeight={500}
                    onClick={onResendCode}
                    type="button"
                    underline="hover"
                    sx={{ color: isResendDisabled ? "gray" : "primary.light" }}
                    disabled={isResendDisabled}
                  >
                    Resend code
                  </Link>
                  {isResendDisabled && (
                    <Typography
                      component="span"
                      fontSize={14}
                      fontWeight={400}
                      color="error"
                      ml={1}
                    >
                      ({resendTimer}s)
                    </Typography>
                  )}
                </Typography>

                <Button
                  fullWidth
                  type="submit"
                  variant="contained"
                  color="primary"
                  sx={{
                    marginTop: -4,
                    borderRadius: 3,
                    paddingY: 1.4,
                    "&:hover": {
                      backgroundColor: "primary.dark",
                    },
                  }}
                  disabled={isLoading || code.some((c) => c === "")}
                >
                  {isLoading ? (
                    <CircularProgress size={26} sx={{ color: "white" }} />
                  ) : (
                    <Typography fontSize={12} fontWeight={900}>
                      Verify
                    </Typography>
                  )}
                </Button>
              </Box>
            </Box>
          </form>
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
          Expired or Invalid Code. Please try again.
        </Alert>
      </Snackbar>
    </Grid>
  );
};
