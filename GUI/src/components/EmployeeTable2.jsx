import { useEffect, lazy, useState, useCallback } from "react";
import { useAtom, useAtomValue } from "jotai";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  FormControl, // For the dropdown
  InputLabel, // For the dropdown label
  Select, // For the dropdown
  MenuItem, // For dropdown options
  Dialog, // For the salary details modal
  DialogTitle,
  DialogContent,
  DialogActions,
  CircularProgress, // For loading state in dialog
  Typography, // For displaying salary details
  Box, // For layout
} from "@mui/material";
import { toast } from "react-hot-toast";
import { employeesAtom } from "../services/atoms";
import {
  useEmployeeBasicInfo,
  useEmployeePartialDetails,
  useFetchMonthlyCutOff, // Import the new hook
  useMonthlyNet, // Import the new hook
} from "../services/hooks"; // Ensure these hooks are in services/hooks.js
import { jwtAtom } from "../services/atoms";
import { useNavigate } from "react-router-dom";

// Lazy load the Loader component
const Loader = lazy(() => import("./Loader"));

const EmployeesTable = ({ isAdmin }) => {
  const [employees, setEmployees] = useAtom(employeesAtom);
  const jwt = useAtomValue(jwtAtom);
  const navigate = useNavigate();

  // State for monthly cutoff selection
  const [selectedYearMonth, setSelectedYearMonth] = useState(""); // Stores YYYY-MM string

  // State for salary calculation dialog
  const [isSalaryDialogOpen, setIsSalaryDialogOpen] = useState(false);
  const [employeeForSalaryCalculation, setEmployeeForSalaryCalculation] =
    useState(null); // { employeeNumber, firstName, lastName }

  // Fetch available monthly cutoffs for the dropdown
  // Enabled only if isAdmin is true and JWT is available
  const {
    data: monthlyCutoffs,
    isLoading: isLoadingCutoffs,
    isError: isErrorCutoffs,
    error: cutoffsError,
  } = useFetchMonthlyCutOff(isAdmin ? jwt?.access : null); // Pass accessToken only if isAdmin

  useEffect(() => {
    if (isErrorCutoffs) {
      toast.error(`Error fetching monthly cutoffs: ${cutoffsError.message}`);
    }
  }, [isErrorCutoffs, cutoffsError]);

  // Set the latest month as default if available
  useEffect(() => {
    if (monthlyCutoffs && monthlyCutoffs.length > 0 && !selectedYearMonth) {
      // Find the latest month
      const latestMonth = monthlyCutoffs.reduce((latest, current) => {
        const latestYM = new Date(latest.yearMonth);
        const currentYM = new Date(current.yearMonth);
        return currentYM > latestYM ? current : latest;
      });
      setSelectedYearMonth(latestMonth.yearMonth);
    }
  }, [monthlyCutoffs, selectedYearMonth]);

  // Choose the right employee data fetcher based on isAdmin
  const useFetchEmployeeData = isAdmin
    ? useEmployeePartialDetails
    : useEmployeeBasicInfo;
  const {
    data: employeeData,
    isLoading: isLoadingEmployees,
    isError: isErrorEmployees,
    error: employeeError,
  } = useFetchEmployeeData(isAdmin ? jwt?.is_admin : jwt?.access); // Pass appropriate token

  useEffect(() => {
    if (employeeData) {
      setEmployees(employeeData);
    }
  }, [employeeData, setEmployees]);

  useEffect(() => {
    if (isErrorEmployees) {
      toast.error(`Error fetching employee data: ${employeeError.message}`);
    }
  }, [isErrorEmployees, employeeError]);

  // Fetch monthly net salary details when dialog is open and employee/month are selected
  const {
    data: monthlyNetSalaryData,
    isLoading: isLoadingMonthlyNet,
    isError: isErrorMonthlyNet,
    error: monthlyNetError,
  } = useMonthlyNet(
    jwt?.access, // Use appropriate access token
    employeeForSalaryCalculation?.employeeNumber,
    selectedYearMonth,
    isSalaryDialogOpen && !!employeeForSalaryCalculation && !!selectedYearMonth // Only enabled when dialog is open and data is ready
  );

  useEffect(() => {
    if (isErrorMonthlyNet) {
      toast.error(`Error calculating salary: ${monthlyNetError.message}`);
    }
  }, [isErrorMonthlyNet, monthlyNetError]);

  // Handle opening the salary dialog
  const handleOpenSalaryDialog = useCallback((employee) => {
    setEmployeeForSalaryCalculation(employee);
    setIsSalaryDialogOpen(true);
  }, []);

  // Handle closing the salary dialog
  const handleCloseSalaryDialog = useCallback(() => {
    setIsSalaryDialogOpen(false);
    setEmployeeForSalaryCalculation(null); // Clear employee data on close
  }, []);

  if (isLoadingCutoffs || isLoadingEmployees) return <Loader />;

  // Define columns based on isAdmin
  const columns = isAdmin
    ? [
        "Employee Number",
        "First Name",
        "Last Name",
        "SSS",
        "PhilHealth",
        "TIN",
        "Pag-IBIG",
        "Username",
        "Admin",
        "Actions", // New column for the button
      ]
    : ["Employee Number", "First Name", "Last Name", "Birthday"];

  return (
    <>
      {isAdmin && (
        <Box sx={{ mb: 3, display: "flex", alignItems: "center", gap: 2 }}>
          <FormControl sx={{ minWidth: 200 }}>
            <InputLabel id="month-select-label">Select Month</InputLabel>
            <Select
              labelId="month-select-label"
              id="month-select"
              value={selectedYearMonth}
              label="Select Month"
              onChange={(e) => setSelectedYearMonth(e.target.value)}
            >
              {monthlyCutoffs && monthlyCutoffs.length > 0 ? (
                monthlyCutoffs.map((cutoff) => (
                  <MenuItem key={cutoff.yearMonth} value={cutoff.yearMonth}>
                    {cutoff.yearMonth} ({cutoff.startDate} to {cutoff.endDate})
                  </MenuItem>
                ))
              ) : (
                <MenuItem value="" disabled>
                  No months available
                </MenuItem>
              )}
            </Select>
          </FormControl>
          <Typography variant="body2" color="text.secondary">
            Select a month to enable salary calculations.
          </Typography>
        </Box>
      )}

      <TableContainer component={Paper} sx={{ mt: 4, maxHeight: 600 }}>
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell key={column}>{column}</TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {employees.map((emp) => (
              <TableRow key={emp.id}>
                <TableCell>
                  {isAdmin ? (
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={(e) => {
                        e.preventDefault();
                        navigate(`/employees/${emp.employeeNumber}`);
                      }}
                    >
                      {emp.employeeNumber}
                    </Button>
                  ) : (
                    emp.employeeNumber
                  )}
                </TableCell>
                <TableCell>{emp.firstName}</TableCell>
                <TableCell>{emp.lastName}</TableCell>
                {isAdmin ? (
                  <>
                    <TableCell>{emp.sss}</TableCell>
                    <TableCell>{emp.philhealth}</TableCell>
                    <TableCell>{emp.tin}</TableCell>
                    <TableCell>{emp.pagibig}</TableCell>
                    <TableCell>{emp.user?.username}</TableCell>
                    <TableCell>{emp.user?.isAdmin ? "Yes" : "No"}</TableCell>
                    <TableCell>
                      <Button
                        variant="contained"
                        size="small"
                        disabled={!selectedYearMonth} // Disable if no month is selected
                        onClick={() => handleOpenSalaryDialog(emp)}
                      >
                        Calculate Salary
                      </Button>
                    </TableCell>
                  </>
                ) : (
                  <TableCell>{emp.birthday}</TableCell>
                )}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Salary Details Dialog */}
      <Dialog
        open={isSalaryDialogOpen}
        onClose={handleCloseSalaryDialog}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>
          Monthly Salary for {employeeForSalaryCalculation?.firstName}{" "}
          {employeeForSalaryCalculation?.lastName} ({selectedYearMonth})
        </DialogTitle>
        <DialogContent dividers>
          {isLoadingMonthlyNet ? (
            <Box
              display="flex"
              justifyContent="center"
              alignItems="center"
              height={100}
            >
              <CircularProgress />
              <Typography variant="body1" sx={{ ml: 2 }}>
                Calculating...
              </Typography>
            </Box>
          ) : isErrorMonthlyNet ? (
            <Typography color="error">
              Failed to load salary details:{" "}
              {monthlyNetError?.message || "Unknown error"}
            </Typography>
          ) : monthlyNetSalaryData ? (
            <Box sx={{ "& > div": { mb: 1 } }}>
              <Typography variant="h6" gutterBottom>
                Summary
              </Typography>
              <div>
                <Typography variant="body1">
                  <strong>Worked Hours:</strong>{" "}
                  {monthlyNetSalaryData.monthly_worked_hours?.toFixed(2) ||
                    "N/A"}
                </Typography>
              </div>
              <div>
                <Typography variant="body1">
                  <strong>Gross Salary:</strong> ₱
                  {monthlyNetSalaryData.gross_monthly_salary?.toFixed(2) ||
                    "N/A"}
                </Typography>
              </div>
              <Typography variant="h6" sx={{ mt: 2 }} gutterBottom>
                Deductions
              </Typography>
              <div>
                <Typography variant="body1">
                  <strong>SSS Deduction:</strong> ₱
                  {monthlyNetSalaryData.monthly_sss_deduction?.toFixed(2) ||
                    "N/A"}
                </Typography>
              </div>
              <div>
                <Typography variant="body1">
                  <strong>PhilHealth Deduction:</strong> ₱
                  {monthlyNetSalaryData.monthly_philhealth_deduction?.toFixed(
                    2
                  ) || "N/A"}
                </Typography>
              </div>
              <div>
                <Typography variant="body1">
                  <strong>Pag-IBIG Deduction:</strong> ₱
                  {monthlyNetSalaryData.monthly_pagibig_deduction?.toFixed(2) ||
                    "N/A"}
                </Typography>
              </div>
              <div>
                <Typography variant="body1">
                  <strong>Withholding Tax:</strong> ₱
                  {monthlyNetSalaryData.monthly_withholding_tax?.toFixed(2) ||
                    "N/A"}
                </Typography>
              </div>
              <div>
                <Typography variant="body1">
                  <strong>Total Deductions:</strong> ₱
                  {monthlyNetSalaryData.total_deductions?.toFixed(2) || "N/A"}
                </Typography>
              </div>
              <Typography variant="h5" sx={{ mt: 3, fontWeight: "bold" }}>
                Net Salary: ₱
                {monthlyNetSalaryData.net_monthly_salary?.toFixed(2) || "N/A"}
              </Typography>
            </Box>
          ) : (
            <Typography>
              Select an employee and month to calculate salary.
            </Typography>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseSalaryDialog}>Close</Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default EmployeesTable;
