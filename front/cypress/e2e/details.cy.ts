describe('Details spec', () => {

    it('Correctly loads in details page', () => {
        cy.loginAndSetupSessionDetail();

        cy.url().should('include', 'detail/1');

        cy.get('mat-card-title').should('contain', 'Morning Yoga')
        cy.get('mat-card-subtitle').should('contain', 'Alice BROWN')
        cy.get('mat-card-content').should('contain.text', '2 attendees')
    });

    it('Displays Delete button when logged as admin', () => {
        cy.loginAndSetupSessionDetail();

        cy.get('mat-card-title').within(() => {
            cy.get('button').should('exist').and('contain', 'Delete');
        });
    });
    
    it('Does not display Delete button when logged as non admin', () => {
            cy.loginAndSetupSessionDetail(false);

        cy.get('mat-card-title').within(() => {
            cy.get('button').should('exist').and('not.contain', 'Delete');
        });
    });

    it('Deletes session when clicking Delete button', () => {
        cy.loginAndSetupSessionDetail();
    
        cy.intercept('DELETE', '/api/session/1', { statusCode: 200 }).as('deleteSession');

        cy.get('mat-card-title').within(() => {
            cy.get('button').contains('Delete').click();
        });
    
        cy.wait('@deleteSession');
    
        cy.url().should('include', '/sessions');
    });
    
    it('Allows user to participate and unparticipate in a session', () => {
        cy.intercept('GET', '/api/user/1', {
            statusCode: 200,
            body: {
                id: 1,
                firstname: 'test',
                lastname: 'user',
                admin: false
            }
        }).as('getUser');

        cy.loginAndSetupSessionDetail(false); // Non-admin user

        cy.intercept('GET', '/api/session/1', {
            statusCode: 200,
            body: {
                id: 1,
                name: 'Morning Yoga',
                date: '2025-03-15T08:00:00.000Z',
                description: 'Start your day with a relaxing yoga session.',
                createdAt: '2025-02-10T14:00:00.000Z',
                updatedAt: '2025-02-15T10:30:00.000Z',
                users: [{ id: 101, name: 'John Doe' }, { id: 102, name: 'Jane Smith' }], 
                teacher_id: 201 
            }
        }).as('getSessionInitial');

        cy.intercept('GET', '/api/session/1', {
            statusCode: 200,
            body: {
                id: 1,
                name: 'Morning Yoga',
                date: '2025-03-15T08:00:00.000Z',
                description: 'Start your day with a relaxing yoga session.',
                createdAt: '2025-02-10T14:00:00.000Z',
                updatedAt: '2025-02-15T10:30:00.000Z',
                users: [{ id: 101, name: 'John Doe' }, { id: 102, name: 'Jane Smith' }, { id: 1, name: 'test user'}], 
                teacher_id: 201 
            }
        }).as('getSessionUpdated');
    
        // Mock API for participation
        cy.intercept('POST', '/api/session/1/participate/1', { statusCode: 200 }).as('participateSession');
        cy.intercept('POST', '/api/session/1/unParticipate/1', { statusCode: 200 }).as('unParticipateSession');
    
        // Click the Participate button
        cy.get('button').contains('Participate').click();
        cy.wait('@participateSession');
        cy.wait('@getSessionUpdated');
    
        // Ensure Participate button is replaced with "Do not participate"
        cy.get('button').contains('Do not participate').should('be.visible');
    
        // Click the "Do not participate" button
        cy.get('button').contains('Do not participate').click();
        cy.wait('@unParticipateSession');
        cy.wait('@getSessionInitial');
    
        // Ensure button is back to "Participate"
        cy.get('button').contains('Participate').should('be.visible');
    });

    /*it('Goes back when clicking the back button', () => {
        cy.loginAndSetupSessionDetail();
        
        cy.window().then((win) => {
            cy.spy(win.history, 'back');
        });
    
        cy.get('mat-icon-button').contains('arrow_back').click();
        cy.window().its('history.back').should('have.been.calledOnce');
    });    */
});
