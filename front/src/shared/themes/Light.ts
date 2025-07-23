import { createTheme } from "@mui/material";
import { grey } from "@mui/material/colors";

export const LightTheme = createTheme({
  palette: {
    mode: "light",
    primary: {
      main: "#1A237E", // Azul escuro
      dark: "#0D133E",
      light: "#3F51B5",
      contrastText: "#ffffff",
    },
    secondary: {
      main: "#009688", // Verde-azulado
      dark: "#00695C",
      light: "#4DB6AC",
      contrastText: "#ffffff",
    },
    background: {
      default: "#F0F1F3", // fundo da tela — cinza claro
      paper: "#FFFFFF", // elementos flutuantes — branco puro
    },
    text: {
      primary: grey[900],
      secondary: grey[700],
    },
    error: {
      main: "#FF7043", // Laranja queimado
    },
  },
  breakpoints: {
    values: {
      xs: 0,
      sm: 600,
      md: 900,
      lg: 1200,
      xl: 1536,
      xxl: 2000, // agora aceito
    },
  },
});
