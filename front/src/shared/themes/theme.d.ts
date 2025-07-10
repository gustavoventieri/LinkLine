// theme.d.ts (ou no topo de onde está o `createTheme`)

import "@mui/material/styles";

declare module "@mui/material/styles" {
  interface BreakpointOverrides {
    xs: true;
    sm: true;
    md: true;
    lg: true;
    xl: true;
    xxl: true; // 👈 agora TypeScript aceita
  }
}
