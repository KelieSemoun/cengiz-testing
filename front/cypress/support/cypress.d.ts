/// <reference types="cypress" />

declare namespace Cypress {
  interface Chainable {
    /**
     * Custom command to log in
     * @param admin - Whether the user is an admin (true) or not (false)
     */
    login(admin?: boolean): Chainable<void>;
  }

  interface Chainable {
    /**
     * Custom command to log in and setup session details
     * @param admin - Whether the user is an admin (true) or not (false)
     */
    loginAndSetupSessionDetail(admin?: boolean): Chainable<void>;
  }

  interface Chainable {
    /**
     * Custom command to log in and setup session details
     * @param admin - Whether the user is an admin (true) or not (false)
     */
    loginAndSetupMe(admin?: boolean): Chainable<void>;
  }
}