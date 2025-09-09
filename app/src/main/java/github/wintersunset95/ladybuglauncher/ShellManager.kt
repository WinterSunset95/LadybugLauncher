package github.wintersunset95.ladybuglauncher

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.concurrent.thread

class ShellManager(private val outputTextView: TextView) {
    private val process: Process = Runtime.getRuntime().exec("sh")
    private val inputWriter = OutputStreamWriter(process.outputStream)
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        startReaderThread(process.inputStream, false)
        startReaderThread(process.errorStream, true)

        mainHandler.post({
            outputTextView.append("Welcome to Ladybug Launcher!\n")
            outputTextView.append("\nType 'help' for custom commands or 'exit' to quit the shell.")
            updatePrompt()
        })
    }

    private fun startReaderThread(stream: java.io.InputStream, isError: Boolean) {
        Thread {
            val reader = BufferedReader(InputStreamReader(stream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val finalLine = line
                mainHandler.post({
                    if (isError) {
                        outputTextView.append("\n[Error]: $finalLine")
                    } else {
                        outputTextView.append("\n$finalLine")
                    }
                })
            }
        }.start()
    }

    private fun updatePrompt() {
        thread {
            val promptProcess = Runtime.getRuntime().exec("sh -c pwd")
            val reader = BufferedReader(InputStreamReader(promptProcess.inputStream))
            val prompt = reader.readLine()
            reader.close()
            promptProcess.waitFor()

            mainHandler.post({
                outputTextView.append("\n[$prompt]> ")
            })
        }
    }

    fun execute(commandLine: String) {
        if (commandLine.isEmpty()) {
            mainHandler.post({
                outputTextView.append("\n$ ")
            })
            return
        }

        mainHandler.post({
            outputTextView.append("\n$ $commandLine")
        })

        val parts = commandLine.split(" ", limit = 2)
        val command = parts[0].lowercase()
        val arg = if (parts.size > 1) parts[1] else ""

        when (command) {
            "help" -> {
                mainHandler.post({
                    outputTextView.append("\n- Custom Commands: help, launch [app]")
                    outputTextView.append("\n- System Commands: ls, cd, etc.")
                    outputTextView.append("\n- Type 'exit' to quit the shell.")
                    updatePrompt()
                })
            }
            "launch" -> {}
            "exit" -> {}
            else -> {
                try {
                    inputWriter.write(commandLine + "\n")
                    inputWriter.flush()
                    mainHandler.post({ updatePrompt() })
                } catch (e: Exception) {
                    mainHandler.post({
                        outputTextView.append("\nError: ${e.message}")
                        updatePrompt()
                    })
                }
            }
        }
    }

    fun destroy() {
        try {
            inputWriter.write("exit\n")
            inputWriter.flush()
        } catch (e: Exception) {

        }

        try {
            inputWriter.close()
        } catch (e: Exception) {}

        process.destroy()
    }
}