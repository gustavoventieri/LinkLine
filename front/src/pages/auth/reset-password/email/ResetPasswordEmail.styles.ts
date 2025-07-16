import { Theme } from "@mui/material";

export const getBoxFormStyle = (mdDown: boolean, theme: Theme) => ({
  borderRadius: mdDown ? 0 : 5,
  overflowY: "auto",
  boxShadow: mdDown ? 0 : 10,
  px: 2,
  backgroundColor: mdDown ? "none" : theme.palette.background.paper,
});

export const getTextFieldStyle = (theme: Theme) => ({
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

export const submitButtonStyle = (theme: Theme) => ({
  borderRadius: 3,
  paddingY: 1.8,
  "&:hover": {
    backgroundColor: theme.palette.primary.dark,
  },
});
