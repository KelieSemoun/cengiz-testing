describe('Login spec', () => {
  beforeEach(() => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', (req) => {
      if (req.body.email === "yoga@studio.com" && req.body.password === "test!1234") {
        req.reply({
          statusCode: 200,
          body: {
            id: 1,
            username: 'userName',
            firstName: 'firstName',
            lastName: 'lastName',
            admin: true
          }
        });
      } else {
        req.reply({
          statusCode: 401,
          body: { message: "Invalid credentials" }
        });
      }
    }).as('login');

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')
  })
  it('Login successfull', () => {
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')
  })

  it('Login fails upon incomplete forms', () => {
    cy.get('input[formControlName=email]').type(`${"yoga@studio.com"}{enter}{enter}`)

    cy.get('p.error').should('be.visible').and('contain.text', 'An error occurred');
  })

  it('Login with wrong credentials', () => {
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('invalid{enter}{enter}');
  
    cy.wait('@login');
  
    cy.get('p.error').should('be.visible').and('contain.text', 'An error occurred');
  });  
});