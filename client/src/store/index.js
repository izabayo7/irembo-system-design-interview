import {
  configureStore
} from '@reduxjs/toolkit'
import authReducer from './modules/authSlice';
import userReducer from './modules/userSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    users: userReducer,
  },
})