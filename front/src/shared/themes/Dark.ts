import { createTheme } from "@mui/material";
import { grey } from "@mui/material/colors";

export const DarkTheme = createTheme({
  palette: {
    mode: "dark",
    primary: {
      main: "#0A7BFF",
      light: "#5AAEFF", // Versão clara
      dark: "#005FCC", // Versão escura
      contrastText: "#ffffff",
    },
    secondary: {
      main: "#009688", // Verde-azulado (moderno)
      dark: "#00695C",
      light: "#4DB6AC",
      contrastText: "#ffffff",
    },
    background: {
      paper: "#121416", // quase preto
      default: "#1A1D21", // grafite profundo
    },
    text: {
      primary: grey[100],
      secondary: grey[400],
    },
    error: {
      main: "#FF7043", // Laranja queimado como alerta
    },
  },
});
