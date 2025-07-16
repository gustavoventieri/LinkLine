import { Theme } from "@mui/material";

export const getLogoStyle = (xxlUp: boolean, theme: Theme) => ({
  fontFamily: '"Irish Grover", cursive',
  fontSize: xxlUp ? 150 : 110,
  color: theme.palette.mode === "light" ? theme.palette.primary.main : "white",
});

export const getContainerStyle = (mdDown: boolean, theme: Theme) => ({
  borderRadius: mdDown ? 0 : 3,
  overflow: "hidden",
  boxShadow: mdDown ? 0 : 10,
  backgroundColor: mdDown ? "transparent" : theme.palette.background.paper,
  padding: 4,
  gap: 5,
});

export const getTitleStyle = (theme: Theme) => {
  return theme.palette.mode === "light" ? theme.palette.primary.main : "white";
};

export const getInputStyle = (theme: Theme) => ({
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
    color: theme.palette.text.primary,
  },
});

export const getIconStyle = (theme: Theme) => ({
  width: 26,
  height: 26,
  color: theme.palette.primary.main,
});
