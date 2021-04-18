package com.maliotis.library


import com.google.common.truth.Truth.assertThat
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun assetAddition() {
        val addition = 2 + 2
        assertThat(addition).isEqualTo(4)
    }
}
