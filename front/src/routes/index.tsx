import { Routes, Route } from "react-router-dom";

import { Typography } from "@mui/material";
import {
  EmailVerification,
  Chats,
  FindFriends,
  Login,
  Register,
  ResetPasswordCode,
  ResetPasswordEmail,
  ResetPasswordUpdate,
  ChatRequests,
  Settings,
} from "../pages";
import { PrivateAppLayout, PrivateAuthLayout } from "./layouts";

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route element={<PrivateAuthLayout />}>
        <Route path="/email-verification" element={<EmailVerification />} />
        <Route path="/reset-password/email" element={<ResetPasswordEmail />} />
        <Route path="/reset-password/code" element={<ResetPasswordCode />} />
        <Route
          path="/reset-password/update"
          element={<ResetPasswordUpdate />}
        />
      </Route>

      <Route element={<PrivateAppLayout />}>
        <Route path="/" element={<></>} />
        <Route path="/chats" element={<Chats />} />
        <Route path="/find-friends" element={<FindFriends />} />
        <Route path="/chat-requests" element={<ChatRequests />} />
        <Route path="/settings" element={<Settings />} />
        {/* Outras rotas privadas aqui */}
      </Route>

      <Route path="*" element={<Typography color="red">404</Typography>} />
    </Routes>
  );
};
