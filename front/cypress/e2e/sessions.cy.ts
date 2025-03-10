describe('Session spec', () => {
    beforeEach(() => {
        cy.intercept('GET', '/api/session', {
            statusCode: 200,
            body: [
              {
                id: 1,
                name: 'Morning Yoga',
                date: '2025-03-15T08:00:00.000Z',
                description: 'Start your day with a relaxing yoga session.',
                teacher_id: 101
              },
              {
                id: 2,
                name: 'Evening Meditation',
                date: '2025-03-15T18:30:00.000Z',
                description: 'A peaceful meditation session to end your day.',
                teacher_id: 102
              }
            ]
        }).as('getSessions');

        cy.login();
        cy.wait('@getSessions'); 
    });

    it('Displays list of Sessions', () => {
        cy.get('.items .item').should('have.length', 2); 

        cy.get('.item').first().within(() => {
            cy.get('mat-card-title').should('contain.text', 'Morning Yoga');
            cy.get('mat-card-subtitle').should('contain.text', 'March 15, 2025');
            cy.get('p').should('contain.text', 'Start your day with a relaxing yoga session.');
        });

        cy.get('.item').last().within(() => {
            cy.get('mat-card-title').should('contain.text', 'Evening Meditation');
            cy.get('mat-card-subtitle').should('contain.text', 'March 15, 2025');
            cy.get('p').should('contain.text', 'A peaceful meditation session to end your day.');
        });
    })

    it('Displays Create and Edit button only as an admin', () => {
        cy.get('.items .item').should('have.length', 2);

        cy.get('button').contains('Create').should('be.visible');

        cy.get('.item').first().within(() => {
            cy.get('button').contains('Edit').should('be.visible');
        });
    })
})