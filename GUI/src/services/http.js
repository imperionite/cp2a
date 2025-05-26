import axios from "axios";
import qs from "qs";

/***
 * Important: create a .env file at the root of this sub-project (GUI folder) and place:
 * VITE_BASE_URL=http://localhost:8080 or the port of the Spring Boot app
 ***/

const baseURL = import.meta.env.VITE_BASE_URL;
const http = axios.create({
  baseURL: baseURL,
  withCredentials: true,
  timeout: 100000,
});

// Function to get access token
const getAccessToken = () => {
  const jwtAtom = localStorage.getItem("jwtAtom");
  let token = jwtAtom ? JSON.parse(jwtAtom) : null;
  return token ? token.access : null;
};

// Function to get refresh token
const getRefreshToken = () => {
  const jwtAtom = localStorage.getItem("jwtAtom");
  let token = jwtAtom ? JSON.parse(jwtAtom) : null;
  return token ? token.refresh : null;
};

// Interceptor to add the Authorization header with the access token
http.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }

    if (config.data) {
      if (
        typeof config.data === "object" &&
        !(config.data instanceof FormData)
      ) {
        config.headers["Content-Type"] = "application/json";
      } else if (typeof config.data === "string") {
        config.headers["Content-Type"] = "application/x-www-form-urlencoded";
      } else if (config.data instanceof FormData) {
        delete config.headers["Content-Type"];
      }
    }

    if (
      config.headers["Content-Type"] === "application/x-www-form-urlencoded"
    ) {
      config.data = qs.stringify(config.data);
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor to handle 401 errors (no refresh endpoint available)
http.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      // For this setup, can't refresh the access token, so just log out
      localStorage.removeItem("jwtAtom");
      // You might also want to redirect to the login page here
      console.log("Session expired or unauthorized. Please log in again.");
      // Trigger a logout event or redirect:
      //window.location.href = "/"; // login route
      return Promise.reject(error);
    }

    return Promise.reject(error);
  }
);

// API functions
const login = async (data) => {
  const response = await http.post("/api/auth/login", data);
  return response.data;
};

/* const login = async (credentials) => {
  const response = await axios.post(`${baseURL}/api/auth/login`, credentials, {
    withCredentials: true,
    headers: { "Content-Type": "application/json" },
  });

  if (response.status === 200 && response.data) {
    return response.data;
  }
}; */

/* const signup = async (userData) => {
  try {
    const response = await http.post("/api/users/auth/registration/", userData);
    return response.data;
  } catch (error) {
    console.error("Signup error:", error);
    throw error;
  }
}; */

const getUserProfile = async () => {
  const response = await http.get("/api/users/auth/user/");
  return response.data;
};

const getEmployeePartialDetails = async () => {
  const response = await http.get("/api/employees/partial/details");
  return response.data;
};

const getEmployeeBasicInfo = async () => {
  const response = await http.get("/api/employees/basic-info");
  return response.data;
};

export {
  login,
  // signup,
  getAccessToken,
  getUserProfile,
  getRefreshToken,
  getEmployeePartialDetails,
  getEmployeeBasicInfo,
};
