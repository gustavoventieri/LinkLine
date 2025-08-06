import { useState, useEffect } from "react";
import {
  Box,
  CircularProgress,
  Typography,
  useMediaQuery,
  Theme,
  useTheme,
} from "@mui/material";
import { Diversity3Rounded } from "@mui/icons-material";
import { BaseLayout } from "../../../shared/layouts";
import { api } from "../../../shared/services";
import { RequestCard } from "../../../shared/components";
import { useNavigate } from "react-router-dom";
import { UUID } from "crypto";
import { useUsername } from "../../../shared/contexts/UsernameContext";

interface Request {
  id: UUID;
  senderUsername: string;
  receiverUsername: string;
  status: "pending" | "accepted";
  createdAt: string;
}

export const Notifications = () => {
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));
  const theme = useTheme();

  const [requests, setRequests] = useState<Request[]>([]);
  const { username } = useUsername();
  const [loading, setLoading] = useState(false);
  const [requestLoadingId, setRequestLoadingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchRequests = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await api.get("/friendship/get-all");

        const requestsArray: Request[] = (response.data || []).map(
          (req: any) => ({
            id: req.friendshipId,
            senderUsername: req.sender,
            receiverUsername: req.receiver,
            status: req.status.toLowerCase() as Request["status"],
            createdAt: req.createdAt,
          })
        );

        setRequests(requestsArray);
      } catch (err: any) {
        if (err.response?.status) {
          setError("Nenhuma solicitação encontrada");
          return;
        }
        setError("Erro ao buscar solicitações");
        setRequests([]);
      } finally {
        setLoading(false);
        console.log(username);
      }
    };
    fetchRequests();
  }, []);

  const handleUpdateChatRequestStatus = async (
    chatRequestId: UUID,
    newStatus: "ACCEPTED" | "DECLINED"
  ) => {
    setRequestLoadingId(chatRequestId);
    try {
      const payload = { status: newStatus };
      console.log(chatRequestId);
      const response = await api.put(
        `/friendship/update/${chatRequestId}`,
        payload
      );

      if (response.status === 200) {
        if (newStatus === "ACCEPTED") {
          await handleCreatePrivateChat(username);
          window.location.reload();
        }
      } else {
        console.warn(
          `Atualização de status falhou. Status HTTP: ${response.status}`
        );
      }
    } catch (error: any) {
      console.error(
        `Erro ao ${
          newStatus === "ACCEPTED" ? "aceitar" : "recusar"
        } solicitação:`,
        error?.response?.data || error.message || error
      );
    } finally {
      setRequestLoadingId(null);
    }
  };

  const handleCreatePrivateChat = async (username: string) => {
    try {
      await api.post("/chat/private/create", { username });
    } catch (error: any) {
      console.error(
        "Erro ao criar chat privado:",
        error?.response?.data || error.message || error
      );
    }
  };

  return (
    <BaseLayout showIcon>
      <Box display="flex" height="100vh">
        {!mdDown && (
          <Box
            width="30%"
            height="100vh"
            bgcolor={
              theme.palette.mode == "light"
                ? theme.palette.background.paper
                : theme.palette.background.default
            }
            display="flex"
            flexDirection="column"
            p={2}
          >
            <Box
              display="flex"
              flexDirection="column"
              alignItems="center"
              justifyContent="center"
              flexGrow={1}
              mb={10}
            >
              <Diversity3Rounded
                sx={{
                  width: 100,
                  height: 100,
                  color: theme.palette.mode === "dark" ? "white" : "grey.400",
                }}
              />
              <Typography
                fontSize={16}
                color={
                  theme.palette.mode === "dark"
                    ? "grey.300"
                    : theme.palette.text.secondary
                }
                textAlign="center"
              >
                New friendships knocking at the door!
              </Typography>
            </Box>
          </Box>
        )}

        <Box
          flex={1}
          display="flex"
          flexDirection="column"
          bgcolor={
            theme.palette.mode == "light"
              ? theme.palette.background.default
              : theme.palette.background.paper
          }
        >
          <Box
            display="flex"
            justifyContent="center"
            alignItems="center"
            gap={smDown ? 2 : 10}
            mt={smDown ? 2 : 1}
            flexWrap="wrap"
          >
            <Typography
              fontSize={25}
              p={smDown ? 0.5 : 2}
              fontWeight={600}
              color={theme.palette.primary.main}
            >
              Notifications
            </Typography>
          </Box>

          <Box display="flex" flexDirection="column" flexGrow={1}>
            {loading ? (
              <Box
                flexGrow={1}
                display="flex"
                alignItems="center"
                justifyContent="center"
              >
                <CircularProgress />
              </Box>
            ) : (
              <Box
                width="100%"
                display="flex"
                flexDirection="column"
                alignItems="center"
                justifyContent={
                  error || requests.length === 0 ? "center" : "start"
                }
                flexGrow={1}
                px={2}
              >
                {error || requests.length === 0 ? (
                  <Typography color="text.secondary" fontSize={16} mt={-10}>
                    {error || "Nenhuma solicitação encontrada"}
                  </Typography>
                ) : (
                  requests.map((req) => (
                    <RequestCard
                      theme={theme}
                      avatarUrl={""}
                      sender={req.senderUsername}
                      receiver={req.receiverUsername}
                      mdDown={mdDown}
                      smDown={smDown}
                      key={req.id}
                      currentUser={username}
                      status={req.status}
                      onAccept={() =>
                        handleUpdateChatRequestStatus(req.id, "ACCEPTED")
                      }
                      onDecline={() =>
                        handleUpdateChatRequestStatus(req.id, "DECLINED")
                      }
                      loading={requestLoadingId === req.id}
                    />
                  ))
                )}
              </Box>
            )}
          </Box>
        </Box>
      </Box>
    </BaseLayout>
  );
};
