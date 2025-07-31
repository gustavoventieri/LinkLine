import { BaseLayout } from "../../../shared/layouts";
import { api } from "../../../shared/services";
import { useNavigate } from "react-router-dom";
export const Settings = () => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    await api.post("/auth/logout");
    navigate("/login");
  };

  return (
    <BaseLayout showIcon>
      <div>
        <button onClick={handleLogout}>Log Out</button>
      </div>
    </BaseLayout>
  );
};
