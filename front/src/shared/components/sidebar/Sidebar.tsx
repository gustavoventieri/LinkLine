import React, { useState } from "react";
import {
  Box,
  IconButton,
  useTheme,
  Drawer,
  useMediaQuery,
  Typography,
  Theme,
  Button,
} from "@mui/material";
import { useLocation, useNavigate } from "react-router-dom";
import {
  ChatBubbleOutlineOutlined,
  DarkModeOutlined,
  GroupOutlined,
  LightModeOutlined,
  Menu,
  SearchOutlined,
  SettingsOutlined,
} from "@mui/icons-material";

import { useAppThemeContext } from "../../contexts";

interface ISidebarProps {
  showIcon: boolean;
}

export const Sidebar: React.FC<ISidebarProps> = ({ showIcon }) => {
  const theme = useTheme();
  const { themeName, toggleTheme } = useAppThemeContext();
  const location = useLocation();
  const navigate = useNavigate();
  const isMobile = useMediaQuery((theme: Theme) =>
    theme.breakpoints.down("md")
  );

  const topIconsStyle = {
    height: 25,
    width: 25,
  };

  const bottomIconsStyle = {
    height: 25,
    width: 25,
  };

  const navItems = [
    {
      icon: <ChatBubbleOutlineOutlined sx={topIconsStyle} />,
      path: "/chats",
      label: "Chats",
    },
    {
      icon: <GroupOutlined sx={topIconsStyle} />,
      path: "/chat-requests",
      label: "Chat Requests",
    },
    {
      icon: <SearchOutlined sx={topIconsStyle} />,
      path: "/find-friends",
      label: "Search Friends",
    },
    {
      icon: <ChatBubbleOutlineOutlined sx={topIconsStyle} />,
      path: "/ia",
      label: "Chats",
    },
    {
      icon:
        themeName === "light" ? (
          <DarkModeOutlined sx={bottomIconsStyle} />
        ) : (
          <LightModeOutlined sx={bottomIconsStyle} />
        ),
      path: "",
      label: "Switch Theme",
    },
    {
      icon: <SettingsOutlined sx={bottomIconsStyle} />,
      path: "/settings",
      label: "Settings",
    },
  ];
  const topIcons = navItems.slice(0, 4);
  const bottomIcons = navItems.slice(4);

  const [drawerOpen, setDrawerOpen] = useState(false);

  const renderNavItems = (items: typeof navItems) =>
    items.map((item, index) => {
      const isActive = location.pathname === item.path;

      return (
        <Box
          key={index}
          sx={{
            backgroundColor: isActive
              ? theme.palette.primary.main
              : "transparent",
            width: isMobile ? "100%" : "70px",
            height: "60px",
            display: "flex",
            alignItems: "center",
            justifyContent: isMobile ? "flex-start" : "center",
            px: isMobile ? 2 : 0,
            cursor: "pointer",
            transition: "background 0.3s",
            "&:hover": {
              backgroundColor: isActive
                ? theme.palette.primary.dark
                : themeName === "light"
                ? theme.palette.grey[400]
                : theme.palette.grey[600],
            },
          }}
          onClick={() => {
            item.label !== "Switch Theme" ? navigate(item.path) : toggleTheme();

            if (isMobile) setDrawerOpen(false);
          }}
        >
          <IconButton
            sx={{
              color: isActive
                ? theme.palette.primary.contrastText
                : themeName === "dark"
                ? "white"
                : theme.palette.primary.main,
              "&:hover": {
                backgroundColor: "transparent",
              },
            }}
          >
            {item.icon}
          </IconButton>
          {isMobile && (
            <Typography
              sx={{
                ml: 1,
                color: isActive
                  ? theme.palette.primary.contrastText
                  : themeName === "light"
                  ? theme.palette.primary.main
                  : "white",
              }}
            >
              {item.label}
            </Typography>
          )}
        </Box>
      );
    });

  const renderSidebarContent = () => (
    <Box
      sx={{
        width: isMobile ? 240 : "70px",
        height: "100vh",
        backgroundColor:
          themeName === "light"
            ? theme.palette.background.default
            : theme.palette.background.paper,
        display: "flex",
        flexDirection: "column",
        justifyContent: "space-between",
      }}
    >
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          px: 2,
        }}
      >
        <Typography
          sx={{
            fontSize: 38,
            color: themeName === "dark" ? "white" : theme.palette.primary.main,
            fontFamily: '"Irish Grover", cursive',
            mt: isMobile ? 2 : 0,
          }}
        >
          {isMobile ? "Link Line" : "LL"}
        </Typography>
      </Box>

      <Box sx={{ display: "flex", flexDirection: "column" }}>
        {renderNavItems(topIcons)}
      </Box>

      <Box sx={{ display: "flex", flexDirection: "column" }}>
        {renderNavItems(bottomIcons)}
      </Box>
    </Box>
  );

  return isMobile ? (
    <Box>
      {showIcon && (
        <Button
          variant="contained"
          disabled={drawerOpen}
          sx={{
            position: "fixed",
            color: "white",
            bottom: 16,
            right: 16,
            zIndex: 1300,
          }}
          onClick={() => setDrawerOpen(true)}
        >
          <Menu />
        </Button>
      )}

      <Drawer
        anchor="left"
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
      >
        {renderSidebarContent()}
      </Drawer>
    </Box>
  ) : (
    renderSidebarContent()
  );
};
