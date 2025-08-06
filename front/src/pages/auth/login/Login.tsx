import {
  LockPersonOutlined,
  MailOutline,
  Visibility,
  VisibilityOff,
} from "@mui/icons-material";
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
import { yupResolver } from "@hookform/resolvers/yup";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import * as yup from "yup";
import { api } from "../../../shared/services";

// Esquema de validação para o formulário
const schema = yup
  .object({
    email: yup.string().email("Email inválido").required("Email é obrigatório"),
    password: yup
      .string()
      .min(6, "A senha deve ter pelo menos 6 caracteres")
      .required("Senha é obrigatória"),
  })
  .required();

type FormData = yup.InferType<typeof schema>;

export const Login = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: yupResolver(schema),
  });

  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const navigate = useNavigate();
  const theme = useTheme();
  const isMdDown = useMediaQuery(theme.breakpoints.down("md"));

  // Efeito para verificar se o usuário já está autenticado
  useEffect(() => {
    const checkAuth = async () => {
      try {
        await api.get("/auth/isAuth");
        navigate("/chats", { replace: true });
      } catch (error) {
        console.warn("Usuário não autenticado", error);
      }
    };
    checkAuth();
  }, [navigate]);

  const onSubmit = async (data: FormData) => {
    setIsLoading(true);
    try {
      const response = await api.post("/auth/login", data);
      if (response.status === 200) {
        navigate("/chats");
      }
    } catch (error: any) {
      console.error("Erro ao fazer login:", error);
      const message =
        error?.response?.data?.message ||
        "Erro ao fazer login. Tente novamente.";
      setErrorMessage(message);
      setOpenSnackbar(true);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCloseSnackbar = (
    event?: React.SyntheticEvent | Event,
    reason?: string
  ) => {
    if (reason === "clickaway") {
      return;
    }
    setOpenSnackbar(false);
  };

  return (
    <Box
      sx={{
        height: "100vh",
        backgroundColor: theme.palette.background.default,
      }}
    >
      <Grid container sx={{ height: "100%" }}>
        {/* Coluna Esquerda: Branding */}

        {/* Coluna Direita: Formulário */}
        <Grid
          size={{ xs: 12, md: 8 }}
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            p: 4,
          }}
        >
          <Paper
            elevation={isMdDown ? 0 : 3}
            sx={{
              display: "flex",
              flexDirection: "column",
              width: "100%",
              maxWidth: 500,
              p: isMdDown ? 1 : 4,
              justifyContent: "center",
              borderRadius: 3,
              gap: 2,
              bgcolor: isMdDown ? "transparent" : undefined,
              boxShadow: isMdDown ? "none" : undefined,
            }}
          >
            <Typography
              variant="h5"
              align="center"
              gutterBottom
              fontWeight="bold"
              color={
                theme.palette.mode === "light"
                  ? theme.palette.primary.main
                  : "white"
              }
            >
              Acesse sua Conta
            </Typography>

            <form onSubmit={handleSubmit(onSubmit)} noValidate>
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

              <Box textAlign="right" sx={{ mt: 1 }}>
                <Link
                  href="/reset-password"
                  underline="hover"
                  variant="body2"
                  color={theme.palette.primary.light}
                >
                  Esqueceu sua senha?
                </Link>
              </Box>

              <Button
                type="submit"
                variant="contained"
                fullWidth
                sx={{ mt: 2, py: 1.5, fontSize: 16 }}
                disabled={isLoading}
              >
                {isLoading ? (
                  <CircularProgress size={24} color="inherit" />
                ) : (
                  "Entrar"
                )}
              </Button>
            </form>

            <Box mt={2} textAlign="center">
              <Typography>
                Não tem uma conta?{" "}
                <Link
                  href="/register"
                  underline="hover"
                  color={theme.palette.primary.light}
                >
                  Cadastre-se
                </Link>
              </Typography>
            </Box>
          </Paper>
        </Grid>

        {!isMdDown && (
          <Grid
            size={{ md: 4 }}
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              bgcolor: "primary.main",
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
      </Grid>

      {/* Snackbar para exibir mensagens de erro */}
      <Snackbar
        open={openSnackbar}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity="error"
          sx={{ width: "100%" }}
          variant="filled"
        >
          {errorMessage}
        </Alert>
      </Snackbar>
    </Box>
  );
};
