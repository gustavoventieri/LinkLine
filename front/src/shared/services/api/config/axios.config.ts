// src/services/api.ts
import axios from "axios";

export const api = axios.create({
  baseURL: "https://8t0866rg-8080.brs.devtunnels.ms/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});
