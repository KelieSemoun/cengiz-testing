package com.openclassrooms.starterjwt.payload.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessageResponseTest {

    @Test
    void testMessageResponseConstructorAndGetter() {
        // Arrange & Act
        MessageResponse messageResponse = new MessageResponse("Success");

        // Assert
        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse.getMessage()).isEqualTo("Success");
    }

    @Test
    void testMessageResponseSetter() {
        // Arrange
        MessageResponse messageResponse = new MessageResponse("Initial Message");

        // Act
        messageResponse.setMessage("Updated Message");

        // Assert
        assertThat(messageResponse.getMessage()).isEqualTo("Updated Message");
    }
}
