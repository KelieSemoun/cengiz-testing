describe('Form spec', () => {

    it('Successfully creates a new session', () => {
        cy.loginAndSetupFormCreate();

        // Mock API response
        cy.intercept('POST', '/api/session', { statusCode: 201 }).as('createSession');

        // Fill the form
        cy.get('input[formControlName="name"]').type('Morning Yoga');
        cy.get('input[formControlName="date"]').type('2025-03-15');
        cy.get('mat-select[formControlName="teacher_id"]').click();
        cy.get('mat-option').contains('Alice Brown').click();
        cy.get('textarea[formControlName="description"]').type('Relaxing morning yoga session');

        // Ensure Save button is enabled
        cy.get('button[type="submit"]').should('not.be.disabled');

        // Click Save
        cy.get('button[type="submit"]').click();

        // Wait for API call and verify navigation
        cy.wait('@createSession');
        cy.url().should('include', '/sessions');
    });

    it('Prevents submission of incomplete form', () => {
        cy.loginAndSetupFormCreate();

        // Ensure Save button is disabled
        cy.get('button[type="submit"]').should('be.disabled');
    });

    it('Navigates to update form with pre-filled data', () => {
        cy.loginAndSetupFormUpdate();

        // Ensure form fields contain the correct values
        cy.get('input[formControlName="name"]').should('have.value', 'Morning Yoga');
        cy.get('input[formControlName="date"]').should('have.value', '2025-03-15');
        cy.get('textarea[formControlName="description"]').should('have.value', 'Start your day with a relaxing yoga session.');
    });

    it('Successfully updates a session', () => {
        cy.loginAndSetupFormUpdate();

        // Mock API response
        cy.intercept('PUT', '/api/session/1', { statusCode: 200 }).as('updateSession');

        // Modify the description
        cy.get('textarea[formControlName="description"]').clear().type('Updated session details');

        // Click Save
        cy.get('button[type="submit"]').click();

        // Wait for API call and verify navigation
        cy.wait('@updateSession');
        cy.url().should('include', '/sessions');
    });
});
