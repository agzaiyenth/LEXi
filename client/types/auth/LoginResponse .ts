// types/auth.ts

import { ApiResponse } from "../common/apiresponse";


export interface LoginResponseDTO {
  accessToken: string;
  refreshToken: string;
  username: string;
  fullName: string;
  roles: string[];
}

export type LoginResponse = ApiResponse<LoginResponseDTO>;
