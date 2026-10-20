package com.example

import com.example.data.model.AiProvider
import com.example.data.model.LanguageMode
import com.example.data.service.AiBrainRouter
import com.example.data.service.PcAutomationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    private val aiRouter = AiBrainRouter()
    private val pcManager = PcAutomationManager()

    @Test
    fun testLanguageDetectionEnglish() {
        val lang = aiRouter.detectLanguage("Open Google Chrome and search for machine learning")
        assertEquals(LanguageMode.ENGLISH, lang)
    }

    @Test
    fun testLanguageDetectionPakistaniUrduScript() {
        val lang = aiRouter.detectLanguage("کروم کھولیں اور فائل تلاش کریں")
        assertEquals(LanguageMode.PAKISTANI_URDU, lang)
    }

    @Test
    fun testLanguageDetectionRomanUrdu() {
        val lang = aiRouter.detectLanguage("Jarvis, Chrome kholo aur meri file dhoondo")
        assertEquals(LanguageMode.ROMAN_URDU, lang)
    }

    @Test
    fun testAiProvidersAndFreeModelsConfigured() {
        val gemini = AiProvider.GEMINI
        assertEquals("gemini-3.5-flash", gemini.defaultModel)
        assertTrue(gemini.isFree)

        val groq = AiProvider.GROQ
        assertTrue(groq.isFree)

        val openRouter = AiProvider.OPENROUTER_FREE
        assertTrue(openRouter.isFree)

        val offline = AiProvider.OFFLINE
        assertNotNull(offline.defaultModel)
    }

    @Test
    fun testAtoZCatalogComplete() {
        val catalog = pcManager.atozCatalog
        assertEquals(26, catalog.size)
        // Verify letters 'A' through 'Z'
        val letters = catalog.map { it.letter }
        assertEquals(('A'..'Z').toList(), letters)
    }
}
