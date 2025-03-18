describe('register spec', () => {
    beforeEach(() => {
        cy.visit('/register')

        cy.intercept('POST', '/api/auth/register', (req) => {
            req.reply({
                statusCode: 200,
                body: { message: "User registered succesfully !"}
            })
        }).as('register');
    })

    it('Register succesfull', () => {
        cy.title().should('equal', 'Register')

        cy.get('input[formControlName=email]').type("newyoga@studio.com")
        cy.get('input[formControlName=firstName]').type("Alice")
        cy.get('input[formControlName=lastName]').type("Dupont")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}`)
   
        cy.wait('@register');
        cy.url().should('include', '/login')
    })

    it('Incomplete Register form', () => {
        cy.title().should('equal', 'Register')

        cy.get('input[formControlName=email]').type("newyoga@studio.com")
        cy.get('input[formControlName=firstName]').type("Alice")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}`)

        cy.get('button[type="submit"]').should('be.disabled')
    })

    it('Register fails when API returns error', () => {
        cy.intercept('POST', '/api/auth/register', {
          statusCode: 400,
          body: { message: "Registration failed!" }
        }).as('registerFail');
      
        cy.get('input[formControlName=email]').type("newyoga@studio.com");
        cy.get('input[formControlName=firstName]').type("Alice");
        cy.get('input[formControlName=lastName]').type("Dupont");
        cy.get('input[formControlName=password]').type(`test!1234{enter}`);
      
        cy.wait('@registerFail');
        cy.get('span.error').should('be.visible').and('contain.text', 'An error occurred');
      });
      
})