import React, { lazy } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useAtomValue } from "jotai";

import { jwtAtom } from "../services/atoms";

// const Signup = lazy(() => import("./Signup"));
const Login = lazy(() => import("./Login"));
const EmployeeManagement = lazy(() => import("./EmployeeManagement"));
const Home = lazy(() => import("./Home"));
const About = lazy(() => import("./AboutPage"));
const Services = lazy(() => import("./Services"));
const Contact = lazy(() => import("./Contact"));
const NotFound = lazy(() => import("./404"));

const RouterList = () => {
  const jwt = useAtomValue(jwtAtom);

  return (
    <Routes>
      <Route path="/home" element={<Home />} />
      <Route path="/about" element={<About />} />
      <Route path="/services" element={<Services />} />
      <Route path="/contact" element={<Contact />} />
      <Route
        path="/"
        element={
          jwt.access !== "" ? <Navigate to="/employee-management" /> : <Login />
        }
      />
      <Route
        path="/employee-management"
        element={
          jwt.access !== "" ? <EmployeeManagement /> : <Navigate to="/" />
        }
      />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default RouterList;
