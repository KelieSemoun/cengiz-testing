describe('Details spec', () => {
    beforeEach(() => {
        cy.loginAndSetupSessionDetail();
    });

    it('Correctly loads in details page', () => {
        cy.url().should('include', 'detail/1');

        cy.get('mat-card-title').should('contain', 'Morning Yoga')
        cy.get('mat-card-subtitle').should('contain', 'Alice BROWN')
        cy.get('mat-card-content').should('contain.text', '2 attendees')
    });

    it('Displays Delete button when logged as admin', () => {
        cy.get('mat-card-title').within(() => {
            cy.get('button').should('exist').and('contain', 'Delete');
        });
    });
    
    it('Does not display Delete button when logged as non admin', () => {
        beforeEach(() => {
            cy.loginAndSetupSessionDetail(false);
        });

        cy.get('mat-card-title').within(() => {
            cy.get('button').should('not.exist');
        });
    })
});
