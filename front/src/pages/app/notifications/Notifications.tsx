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

interface Request {
  id: string;
  senderUsername: string;
  receiverUsername: string;
  status: "declined" | "pending" | "accepted";
  createdAt: string;
  updatedAt: string;
}

export const Notifications = () => {
  const mdDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("md"));
  const smDown = useMediaQuery((theme: Theme) => theme.breakpoints.down("sm"));
  const theme = useTheme();

  const [statusFilter, setStatusFilter] = useState<
    "declined" | "pending" | "accepted"
  >("pending");
  const [tab, setTab] = useState<0 | 1>(1);
  const [requests, setRequests] = useState<Request[]>([]);
  const [loading, setLoading] = useState(false);
  const [requestLoadingId, setRequestLoadingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const fetchRequests = async () => {
    setLoading(true);
    setError(null);
    try {
      const type = tab === 0 ? "sent" : "received";
      const response = await api.get(`/chat-request/${type}/${statusFilter}`);

      const requestsObject = response.data.chat_requests || {};
      const requestsArray: Request[] = Object.values(requestsObject).map(
        (req: any) => ({
          id: req.id,
          senderUsername: req.sender.username,
          receiverUsername: req.receiver.username,
          status: req.status.toLowerCase() as Request["status"],
          createdAt: req.createdAt || "",
          updatedAt: req.updatedAt || "",
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
    }
  };

  useEffect(() => {
    fetchRequests();
  }, [statusFilter, tab]);

  const handleUpdateChatRequestStatus = async (
    username: string,
    chatRequestId: string,
    newStatus: "ACCEPTED" | "DECLINED"
  ) => {
    setRequestLoadingId(chatRequestId);
    try {
      const payload = { newStatus };
      const response = await api.put(
        `/chat-request/update/${chatRequestId}`,
        payload
      );

      if (response.status === 200 || response.status === 201) {
        if (newStatus === "ACCEPTED") {
          await handleCreatePrivateChat(username);
        }
        await fetchRequests();
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
      await api.post("/private-chat/create", { username });
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
              theme.palette.mode === "light"
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
            theme.palette.mode === "light"
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
                mt={-10}
                display="flex"
                flexDirection="column"
                alignItems="center"
                justifyContent="center"
                flexGrow={1}
              >
                {error || requests.length === 0 ? (
                  <Typography color="text.secondary" fontSize={16}>
                    {error || "Nenhuma solicitação encontrada"}
                  </Typography>
                ) : (
                  requests.map((req) => (
                    <RequestCard
                      avatarUrl={req.receiverUsername}
                      mdDown={mdDown}
                      smDown={smDown}
                      key={req.id}
                      username={
                        tab === 0 ? req.receiverUsername : req.senderUsername
                      }
                      status={req.status}
                      onAccept={() =>
                        handleUpdateChatRequestStatus(
                          tab === 0 ? req.receiverUsername : req.senderUsername,
                          req.id,
                          "ACCEPTED"
                        )
                      }
                      onDecline={() =>
                        handleUpdateChatRequestStatus(
                          tab === 0 ? req.receiverUsername : req.senderUsername,
                          req.id,
                          "DECLINED"
                        )
                      }
                      type={tab}
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
