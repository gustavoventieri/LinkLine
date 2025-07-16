import { Theme } from "@mui/material";

export const getContainerStyle = (smDown: boolean, mdDown: boolean) => ({
  borderRadius: smDown ? 0 : 3,
  overflow: "hidden",
  boxShadow: smDown ? 0 : 10,
  backgroundColor: mdDown ? "none" : "#1E2125",
});

export const getInputStyle = (theme: Theme) => ({
  width: { xs: "2.5rem", sm: "3rem", md: "3rem" },
  "& input": {
    fontSize: { xs: "2rem", sm: "2.5rem", md: "2.5rem" },
    padding: 0,
    textAlign: "center" as const,
    color: theme.palette.text.primary, // exemplo, se quiser cor do texto
  },
});

export const getButtonStyle = (theme: Theme) => ({
  marginTop: -4,
  borderRadius: 3,
  paddingY: 1.4,
  "&:hover": {
    backgroundColor: theme.palette.primary.dark,
  },
});

export const getTitleStyle = (theme: Theme, mdDown: boolean) => ({
  fontSize: mdDown ? 24 : 28,
  fontWeight: 900,
  mb: -5,
  textAlign: "center" as const,
  color:
    theme.palette.mode === "light"
      ? theme.palette.primary.main
      : theme.palette.common.white,
});

export const getSubtitleStyle = (theme: Theme, mdDown: boolean) => ({
  fontSize: mdDown ? 12 : 16,
  mb: -5,
  fontWeight: 400,
  textAlign: "center" as const,
  color:
    theme.palette.mode === "light"
      ? theme.palette.text.primary
      : theme.palette.common.white,
});

export const getErrorStyle = {
  mt: -6,
  mr: 1,
};

export const getResendLinkStyle = (isDisabled: boolean, theme: Theme) => ({
  color: isDisabled ? "gray" : theme.palette.primary.light,
  fontWeight: 500,
  mt: -0.3,
});

export const getSnackbarStyle = {
  width: "100%",
  color: "white",
  backgroundColor: "red",
};
