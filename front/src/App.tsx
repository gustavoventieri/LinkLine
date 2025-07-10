import { BrowserRouter } from "react-router-dom";
import { AppThemeProvider } from "./shared/contexts/ThemeContext";
import { AppRoutes } from "./routes";
import { EmailProvider } from "./shared/contexts/EmailContext";

export const App = () => {
  return (
    <EmailProvider>
      <AppThemeProvider>
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      </AppThemeProvider>
    </EmailProvider>
  );
};
