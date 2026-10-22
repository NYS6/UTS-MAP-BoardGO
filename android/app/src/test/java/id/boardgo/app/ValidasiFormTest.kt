package id.boardgo.app

import id.boardgo.app.ui.viewmodel.BoardGOViewModel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test sederhana untuk logika validasi form (MVVM).
 * Menjalankan: ./gradlew testDebugUnitTest
 */
class ValidasiFormTest {

    @Test
    fun emailValid_diterima() {
        assertTrue(BoardGOViewModel.isValidEmail("dimas@boardgo.id"))
        assertTrue(BoardGOViewModel.isValidEmail("a.b+c@domain.co.uk"))
    }

    @Test
    fun emailTidakValid_ditolak() {
        assertFalse(BoardGOViewModel.isValidEmail("dimas@boardgo"))   // tanpa TLD
        assertFalse(BoardGOViewModel.isValidEmail("dimas@"))          // tanpa domain
        assertFalse(BoardGOViewModel.isValidEmail("dimas.boardgo.id"))// tanpa @
        assertFalse(BoardGOViewModel.isValidEmail(""))                // kosong
    }
}
