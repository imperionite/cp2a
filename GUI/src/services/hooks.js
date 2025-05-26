import { useQuery } from "@tanstack/react-query";
import { userKeys, employeeKeys } from "./queryKeyFactory";
import { getUserProfile, getEmployeePartialDetails } from "./http";

export const useUserProfile = (accessToken) => {
  return useQuery({
    queryKey: userKeys.detail("profile"), // defined related key for invallidate or caching
    queryFn: getUserProfile, // your async function that fetches user profile
    // enabled, // enables data fetching on condition
    staleTime: 5 * 60 * 1000, // optional: cache data for 5 minutes
    retry: 1, // optional: retry once on failure
    enabled: !!accessToken, // enable only if auth accessToken is truthy
  });
};

export const useEmployeePartialDetails = (is_admin) => {
  return useQuery({
    queryKey: employeeKeys.detail("partialDetails"),
    queryFn: getEmployeePartialDetails,
    staleTime: 5 * 60 * 1000,
    retry: 1,
    enabled: !!is_admin
  });
};
