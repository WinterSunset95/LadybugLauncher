package github.wintersunset95.ladybuglauncher

import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class ShellManager(private val outputTextView: TextView) {
    private val process: Process = Runtime.getRuntime().exec("sh")
    private val outputReader = BufferedReader(InputStreamReader(process.inputStream))
    private val errorReader = BufferedReader(InputStreamReader(process.errorStream))
    private val inputWriter = OutputStreamWriter(process.outputStream)

    init {
        readOutput()
        outputTextView.setText("\nType 'exit' to quit the shell.")
    }

    fun execute(command: String) {
        try {
            outputTextView.append("\n> $command")
            inputWriter.write(command + "\n")
            inputWriter.flush()
            readOutput()
        } catch (e: Exception) {
            outputTextView.append("\nError: ${e.message}")
        }
    }

   private fun readOutput() {
       val output = StringBuilder()
       while (outputReader.ready()) {
           output.append(outputReader.readLine()).append("\n")
       }
       while (errorReader.ready()) {
           output.append(errorReader.readLine()).append("\n")
       }
       if (output.isNotEmpty()) {
           outputTextView.append("\n" + output.toString().trim())
       }
   }

   fun destroy() {
        inputWriter.close()
        outputReader.close()
        errorReader.close()
        process.destroy()
    }
}