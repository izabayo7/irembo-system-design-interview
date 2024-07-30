import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Provider } from "react-redux";
import { store } from "./store/index";
import { Toaster } from "react-hot-toast";
import Login from "./pages/Login";
import CreatePasswordReset from "./pages/CreatePasswordReset";
import UpdatePasswordReset from "./pages/UpdatePasswordReset";
import SigninWithEmail from "./pages/SigninWithEmail";
import Dashboard from "./pages/Dashboard";
import NotFound from "./pages/404";
import DashboardLayout from "./components/Layout/Dashboard";
import Privileges from "./pages/Privileges";
import Roles from "./pages/Roles";
import Users from "./pages/Users";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <React.StrictMode>
      <Provider store={store}>
        <Router>
          <Routes>
            <Route
              path="/"
              element={<DashboardLayout children={<Dashboard />} />}
            />
            <Route
              path="/data-management/privileges"
              element={<DashboardLayout children={<Privileges />} />}
            />
            <Route
              path="/data-management/roles"
              element={<DashboardLayout children={<Roles />} />}
            />
            <Route
              path="/data-management/users"
              element={<DashboardLayout children={<Users />} />}
            />
            <Route path="login" element={<Login />} />
            <Route path="reset-password" element={<CreatePasswordReset />} />
            <Route
              path="reset-password/:token"
              element={<UpdatePasswordReset />}
            />
            <Route path="login/with-email" element={<SigninWithEmail />} />
            <Route
              path="login/with-email/:token"
              element={<SigninWithEmail />}
            />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Router>
      </Provider>
      <Toaster />
    </React.StrictMode>
  </React.StrictMode>
);
