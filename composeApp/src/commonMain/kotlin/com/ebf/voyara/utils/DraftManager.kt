package com.ebf.voyara.utils

import com.ebf.voyara.data.TripDraft
import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json

/**
 * Manages saving and retrieving trip drafts from local storage
 */
class DraftManager(private val settings: Settings) {

    companion object {
        private const val DRAFTS_KEY = "trip_drafts"
    }

    /**
     * Save a new draft or update an existing one
     */
    fun saveDraft(draft: TripDraft): Boolean {
        return try {
            val currentDrafts = getAllDrafts().toMutableList()

            // Check if draft with same ID exists and remove it
            val existingIndex = currentDrafts.indexOfFirst { it.id == draft.id }
            if (existingIndex != -1) {
                currentDrafts[existingIndex] = draft.copy(updatedAt = kotlin.time.Clock.System.now().toEpochMilliseconds())
            } else {
                currentDrafts.add(draft)
            }

            // Save to settings
            val draftsJson = Json.encodeToString(currentDrafts)
            settings.putString(DRAFTS_KEY, draftsJson)

            println("DraftManager: Draft saved successfully - ID: ${draft.id}, Name: ${draft.name}")
            true
        } catch (e: Exception) {
            println("DraftManager: Error saving draft: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Get all saved drafts
     */
    fun getAllDrafts(): List<TripDraft> {
        return try {
            val draftsJson = settings.getStringOrNull(DRAFTS_KEY)
            if (draftsJson.isNullOrBlank()) {
                println("DraftManager: No drafts found")
                emptyList()
            } else {
                val drafts = Json.decodeFromString<List<TripDraft>>(draftsJson)
                println("DraftManager: Retrieved ${drafts.size} drafts")
                drafts.sortedByDescending { it.updatedAt } // Most recent first
            }
        } catch (e: Exception) {
            println("DraftManager: Error retrieving drafts: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Get a specific draft by ID
     */
    fun getDraftById(id: String): TripDraft? {
        return try {
            getAllDrafts().find { it.id == id }
        } catch (e: Exception) {
            println("DraftManager: Error getting draft by ID: ${e.message}")
            null
        }
    }

    /**
     * Delete a specific draft
     */
    fun deleteDraft(id: String): Boolean {
        return try {
            val currentDrafts = getAllDrafts().toMutableList()
            val initialSize = currentDrafts.size
            val filteredDrafts = currentDrafts.filter { it.id != id }
            val removed = filteredDrafts.size < initialSize

            if (removed) {
                val draftsJson = Json.encodeToString(filteredDrafts)
                settings.putString(DRAFTS_KEY, draftsJson)
                println("DraftManager: Draft deleted successfully - ID: $id")
            } else {
                println("DraftManager: Draft not found - ID: $id")
            }

            removed
        } catch (e: Exception) {
            println("DraftManager: Error deleting draft: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Delete all drafts
     */
    fun deleteAllDrafts(): Boolean {
        return try {
            settings.remove(DRAFTS_KEY)
            println("DraftManager: All drafts deleted")
            true
        } catch (e: Exception) {
            println("DraftManager: Error deleting all drafts: ${e.message}")
            false
        }
    }

    /**
     * Get count of saved drafts
     */
    fun getDraftCount(): Int {
        return getAllDrafts().size
    }
}

/**
 * Composable to remember DraftManager instance
 */
@androidx.compose.runtime.Composable
fun rememberDraftManager(): DraftManager {
    return androidx.compose.runtime.remember {
        DraftManager(Settings())
    }
}

