package org.lecomte.crackingcodeinterview.ch01;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IsUniqueTest {
    private String uniqueChars = "abcdefg";
    private String nonUniqueChars = "abcdefgb";

    @Test
    void isUnique1ShouldReturnProperResult() {
        assertThat(IsUnique.isUniqueHashmap(uniqueChars))
                .as("should return true when all chars are unique")
                .isEqualTo(true);

        assertThat(IsUnique.isUniqueHashmap(nonUniqueChars))
                .as("should return false when a char is not unique")
                .isEqualTo(false);
    }

    @Test
    void isUnique2ShouldReturnProperResult() {
        assertThat(IsUnique.isUniqueBruteForce(uniqueChars))
                .as("should return true when all chars are unique")
                .isEqualTo(true);

        assertThat(IsUnique.isUniqueBruteForce(nonUniqueChars))
                .as("should return false when a char is not unique")
                .isEqualTo(false);
    }
}
