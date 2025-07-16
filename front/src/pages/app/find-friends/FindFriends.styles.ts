import { Theme } from "@mui/material";

export const getLeftPanel = (theme: Theme) => ({
  borderRight: `1px solid ${theme.palette.divider}`,
});

export const getDiversityIcon = (theme: Theme,) => ({
  width: 80,
  height: 80,
  color:
    theme.palette.mode === "dark"
      ? theme.palette.primary.main
      : theme.palette.primary.dark,
});

export const getDiversityText = (theme: Theme) => ({
  color: theme.palette.mode === "dark" ? "grey.300" : "text.secondary",
  textAlign: "center",
});


export const getSearchInput = (
  theme: Theme,
  mdDown: boolean,
  smDown: boolean
) => ({
  width: mdDown ? "100%" : "80%",
  boxShadow: theme.palette.mode === "light" ? 1 : 0,
  borderRadius: "10px",
  mx: smDown ? 1 : 3,
  "& .MuiOutlinedInput-root": {
    borderRadius: "10px",
    height: "60px",
    backgroundColor:
      theme.palette.mode === "light"
        ? "white"
        : theme.palette.background.default,
    "& fieldset": { borderColor: "transparent" },
    "&:hover fieldset": {
      borderColor: theme.palette.action.hover,
    },
    "&.Mui-focused fieldset": {
      borderColor: "primary.main",
      borderWidth: "1px",
    },
  },
  "& .MuiInputBase-input": {
    paddingTop: "12px",
    paddingBottom: "12px",
  },
});

