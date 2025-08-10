import { memo } from "react";
import {
  Avatar,
  Box,
  Button,
  Card,
  CircularProgress,
  Theme,
  Typography,
} from "@mui/material";

interface RequestCardProps {
  avatarUrl: string;
  smDown: boolean;
  mdDown: boolean;
  theme: Theme;
  currentUser: string;
  sender: string;
  receiver: string;
  status: "PENDING" | "ACCEPTED";
  onAccept?: () => void;
  onDecline?: () => void;
  onRemove?: () => void;
  loading?: boolean;
}

export const RequestCard = memo(
  ({
    avatarUrl,
    smDown,
    mdDown,
    currentUser,
    theme,
    sender,
    receiver,
    status,
    onAccept,
    onDecline,
    onRemove,
    loading = false,
  }: RequestCardProps) => {
    const isYouSender = currentUser === sender;
    const isYouReceiver = currentUser === receiver;
    const otherUser = isYouSender ? receiver : sender;
    const avatarLetter = otherUser[0]?.toUpperCase() || "?";

    const renderContent = () => {
      switch (status) {
        case "PENDING":
          if (isYouSender) {
            return (
              <Box
                alignItems="center"
                display="flex"
                justifyContent="space-between"
              >
                <Typography fontSize={smDown ? 10 : 12}>
                  You sent a follow request to <strong>{receiver}</strong>
                </Typography>
                <Box
                  display="flex"
                  ml={1}
                  gap={0.5}
                  flexDirection={smDown ? "column" : "row"}
                >
                  <Button
                    variant="contained"
                    onClick={onRemove}
                    disabled={loading}
                    sx={{ py: 0.5, position: "relative" }}
                  >
                    Remove
                    {loading && (
                      <CircularProgress
                        size={24}
                        sx={{
                          position: "absolute",
                          top: "50%",
                          left: "50%",
                          marginTop: "-12px",
                          marginLeft: "-12px",
                        }}
                      />
                    )}
                  </Button>
                </Box>
              </Box>
            );
          }

          if (isYouReceiver) {
            return (
              <Box
                alignItems="center"
                display="flex"
                justifyContent="space-between"
              >
                <Typography fontSize={14}>
                  <strong>{sender}</strong> wants to follow you
                </Typography>

                <Box
                  display="flex"
                  ml={1}
                  gap={0.5}
                  flexDirection={smDown ? "column" : "row"}
                >
                  <Button
                    variant="contained"
                    onClick={onAccept}
                    disabled={loading}
                    sx={{ py: 0.5, position: "relative" }}
                  >
                    Accept
                    {loading && (
                      <CircularProgress
                        size={24}
                        sx={{
                          position: "absolute",
                          top: "50%",
                          left: "50%",
                          marginTop: "-12px",
                          marginLeft: "-12px",
                          color: "white",
                        }}
                      />
                    )}
                  </Button>
                  <Button
                    variant="outlined"
                    onClick={onDecline}
                    disabled={loading}
                    sx={{ py: 0.5, position: "relative" }}
                  >
                    Decline
                    {loading && (
                      <CircularProgress
                        size={24}
                        sx={{
                          position: "absolute",
                          top: "50%",
                          left: "50%",
                          marginTop: "-12px",
                          marginLeft: "-12px",
                        }}
                      />
                    )}
                  </Button>
                </Box>
              </Box>
            );
          }
          break;

        case "ACCEPTED":
          return (
            <Typography fontSize={smDown ? 10 : 12}>
              You and <strong>{otherUser}</strong> are now connected.
            </Typography>
          );
      }

      return null;
    };

    return (
      <Card
        elevation={1}
        sx={{
          display: "flex",
          flexDirection: "row",
          alignItems: "center",
          p: smDown ? 1 : 2,
          width: mdDown ? "100%" : "80%",
          mb: 1.5,
          borderRadius: "8px",
          boxShadow: 4,
          gap: 2,
          backgroundColor:
            theme.palette.mode == "light"
              ? theme.palette.background.paper
              : theme.palette.background.default,
        }}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            flexDirection: "row",
            gap: 2,
            width: "100%",
          }}
        >
          <Avatar
            src={avatarUrl}
            sx={{
              width: smDown ? 40 : 48,
              height: smDown ? 40 : 48,
              bgcolor: avatarUrl ? "transparent" : "primary.main",
              color: "primary.contrastText",
              fontSize: smDown ? "1rem" : "1.25rem",
            }}
          >
            {!avatarUrl ? avatarLetter : null}
          </Avatar>

          <Box sx={{ width: "100%" }}>{renderContent()}</Box>
        </Box>
      </Card>
    );
  }
);
