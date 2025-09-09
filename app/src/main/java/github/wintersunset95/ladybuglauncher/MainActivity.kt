package github.wintersunset95.ladybuglauncher

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var outputTextView: TextView
    private lateinit var inputEditText: EditText
    private lateinit var shellManager: ShellManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        outputTextView = findViewById(R.id.output_text_view)
        inputEditText = findViewById(R.id.input_edit_text)

        shellManager = ShellManager(outputTextView)
        shellManager.execute("cd /storage/emulated/0")

        inputEditText.setOnEditorActionListener({ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val command = inputEditText.text.toString().trim()
                inputEditText.setText("")

                if (command.equals("exit", ignoreCase =  true)) {
                    finish()
                    shellManager.destroy()
                } else {
                    shellManager.execute(command)
                }

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}