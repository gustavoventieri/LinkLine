import { jwtDecode } from "jwt-decode";

export const jwtDecoded = (token: string) => {
  const decoded = jwtDecode(token);

  return decoded;
};
