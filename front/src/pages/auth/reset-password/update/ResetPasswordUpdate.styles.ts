// src/pages/.../ResetPasswordUpdate.styles.ts

import { Theme } from "@mui/material";

export const getIconStyle = (theme: Theme) => ({
  color: theme.palette.primary.main,
  width: 26,
  height: 26,
});

export const getContainerStyle = (mdDown: boolean, theme: Theme) => ({
  borderRadius: mdDown ? 0 : 5,
  overflowY: "auto",
  boxShadow: mdDown ? 0 : 10,
  px: 2,
  backgroundColor: mdDown ? "none" : theme.palette.background.paper,
});

export const getInputStyle = (theme: Theme) => ({
  "& .MuiOutlinedInput-root": {
    borderRadius: 3,
    height: 65,
    "& fieldset": {
      borderColor: theme.palette.primary.main,
    },
  },
  "& .MuiInputLabel-root.Mui-focused": {
    color: theme.palette.primary.main,
  },
  "& .MuiInputLabel-shrink": {
    color: theme.palette.text.primary,
  },
});

export const getSubmitButtonStyle = (theme: Theme, mdDown: boolean) => ({
  marginTop: mdDown ? -1 : -4,
  borderRadius: 3,
  paddingY: 1.8,
  "&:hover": {
    backgroundColor: theme.palette.primary.dark,
  },
});
