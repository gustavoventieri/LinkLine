import React from "react";
import { Sidebar } from "../components";
import { Box } from "@mui/material";

interface IBaseLayoutProps {
  children: React.ReactNode;
  showIcon: boolean;
}

export const BaseLayout: React.FC<IBaseLayoutProps> = ({
  children,
  showIcon,
}) => {
  return (
    <Box display="flex">
      <Sidebar showIcon={showIcon} />

      <Box flexGrow={1}>{children}</Box>
    </Box>
  );
};
