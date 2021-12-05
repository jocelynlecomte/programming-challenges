package org.lecomte.euler.rating5;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MultiplesOf3Or5Test {
    private MultiplesOf3Or5 sut = new MultiplesOf3Or5();

    @Test
    void testBruteForce() {
        assertThat(sut.bruteForce(9)).isEqualTo(23);
        assertThat(sut.bruteForce(999)).isEqualTo(233168);
    }

    @Test
    void testMathMethod() {
        assertThat(sut.mathMethod(9)).isEqualTo(23);
        assertThat(sut.mathMethod(999)).isEqualTo(233168);
    }
}
