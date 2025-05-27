import React, { lazy } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useAtomValue } from "jotai";
import { jwtAtom } from "../services/atoms";

const Login = lazy(() => import("./Login"));
const Employees = lazy(() => import("./Employees"));
const Home = lazy(() => import("./Home"));
const About = lazy(() => import("./AboutPage"));
const Services = lazy(() => import("./Services"));
const Contact = lazy(() => import("./Contact"));
const EmployeeDetailsPage = lazy(() => import("./EmployeeDetailsPage"));
const NotFound = lazy(() => import("./404"));

const RouterList = () => {
  const jwt = useAtomValue(jwtAtom);

  return (
    <Routes>
      <Route
        path="/"
        element={jwt.access ? <Navigate to="/employees" replace /> : <Login />}
      />
      <Route
        path="/employees"
        element={jwt.access ? <Employees /> : <Navigate to="/" replace />}
      />
      <Route
        path="/employees/:employeeNumber"
        element={
          jwt.access ? <EmployeeDetailsPage /> : <Navigate to="/" replace />
        }
      />
      <Route path="/home" element={<Home />} />
      <Route path="/about" element={<About />} />
      <Route path="/services" element={<Services />} />
      <Route path="/contact" element={<Contact />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default RouterList;
