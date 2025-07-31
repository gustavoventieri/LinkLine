import { memo, useState } from "react";
import {
  PersonOutlineOutlined,
  PersonAdd,
  CheckCircleOutline,
  HourglassEmpty,
} from "@mui/icons-material";
import {
  Avatar,
  Box,
  Button,
  Card,
  CircularProgress,
  Typography,
  useTheme,
} from "@mui/material";

export type UserRelationshipStatus = "PENDING" | "ACCEPTED" | "NOT_FRIEND";

interface UserFoundCardProps {
  username: string;
  avatarUrl?: string;
  smDown: boolean;
  mdDown: boolean;
  relationshipStatus: UserRelationshipStatus;
  onAddFriend: () => Promise<void>;
  onAcceptRequest?: () => Promise<void>;
  onDeclineRequest?: () => Promise<void>;
}

export const UserFoundCard = memo(
  ({
    username,
    avatarUrl,
    smDown,
    mdDown,
    relationshipStatus,
    onAddFriend,
  }: UserFoundCardProps) => {
    const theme = useTheme();
    const [isLoading, setIsLoading] = useState(false);

    const handleAction = async (callback: () => Promise<void>) => {
      try {
        setIsLoading(true);
        await callback();
      } finally {
        setIsLoading(false);
      }
    };

    const renderActionButton = () => {
      if (isLoading) {
        return (
          <Button
            variant="contained"
            disabled
            sx={{ minWidth: smDown ? "90px" : "110px", height: "36.5px" }}
          >
            <CircularProgress size={24} color="inherit" />
          </Button>
        );
      }

      switch (relationshipStatus) {
        case "NOT_FRIEND":
          return (
            <Button
              variant="contained"
              startIcon={<PersonAdd />}
              onClick={() => handleAction(onAddFriend)}
              sx={{
                minWidth: smDown ? "90px" : "110px",
                fontSize: smDown ? 10 : 14,
              }}
            >
              {!smDown ? "Send Request" : "Request"}
            </Button>
          );

        case "PENDING":
          return (
            <>
              <Button
                variant="outlined"
                startIcon={<HourglassEmpty sx={{ color: "#FFD52F" }} />}
                disabled
                sx={{ minWidth: smDown ? "90px" : "110px" }}
              >
                <Typography fontSize={14} color="#FFD52F" fontWeight="bold">
                  Pending
                </Typography>
              </Button>
            </>
          );

        case "ACCEPTED":
          return (
            <>
              <Button
                variant="text"
                startIcon={<CheckCircleOutline sx={{ color: "#15FF00" }} />}
                disabled
                sx={{ minWidth: smDown ? "90px" : "110px" }}
              >
                <Typography fontSize={14} color="#15FF00" fontWeight="bold">
                  Friends
                </Typography>
              </Button>
            </>
          );

        default:
          return null;
      }
    };

    return (
      <Card
        sx={{
          display: "flex",
          flexDirection: "row",
          alignItems: "center",

          p: smDown ? 1.5 : 2,
          width: mdDown ? "100%" : "80%",
          mb: 1.5,
          borderRadius: "8px",
          gap: 2,
          backgroundColor: theme.palette.background.paper,
        }}
      >
        <Avatar
          src={avatarUrl}
          sx={{
            width: smDown ? 40 : 48,
            height: smDown ? 40 : 48,
            bgcolor: avatarUrl ? "transparent" : theme.palette.primary.main,
            color: theme.palette.primary.main,
            fontSize: smDown ? "1rem" : "1.25rem",
          }}
        >
          {!avatarUrl && username
            ? username[0].toUpperCase()
            : !avatarUrl && <PersonOutlineOutlined />}
        </Avatar>

        <Box
          sx={{
            flexGrow: 1,
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
          }}
        >
          <Typography variant="subtitle1" fontWeight={500} noWrap>
            {username}
          </Typography>
        </Box>

        <Box sx={{ ml: "auto", display: "flex", alignItems: "center", gap: 1 }}>
          {renderActionButton()}
        </Box>
      </Card>
    );
  }
);
