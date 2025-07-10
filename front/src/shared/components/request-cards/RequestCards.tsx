import { memo, useMemo } from "react";
import { PersonOutlineOutlined } from "@mui/icons-material";
import {
  Avatar,
  Box,
  Button,
  Card,
  CircularProgress,
  Typography,
} from "@mui/material";

interface RequestCardProps {
  avatarUrl: string;
  smDown: boolean;
  mdDown: boolean;
  username: string;
  status: "pending" | "accepted" | "declined";
  type: 0 | 1; // 0 = sent, 1 = received
  onAccept?: () => void;
  onDecline?: () => void;
  loading?: boolean;
}

export const RequestCard = memo(
  ({
    avatarUrl,
    smDown,
    mdDown,
    username,
    status,
    type,
    onAccept,
    onDecline,
    loading = false,
  }: RequestCardProps) => {
    const content = useMemo(() => {
      if (status === "pending") {
        if (type === 1) {
          return (
            <Box
              alignItems="center"
              display="flex"
              justifyContent="space-between"
            >
              <Typography fontSize={14}>
                <strong>{username}</strong> wants to follow you
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
                  sx={{ py: 0.5 }}
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
                  sx={{ py: 0.5 }}
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
        } else {
          return (
            <Typography fontSize={smDown ? 10 : 12}>
              You sent a follow request to <strong>{username}</strong>
            </Typography>
          );
        }
      }

      if (status === "accepted") {
        return (
          <Typography fontSize={14}>
            {type === 1 ? (
              <>
                You accepted <strong>{username}</strong>'s request.
              </>
            ) : (
              <>
                <strong>{username}</strong> accepted your request.
              </>
            )}
          </Typography>
        );
      }

      if (status === "declined") {
        return (
          <Typography fontSize={14}>
            {type === 1 ? (
              <>
                You declined <strong>{username}</strong>'s request.
              </>
            ) : (
              <>
                <strong>{username}</strong> declined your request.
              </>
            )}
          </Typography>
        );
      }

      return (
        <Typography fontSize={14}>
          Unknown request status for <strong>{username}</strong>
        </Typography>
      );
    }, [avatarUrl, status, type, username, onAccept, onDecline, loading]);

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
          gap: 2,
          backgroundColor: "primary.paper",
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
            {!avatarUrl && username
              ? username[0].toUpperCase()
              : !avatarUrl && (
                  <PersonOutlineOutlined style={{ color: "black" }} />
                )}
          </Avatar>
          <Box sx={{ width: "100%" }}>{content}</Box>
        </Box>
      </Card>
    );
  }
);
