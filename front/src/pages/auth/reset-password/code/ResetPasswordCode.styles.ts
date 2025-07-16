import { Theme } from "@mui/material";

export const getContainerStyle = (
  smDown: boolean,
  mdDown: boolean,
  theme: Theme
) => ({
  borderRadius: smDown ? 0 : 3,
  overflow: "hidden",
  boxShadow: smDown ? 0 : 10,
  backgroundColor: mdDown ? "none" : theme.palette.background.paper,
});

export const getInputStyle = () => ({
  width: {
    xs: "2.5rem",
    sm: "3.0rem",
    md: "3.0rem",
  },
  "& input": {
    fontSize: {
      xs: "2rem",
      sm: "2.5rem",
      md: "2.5rem",
    },
    padding: 0,
  },
});
