// src/pages/Chats/styles.ts
import { Theme } from "@mui/material";

export const getSearchBarStyle = (theme: Theme) => ({
  p: "12px 4px",
  display: "flex",
  boxShadow: 3,
  alignItems: "center",
  mb: theme.breakpoints.down("md") ? 2 : 5,
  borderRadius: 2,
  backgroundColor: theme.palette.background.default,
});
