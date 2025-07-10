import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
} from "react";
import { ThemeProvider } from "@mui/material";
import { Box } from "@mui/system";

import { DarkTheme, LightTheme } from "../themes";

interface IThemeContextData {
  themeName: "light" | "dark";
  toggleTheme: () => void;
}

const ThemeContext = createContext({} as IThemeContextData);

export const useAppThemeContext = () => {
  return useContext(ThemeContext);
};

interface IAppThemeProviderProps {
  children: React.ReactNode;
}
export const AppThemeProvider: React.FC<IAppThemeProviderProps> = ({
  children,
}) => {
  const [themeName, setThemeName] = useState<"light" | "dark">(() => {
    const storedTheme = localStorage.getItem("app-theme");
    return storedTheme === "dark" ? "dark" : "light";
  });

  const toggleTheme = useCallback(() => {
    setThemeName((oldThemeName) => {
      const newTheme = oldThemeName === "light" ? "dark" : "light";
      localStorage.setItem("app-theme", newTheme);
      return newTheme;
    });
  }, []);

  const theme = useMemo(() => {
    return themeName === "light" ? LightTheme : DarkTheme;
  }, [themeName]);

  return (
    <ThemeContext.Provider value={{ themeName, toggleTheme }}>
      <ThemeProvider theme={theme}>
        <Box
          width="100vw"
          height="100vh"
          bgcolor={theme.palette.background.default}
        >
          {children}
        </Box>
      </ThemeProvider>
    </ThemeContext.Provider>
  );
};
