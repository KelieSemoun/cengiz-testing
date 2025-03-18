describe('Me Page Spec', () => {

    it('Correctly loads user information', () => {
        cy.loginAndSetupMe();

        cy.get('p').contains('Name: Test USER').should('be.visible');
        cy.get('p').contains('Email: test@studio.com').should('be.visible');
        cy.get('p').contains('Create at: June 1, 2024').should('be.visible');
        cy.get('p').contains('Last update: July 1, 2024').should('be.visible');
    });

    it('Displays admin status when user is an admin', () => {
        cy.loginAndSetupMe();

        cy.get('p').contains('You are admin').should('be.visible');
    });

    it('Deletes the user account', () => {
        cy.loginAndSetupMe(false);

        cy.intercept('DELETE', '/api/user/1', { statusCode: 200 }).as('deleteUser');

        cy.get('button').contains('Detail').click();
        cy.wait('@deleteUser');

        cy.url().should('eq', Cypress.config().baseUrl); // Verify redirection
    });

    it('Navigates back using the Back button', () => {
        cy.loginAndSetupMe();
    
        // Click the back button
        cy.get('button').contains('back').click();
    
        // Ensure it navigates back to the sessions page
        cy.url().should('include', '/sessions');
    });
});
