import { Theme } from "@mui/material";

export const getInputStyle = (theme: Theme, themeName: string) => ({
  "& .MuiOutlinedInput-root": {
    borderRadius: 3,
    height: 55,
    "& fieldset": {
      borderColor: theme.palette.primary.main,
    },
  },
  "& .MuiInputLabel-root.Mui-focused": {
    color: theme.palette.primary.main,
  },
  "& .MuiInputLabel-shrink": {
    color: themeName === "light" ? theme.palette.text.primary : "white",
  },
});

export const getIconStyle = (theme: Theme) => ({
  width: 26,
  height: 26,
  color: theme.palette.primary.main,
});
