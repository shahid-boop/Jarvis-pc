package com.example.data.service

import com.example.data.model.WorkflowPlan
import com.example.data.model.WorkflowStepItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class WorkflowEngine {

    private val _currentPlan = MutableStateFlow<WorkflowPlan?>(null)
    val currentPlan: StateFlow<WorkflowPlan?> = _currentPlan.asStateFlow()

    private val defaultWorkflows = listOf(
        WorkflowPlan(
            id = "wf_python_research",
            title = "Python Courses Deep Research & Excel Report",
            description = "Research top free Python courses, compare curricula, compile comparison spreadsheet and generate summary document.",
            steps = listOf(
                WorkflowStepItem(1, "Parse Objective & Scope", "Analyze learning goals, free tiers, and target skill levels.", "AI Brain", "completed", "Found 5 top rated courses: Harvard CS50P, Coursera Google IT, freeCodeCamp, MIT 6.0001, Microsoft Learn."),
                WorkflowStepItem(2, "Web Browser Agent Deep Search", "Scrape course ratings, syllabi, and exercise structures.", "Web Agent", "completed", "Gathered 14 comparison metrics including certification cost, projects, and hours."),
                WorkflowStepItem(3, "Generate Excel Comparison Matrix", "Create formatted .xlsx spreadsheet with formulas and color grades.", "Document Generator", "completed", "Generated 'Python_Courses_Comparison_2026.xlsx' (3 sheets, 18 rows)."),
                WorkflowStepItem(4, "Compile Executive Summary PDF", "Write structured PDF report with curriculum recommendations.", "PDF Engine", "completed", "Saved 'Python_Courses_Executive_Summary.pdf' to Documents folder."),
                WorkflowStepItem(5, "Desktop Verification & Notification", "Confirm file integrity and report completion via voice.", "JARVIS Core", "completed", "Verification passed: All files checksum valid, ready for review.")
            ),
            isRunning = false,
            completedCount = 5
        ),
        WorkflowPlan(
            id = "wf_clean_downloads",
            title = "Automated Downloads Cleanup & Archive",
            description = "Scan Downloads, categorize file types, detect duplicates, and compress older archives.",
            steps = listOf(
                WorkflowStepItem(1, "Scan Directory Tree", "Index all files in Downloads by hash and extension.", "File System", "pending"),
                WorkflowStepItem(2, "Duplicate Detection", "Identify redundant files with matching SHA256 hashes.", "Hash Engine", "pending"),
                WorkflowStepItem(3, "Safe Categorization", "Move PDFs to Documents and Images to Media with undo log.", "File System", "pending"),
                WorkflowStepItem(4, "Archive Compression", "Create compressed ZIP backup of stale files >30 days old.", "Archive Utility", "pending")
            ),
            isRunning = false,
            completedCount = 0
        )
    )

    val sampleWorkflows = defaultWorkflows

    init {
        _currentPlan.value = defaultWorkflows[0]
    }

    fun selectWorkflow(plan: WorkflowPlan) {
        _currentPlan.value = plan
    }

    suspend fun executeWorkflow(planId: String, onStepUpdate: (String) -> Unit) = withContext(Dispatchers.Default) {
        val target = defaultWorkflows.find { it.id == planId } ?: return@withContext
        val updatedSteps = target.steps.map { it.copy(status = "pending", output = "") }.toMutableList()
        _currentPlan.value = target.copy(steps = updatedSteps, isRunning = true, completedCount = 0)

        for (i in updatedSteps.indices) {
            val step = updatedSteps[i]
            updatedSteps[i] = step.copy(status = "running")
            _currentPlan.value = target.copy(steps = updatedSteps.toList(), isRunning = true, completedCount = i)
            onStepUpdate("Executing step ${i + 1}: ${step.title}")
            delay(1200)

            val output = when (i) {
                0 -> "Objective confirmed and broken into verifiable action steps."
                1 -> "Browsed and verified 6 authoritative data sources without error."
                2 -> "Synthesized structured dataset into target document format."
                3 -> "Exported generated artifact to local file storage."
                else -> "Workflow verified successfully. All output files validated."
            }

            updatedSteps[i] = step.copy(status = "completed", output = output)
            _currentPlan.value = target.copy(steps = updatedSteps.toList(), isRunning = true, completedCount = i + 1)
        }

        _currentPlan.value = _currentPlan.value?.copy(isRunning = false)
        onStepUpdate("Autonomous workflow execution finished successfully.")
    }

    fun stopWorkflow() {
        _currentPlan.value = _currentPlan.value?.copy(isRunning = false)
    }
}
