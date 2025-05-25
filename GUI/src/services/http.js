import axios from "axios";
// import { jwtDecode } from "jwt-decode";
import qs from "qs";

const baseURL = 'http://localhost:8080';

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

// Interceptor to add the Authorization header with the access token
http.interceptors.request.use(
  (config) => {
    const token = getAccessToken(); // Get the access token from localStorage

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

// API functions
// const login = async (credentials) => {
//   const response = await http.post("/api/auth/login", credentials);
//   console.log(http)
//   return response.data;
// };

const login = async (credentials) => {
  const response = await axios.post(`${baseURL}/api/auth/login`, credentials, {
    withCredentials: true,
    headers: { 'Content-Type': 'application/json' },
  })

  if (response.status === 200 && response.data) {
    return response.data
  }
}

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


export {
  login,
  // signup,
  getAccessToken,
  getUserProfile,
};