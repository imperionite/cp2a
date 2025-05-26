import { atomWithStorage } from "jotai/utils";
import { atom } from "jotai";

export const jwtAtom = atomWithStorage("jwtAtom", {
  access: "",
  refresh: "",
  username: "",
  is_admin: '',
});

export const employeesAtom = atom([]);
