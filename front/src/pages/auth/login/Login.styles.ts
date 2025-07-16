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

export const getLinkStyle = (theme: Theme) => ({
  color: theme.palette.primary.light,
});

export const getTitleStyle = (
  theme: Theme,
  themeName: string,
  mdDown: boolean
) => ({
  fontSize: mdDown ? 28 : 32,
  marginBottom: 4,
  fontWeight: 900,
  textAlign: "center",
  color: themeName === "light" ? theme.palette.primary.main : "white",
});

export const getFormStyle = (smDown: boolean) => ({
  width: "100%",
  display: "flex",
  flexDirection: "column",
  gap: 3,
  paddingX: smDown ? 2 : 5,
});

export const buttonStyle = {
  marginTop: 0,
  borderRadius: 2,
  paddingY: 1.4,
};
