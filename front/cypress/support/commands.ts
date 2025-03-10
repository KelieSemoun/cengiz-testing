// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************
// declare namespace Cypress {
//   interface Chainable<Subject = any> {
//     customCommand(param: any): typeof customCommand;
//   }
// }
//
// function customCommand(param: any): void {
//   console.warn(param);
// }
//
// NOTE: You can use it like so:
// Cypress.Commands.add('customCommand', customCommand);
//
// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add('login', (admin = true) => { 
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      id: 1,
      username: 'testuser',
      admin: admin
    }
  });

  cy.visit('/login');

  cy.get('input[formControlName=email]').type('yoga@studio.com');
  cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');

  cy.url().should('include', '/sessions'); 
});

Cypress.Commands.add('loginAndSetupSessionDetail', (admin = true) => {
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

  cy.login(admin);

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
  }).as('getSession');

  cy.intercept('GET', '/api/teacher/201', {
    statusCode: 200,
    body: {
      id: 201,
      firstName: 'Alice',
      lastName: 'Brown',
      createdAt: '2024-06-01T09:00:00.000Z',
      updatedAt: '2024-07-01T10:30:00.000Z'
    }
  }).as('getTeacher');

  cy.wait('@getSessions');

  cy.get('.item').should('have.length', 2);

  cy.get('.item').first().within(() => {
    cy.get('button').contains('Detail').click();
  });

  cy.wait('@getSession'); 
  cy.wait('@getTeacher'); 
})
