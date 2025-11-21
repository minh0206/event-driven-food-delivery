import { Role } from "@repo/shared/models";

export interface RefreshToken {
  id: number;
  token: string;
  userId: number;
  userEmail: string;
  userRole: Role;
  expiresAt: string;
  revoked: boolean;
  createdAt: string;
  revokedAt: string | null;
}
