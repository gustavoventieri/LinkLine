import { BrowserRouter } from "react-router-dom";
import { AppThemeProvider } from "./shared/contexts/ThemeContext";
import { AppRoutes } from "./routes";
import { EmailProvider } from "./shared/contexts/EmailContext";
import { UsernameProvider } from "./shared/contexts/UsernameContext";

export const App = () => {
  return (
    <UsernameProvider>
      <EmailProvider>
        <AppThemeProvider>
          <BrowserRouter>
            <AppRoutes />
          </BrowserRouter>
        </AppThemeProvider>
      </EmailProvider>
    </UsernameProvider>
  );
};
