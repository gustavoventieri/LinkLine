import { BrowserRouter } from "react-router-dom";
import { AppThemeProvider } from "./shared/contexts/ThemeContext";
import { AppRoutes } from "./routes";
import { UsernameProvider } from "./shared/contexts/UsernameContext";

export const App = () => {
  return (
    <UsernameProvider>
      <AppThemeProvider>
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      </AppThemeProvider>
    </UsernameProvider>
  );
};
