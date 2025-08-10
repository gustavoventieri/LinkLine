import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Grid,
  IconButton,
  InputAdornment,
  Link,
  Paper,
  Snackbar,
  TextField,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import {
  LockPersonOutlined,
  MailOutline,
  PersonOutline,
  Visibility,
  VisibilityOff,
} from "@mui/icons-material";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { useNavigate } from "react-router-dom";
import { api } from "../../../shared/services";

// Esquema de validação com Yup
const schema = yup.object().shape({
  username: yup.string().required("O nome de usuário é obrigatório"),
  email: yup.string().email("Email inválido").required("O email é obrigatório"),
  password: yup
    .string()
    .required("A senha é obrigatória")
    .min(6, "A senha deve ter pelo menos 6 caracteres"),
  confirmPassword: yup
    .string()
    .required("Confirme sua senha")
    .oneOf([yup.ref("password")], "As senhas devem ser iguais"),
});

type FormData = yup.InferType<typeof schema>;

export const Register = () => {
  const theme = useTheme();
  const navigate = useNavigate();
  const isMdDown = useMediaQuery(theme.breakpoints.down("md"));

  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  // Estado para o Snackbar
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({ resolver: yupResolver(schema) });

  // Verifica se o usuário já está autenticado
  useEffect(() => {
    const checkAuth = async () => {
      try {
        await api.get("/auth/isAuth");
        navigate("/chats", { replace: true });
      } catch {
        // O usuário não está autenticado, pode permanecer na página de registro
      }
    };
    checkAuth();
  }, [navigate]);

  const onSubmit = async (data: FormData) => {
    setIsLoading(true);
    try {
      const response = await api.post("/auth/email-confirmation/send", {
        username: data.username.toLowerCase(),
        email: data.email,
        password: data.password,
      });

      if (response.status === 200) {
        localStorage.setItem("email", data.email);
        localStorage.setItem("authSession", "true");
        navigate("/email-verification");
      }
    } catch (error: any) {
      setErrorMessage(
        error.response?.data?.message || "Ocorreu um erro desconhecido."
      );
      setSnackbarOpen(true);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbarOpen(false);
  };

  return (
    <Box
      sx={{
        height: "100vh",
        backgroundColor: theme.palette.background.default,
      }}
    >
      <Grid container sx={{ height: "100%" }}>
        {/* Coluna Esquerda - Branding */}
        {!isMdDown && (
          <Grid
            size={{ md: 4 }}
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              bgcolor: theme.palette.primary.main,
              color: "#fff",
              p: 4,
            }}
          >
            <Box textAlign="center">
              <Typography
                sx={{
                  fontFamily: '"Irish Grover", cursive',
                  fontSize: 80,
                  color: "white",
                  userSelect: "none",
                }}
              >
                Link Line
              </Typography>
              <Typography variant="subtitle1" mt={1}>
                Crie sua conta e conecte-se com seus amigos.
              </Typography>
            </Box>
          </Grid>
        )}

        {/* Coluna Direita - Formulário de Registro */}
        <Grid
          size={{ xs: 12, md: 8 }}
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            p: 4,
          }}
        >
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              height: "65vh",
              maxWidth: 500,
              p: isMdDown ? 1 : 4,
              justifyContent: "center",
              borderRadius: 3,
              bgcolor: isMdDown
                ? "transparent"
                : theme.palette.background.paper,
              boxShadow: 3,
            }}
          >
            <Typography
              variant="h5"
              align="center"
              fontWeight="bold"
              gutterBottom
              color={
                theme.palette.mode === "light"
                  ? theme.palette.primary.main
                  : "white"
              }
            >
              Criar Conta
            </Typography>

            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <TextField
                fullWidth
                label="Nome de usuário"
                {...register("username")}
                margin="normal"
                error={!!errors.username}
                helperText={errors.username?.message}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <PersonOutline
                        sx={{ color: theme.palette.primary.main }}
                      />
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                fullWidth
                label="Email"
                type="email"
                {...register("email")}
                margin="normal"
                error={!!errors.email}
                helperText={errors.email?.message}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <MailOutline sx={{ color: theme.palette.primary.main }} />
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                fullWidth
                label="Senha"
                type={showPassword ? "text" : "password"}
                {...register("password")}
                margin="normal"
                error={!!errors.password}
                helperText={errors.password?.message}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockPersonOutlined
                        sx={{ color: theme.palette.primary.main }}
                      />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowPassword((prev) => !prev)}
                        edge="end"
                      >
                        {showPassword ? (
                          <VisibilityOff
                            sx={{ color: theme.palette.primary.main }}
                          />
                        ) : (
                          <Visibility
                            sx={{ color: theme.palette.primary.main }}
                          />
                        )}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                fullWidth
                label="Confirmar Senha"
                type={showConfirmPassword ? "text" : "password"}
                {...register("confirmPassword")}
                margin="normal"
                error={!!errors.confirmPassword}
                helperText={errors.confirmPassword?.message}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockPersonOutlined
                        sx={{ color: theme.palette.primary.main }}
                      />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowConfirmPassword((prev) => !prev)}
                        edge="end"
                      >
                        {showConfirmPassword ? (
                          <VisibilityOff
                            sx={{ color: theme.palette.primary.main }}
                          />
                        ) : (
                          <Visibility
                            sx={{ color: theme.palette.primary.main }}
                          />
                        )}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
              <Box my={1} textAlign="left">
                <Typography
                  color={theme.palette.mode === "light" ? "black" : "white"}
                  fontSize={14}
                >
                  Já tem uma conta?{" "}
                  <Link
                    href="/"
                    underline="hover"
                    color={theme.palette.primary.light}
                  >
                    Entrar
                  </Link>
                </Typography>
              </Box>
              <Button
                type="submit"
                variant="contained"
                fullWidth
                disabled={isLoading}
                sx={{ mt: 2, py: 1.5 }}
              >
                {isLoading ? (
                  <CircularProgress size={24} color="inherit" />
                ) : (
                  "Registrar"
                )}
              </Button>
            </form>
          </Box>
        </Grid>
      </Grid>

      {/* Snackbar para erros */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity="error"
          variant="filled"
          sx={{ width: "100%" }}
        >
          {errorMessage}
        </Alert>
      </Snackbar>
    </Box>
  );
};
