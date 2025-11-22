/**
 * Token Logger Utility
 * Logs the complete lifecycle of JWT access and refresh tokens
 * Only logs in development mode (NODE_ENV === 'development')
 */

class TokenLogger {
  private logPrefix = "[JWT Token Lifecycle]";
  private isDevelopment = process.env.NODE_ENV === "development";

  /**
   * Decode JWT token to extract expiration time
   */
  private decodeToken(token: string): { exp?: number; iat?: number } | null {
    try {
      const base64Url = token.split(".")[1];
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split("")
          .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
          .join("")
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error(`${this.logPrefix} Failed to decode token:`, error);
      return null;
    }
  }

  /**
   * Format timestamp for logging
   */
  private formatTimestamp(timestamp?: number): string {
    if (!timestamp) return "N/A";
    return new Date(timestamp * 1000).toLocaleString();
  }

  /**
   * Calculate time until expiration
   */
  private getTimeUntilExpiration(exp?: number): string {
    if (!exp) return "N/A";
    const now = Math.floor(Date.now() / 1000);
    const secondsLeft = exp - now;

    if (secondsLeft <= 0) return "EXPIRED";

    const minutes = Math.floor(secondsLeft / 60);
    const seconds = secondsLeft % 60;
    return `${minutes}m ${seconds}s`;
  }

  /**
   * Log access token received
   */
  logAccessTokenReceived(
    token: string,
    source: "login" | "register" | "refresh"
  ) {
    if (!this.isDevelopment) return;

    const decoded = this.decodeToken(token);
    const now = new Date().toISOString();

    console.group(`${this.logPrefix} 🔑 Access Token RECEIVED (${source})`);
    console.log("Timestamp:", now);
    console.log("Token (first 20 chars):", token.substring(0, 20) + "...");
    console.log("Issued At:", this.formatTimestamp(decoded?.iat));
    console.log("Expires At:", this.formatTimestamp(decoded?.exp));
    console.log(
      "Time Until Expiration:",
      this.getTimeUntilExpiration(decoded?.exp)
    );
    console.log("Full Payload:", decoded);
    console.groupEnd();
  }

  /**
   * Log access token stored
   */
  logAccessTokenStored(token: string) {
    if (!this.isDevelopment) return;

    const decoded = this.decodeToken(token);
    console.log(
      `${this.logPrefix} 💾 Access Token STORED in sessionStorage`,
      `| Expires: ${this.formatTimestamp(decoded?.exp)}`,
      `| Time left: ${this.getTimeUntilExpiration(decoded?.exp)}`
    );
  }

  /**
   * Log access token retrieved from storage
   */
  logAccessTokenRetrieved(token: string | null) {
    if (!this.isDevelopment) return;

    if (!token) {
      console.log(
        `${this.logPrefix} ⚠️ Access Token NOT FOUND in sessionStorage`
      );
      return;
    }

    const decoded = this.decodeToken(token);
    const timeLeft = this.getTimeUntilExpiration(decoded?.exp);
    const isExpired = timeLeft === "EXPIRED";

    console.log(
      `${this.logPrefix} 📤 Access Token RETRIEVED from sessionStorage`,
      `| Status: ${isExpired ? "❌ EXPIRED" : "✅ Valid"}`,
      `| Time left: ${timeLeft}`
    );
  }

  /**
   * Log access token attached to request
   */
  logAccessTokenAttached(url: string, token: string) {
    if (!this.isDevelopment) return;

    const decoded = this.decodeToken(token);
    console.log(
      `${this.logPrefix} 📨 Access Token ATTACHED to request`,
      `| URL: ${url}`,
      `| Time left: ${this.getTimeUntilExpiration(decoded?.exp)}`
    );
  }

  /**
   * Log token refresh initiated
   */
  logRefreshInitiated(reason: "401_error" | "manual" | "queued_request") {
    if (!this.isDevelopment) return;

    console.group(`${this.logPrefix} 🔄 Token REFRESH Initiated`);
    console.log("Reason:", reason);
    console.log("Timestamp:", new Date().toISOString());
    console.log("Note: Refresh token cookie sent automatically by browser");
    console.groupEnd();
  }

  /**
   * Log token refresh success
   */
  logRefreshSuccess(newToken: string, queuedRequestsCount: number = 0) {
    if (!this.isDevelopment) return;

    const decoded = this.decodeToken(newToken);
    console.group(`${this.logPrefix} ✅ Token REFRESH Successful`);
    console.log("New Access Token Received");
    console.log("Issued At:", this.formatTimestamp(decoded?.iat));
    console.log("Expires At:", this.formatTimestamp(decoded?.exp));
    console.log(
      "Time Until Expiration:",
      this.getTimeUntilExpiration(decoded?.exp)
    );
    if (queuedRequestsCount > 0) {
      console.log("Queued Requests Processed:", queuedRequestsCount);
    }
    console.groupEnd();
  }

  /**
   * Log token refresh failure
   */
  logRefreshFailure(error: unknown) {
    if (!this.isDevelopment) return;

    console.group(`${this.logPrefix} ❌ Token REFRESH Failed`);
    console.error("Error:", error);
    console.log("Timestamp:", new Date().toISOString());
    console.log("Action: User will be logged out");
    console.groupEnd();
  }

  /**
   * Log token removed
   */
  logTokenRemoved(
    reason: "logout" | "refresh_failed" | "initialization_failed"
  ) {
    if (!this.isDevelopment) return;

    console.log(
      `${this.logPrefix} 🗑️ Access Token REMOVED from sessionStorage`,
      `| Reason: ${reason}`,
      `| Timestamp: ${new Date().toISOString()}`
    );
  }

  /**
   * Log 401 error detected
   */
  log401Error(url: string, isRetry: boolean) {
    if (!this.isDevelopment) return;

    console.warn(
      `${this.logPrefix} ⚠️ 401 Unauthorized Error`,
      `| URL: ${url}`,
      `| Is Retry: ${isRetry}`,
      `| Action: ${isRetry ? "Logout user" : "Attempt token refresh"}`
    );
  }

  /**
   * Log request queued during refresh
   */
  logRequestQueued(url: string, queueLength: number) {
    if (!this.isDevelopment) return;

    console.log(
      `${this.logPrefix} ⏳ Request QUEUED (refresh in progress)`,
      `| URL: ${url}`,
      `| Queue Length: ${queueLength}`
    );
  }

  /**
   * Log initialization
   */
  logInitialization(hasToken: boolean) {
    if (!this.isDevelopment) return;

    console.group(`${this.logPrefix} 🚀 Auth Store INITIALIZATION`);
    console.log("Timestamp:", new Date().toISOString());
    console.log("Access Token Found:", hasToken ? "✅ Yes" : "❌ No");
    if (hasToken) {
      console.log("Action: Fetching user profile");
    }
    console.groupEnd();
  }

  /**
   * Log logout
   */
  logLogout() {
    if (!this.isDevelopment) return;

    console.group(`${this.logPrefix} 👋 User LOGOUT`);
    console.log("Timestamp:", new Date().toISOString());
    console.log("Action: Clearing access token from sessionStorage");
    console.log("Action: Clearing refresh token cookie via backend");
    console.groupEnd();
  }
}

export const tokenLogger = new TokenLogger();
