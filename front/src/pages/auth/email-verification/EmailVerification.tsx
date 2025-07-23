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
import { useEmail } from "../../../shared/contexts/EmailContext";

import {
  getContainerStyle,
  getInputStyle,
  getButtonStyle,
  getTitleStyle,
  getSubtitleStyle,
  getResendLinkStyle,
  getSnackbarStyle,
} from "./EmailVerification.styles";

const schema = yup.object({
  verificationCode: yup
    .string()
    .required("Code is required")
    .length(6, "Code must be 6 digits"),
});

export const EmailVerification = () => {
  const theme = useTheme();

  const navigate = useNavigate();
  const { email } = useEmail();

  const smDown = useMediaQuery((t: Theme) => t.breakpoints.down("sm"));
  const mdDown = useMediaQuery((t: Theme) => t.breakpoints.down("md"));

  const inputsRef = useRef<Array<HTMLInputElement | null>>([]);
  const [code, setCode] = useState(["", "", "", "", "", ""]);
  const [isResendDisabled, setIsResendDisabled] = useState(false);
  const [resendTimer, setResendTimer] = useState(30);
  const [isLoading, setIsLoading] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);

  const containerStyle = getContainerStyle(smDown, mdDown, theme);
  const inputStyle = getInputStyle(theme);
  const buttonStyle = getButtonStyle(theme);
  const titleStyle = getTitleStyle(theme, mdDown);
  const subtitleStyle = getSubtitleStyle(theme, mdDown);
  const resendLinkStyle = getResendLinkStyle(isResendDisabled, theme);
  const snackbarStyle = getSnackbarStyle;

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
    if (!token) return console.warn("No session token");
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
    if (!token) return console.warn("No session token");
    setIsResendDisabled(true);
    try {
      await api.post("/auth/email-confirmation/resend", { email });
    } catch {
      /*ignore*/
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
        <Box
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          width="80%"
          height="50vh"
          sx={containerStyle}
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
                width={smDown ? "100%" : "80%"}
                display="flex"
                flexDirection="column"
                gap={8}
              >
                <Typography sx={titleStyle}>Verifying Your Email</Typography>
                <Typography sx={subtitleStyle}>
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
                      sx={inputStyle}
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
                  fontSize={16}
                  fontWeight={500}
                  align="left"
                  ml={0.5}
                  mt={-5}
                  sx={{ color: "gray" }}
                >
                  Didn’t receive yet?{" "}
                  <Link
                    component="button"
                    onClick={onResend}
                    underline="hover"
                    disabled={isResendDisabled}
                    sx={resendLinkStyle}
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
                  sx={buttonStyle}
                  disabled={isLoading || code.some((x) => !x)}
                >
                  {isLoading ? (
                    <CircularProgress size={26} color="inherit" />
                  ) : (
                    "Verify"
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
          sx={snackbarStyle}
        >
          Expired or Invalid Code. Please try again.
        </Alert>
      </Snackbar>
    </Grid>
  );
};
