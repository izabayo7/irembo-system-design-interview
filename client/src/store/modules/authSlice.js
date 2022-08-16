import {
  createSlice,
  createAsyncThunk
} from "@reduxjs/toolkit";
import jwt from "jwt-decode";
import axios from "axios";
import AppServices from "../../services";
import {
  useDispatch
} from "react-redux";

const initialState = {
  user: null,
  isLoggedIn: false,
};

export const loadUser = createAsyncThunk(
  'auth/loadUser',
  async () => {
    const token = localStorage.getItem("user");
    if (token) {
      const bearer = JSON.parse(token || "{}");
      axios.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${bearer?.accessToken}`;

      return AppServices.getCurrentUser().then(({
        data
      }) => {
        return data;
      }).catch((e) => {
        localStorage.removeItem('user');
      })

    } else {
      throw new Error("No token found");
    }
  }
);

export const AuthSlice = createSlice({
  name: "Auth",
  initialState,
  reducers: {
    login: (state, action) => {
      state.user = action.payload;
      state.isLoggedIn = true;
    },
    logout: (state) => {
      localStorage.removeItem("user");
      state.user = undefined;
      state.isLoggedIn = false;
    },
    setUser: (state, action) => {
      state.user = action.payload;
      state.isLoggedIn = true;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(loadUser.pending, (state) => {
        state.user = null;
      })
      .addCase(loadUser.fulfilled, (state, action) => {
        state.user = {
          ...action.payload
        };
        if (action.payload) state.isLoggedIn = true;
      })
      .addCase(loadUser.rejected, (state) => {
        state.user = null;
      });
  },
});

export const {
  login,
  logout,
  setUser
} = AuthSlice.actions;

export const selectUser = (state) => state.auth.user;
export const selectIsLoggedIn = (state) => state.auth.isLoggedIn;

export default AuthSlice.reducer;