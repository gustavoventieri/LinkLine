import {
  Box,
  Button,
  Grid,
  Link,
  Snackbar,
  TextField,
  Typography,
  useMediaQuery,
  Alert,
  CircularProgress,
  useTheme,
  Theme,
} from "@mui/material";
import { useEffect, useRef, useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { useNavigate } from "react-router-dom";
import { api } from "../../../shared/services";

const schema = yup.object({
  verificationCode: yup
    .string()
    .required("Code is required")
    .length(6, "Code must be 6 digits"),
});

export const EmailVerification = () => {
  const theme = useTheme();

  const navigate = useNavigate();

  const smDown = useMediaQuery((t: Theme) => t.breakpoints.down("sm"));
  const mdDown = useMediaQuery((t: Theme) => t.breakpoints.down("md"));

  const inputsRef = useRef<Array<HTMLInputElement | null>>([]);
  const [code, setCode] = useState(["", "", "", "", "", ""]);
  const [isResendDisabled, setIsResendDisabled] = useState(false);
  const [resendTimer, setResendTimer] = useState(30);
  const [isLoading, setIsLoading] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);

  const {
    register,
    handleSubmit,
    clearErrors,
    setValue,
    formState: { errors },
  } = useForm<{ verificationCode: string }>({
    resolver: yupResolver(schema),
    mode: "onChange",
  });

  useEffect(() => {
    api
      .get("/auth/isAuth")
      .then(() => navigate("/chats", { replace: true }))
      .catch(() => {});
  }, [navigate]);

  useEffect(() => {
    if (!isResendDisabled) return;
    if (resendTimer === 0) {
      setIsResendDisabled(false);
      setResendTimer(30);
      return;
    }
    const id = setTimeout(() => setResendTimer((r) => r - 1), 1000);
    return () => clearTimeout(id);
  }, [isResendDisabled, resendTimer]);

  const handleInputChange = (i: number, v: string) => {
    if (!/^\d?$/.test(v)) return;
    const newCode = [...code];
    newCode[i] = v;
    setCode(newCode);
    setValue("verificationCode", newCode.join(""));
    clearErrors("verificationCode");
    if (v && i < 5) inputsRef.current[i + 1]?.focus();
  };
  const handleKeyDown = (e: React.KeyboardEvent, i: number) => {
    if (e.key === "Backspace" && !code[i] && i > 0) {
      const newCode = [...code];
      newCode[i - 1] = "";
      setCode(newCode);
      setValue("verificationCode", newCode.join(""));
      inputsRef.current[i - 1]?.focus();
    }
  };

  const onSubmit = async (data: { verificationCode: string }) => {
    const token = localStorage.getItem("authSession");
    const email = localStorage.getItem("email");
    if (!token || !email) return console.warn("No session token or user email");
    setIsLoading(true);
    try {
      const { status } = await api.post("/auth/email-confirmation/verify", {
        email,
        code: data.verificationCode,
      });
      if (status === 200) {
        const reg = await api.post("/auth/register", {
          email,
          code: data.verificationCode,
          avatarUrl: "asdfghjkljhgfd",
        });
        if (reg.status === 200) {
          localStorage.removeItem("authSession");
          localStorage.removeItem("email");

          navigate("/chats", { replace: true });
        }
      }
    } catch {
      setSnackbarOpen(true);
    } finally {
      setIsLoading(false);
    }
  };

  const onResend = async () => {
    const token = localStorage.getItem("authSession");
    const email = localStorage.getItem("email");
    if (!token || !email) return console.warn("No session token or user email");
    setIsResendDisabled(true);
    try {
      await api.post("/auth/email-confirmation/resend", { email });
    } catch {
      /*ignore*/
    }
  };

  return (
    <Box
      display="flex"
      width="100%"
      height="100vh"
      justifyContent="center"
      alignItems="center"
    >
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        height="50vh"
        sx={{
          borderRadius: smDown ? 0 : 3,
          overflow: "hidden",
          boxShadow: mdDown ? 0 : 4,
          maxWidth: 500,
          backgroundColor: mdDown ? "none" : theme.palette.background.paper,
          px: 5,
        }}
      >
        <form
          onSubmit={handleSubmit(onSubmit)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
            }
          }}
          style={{ width: "100%" }}
        >
          <input type="hidden" {...register("verificationCode")} />

          <Box
            width="100%"
            display="flex"
            flexDirection="column"
            alignItems="center"
            justifyContent="center"
          >
            <Box display="flex" flexDirection="column" gap={8} maxWidth={500}>
              <Typography
                sx={{
                  fontSize: mdDown ? 24 : 28,
                  fontWeight: 900,
                  mb: -5,
                  textAlign: "center" as const,
                  color:
                    theme.palette.mode === "light"
                      ? theme.palette.primary.main
                      : theme.palette.common.white,
                }}
              >
                Verifying Your Email
              </Typography>
              <Typography
                sx={{
                  fontSize: mdDown ? 12 : 16,
                  mb: -5,
                  fontWeight: 400,
                  textAlign: "center" as const,
                  color:
                    theme.palette.mode === "light"
                      ? theme.palette.text.primary
                      : theme.palette.common.white,
                }}
              >
                Check your email — we’ve sent you a code to verify your email!
              </Typography>

              <Box display="flex" justifyContent="center" gap={1}>
                {code.map((c, i) => (
                  <TextField
                    key={i}
                    inputRef={(el) => (inputsRef.current[i] = el)}
                    inputProps={{
                      maxLength: 1,
                      inputMode: "numeric",
                      pattern: "[0-9]*",
                    }}
                    sx={{
                      width: { xs: "2.5rem", sm: "3rem", md: "3rem" },
                      "& input": {
                        fontSize: { xs: "2rem", sm: "2.5rem", md: "2.5rem" },
                        padding: 0,
                        textAlign: "center" as const,
                        color: theme.palette.text.primary,
                      },
                    }}
                    value={c}
                    onChange={(e) => handleInputChange(i, e.target.value)}
                    onKeyDown={(e) => handleKeyDown(e, i)}
                    error={!!errors.verificationCode}
                    helperText={errors.verificationCode?.message}
                    size="small" // opcional para deixar menor
                    variant="outlined"
                  />
                ))}
              </Box>
              <Typography
                fontSize={14}
                align="left"
                ml={0.5}
                mt={-5}
                sx={{
                  color: theme.palette.mode === "light" ? "black" : "white",
                }}
              >
                Didn’t receive yet?{" "}
                <Link
                  component="button"
                  onClick={onResend}
                  underline="hover"
                  disabled={isResendDisabled}
                  sx={{
                    color: isResendDisabled
                      ? "gray"
                      : theme.palette.primary.light,
                    fontSize: 14,
                    mt: -0.3,
                  }}
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
                fullWidth
                type="submit"
                variant="contained"
                sx={{
                  marginTop: -4,

                  paddingY: 1.4,
                  "&:hover": {
                    backgroundColor: theme.palette.primary.dark,
                  },
                }}
                disabled={isLoading || code.some((x) => !x)}
              >
                {isLoading ? (
                  <CircularProgress size={26} sx={{ color: "white" }} />
                ) : (
                  "Verify"
                )}
              </Button>
            </Box>
          </Box>
        </form>
      </Box>

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
    </Box>
  );
};
