package com.zrh.log

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.zrh.log.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnCrash.setOnClickListener {
            throw java.lang.IllegalStateException("crash!")
        }

        mBinding.btnZip.setOnClickListener {
            zipLogs()
        }
    }

    private fun zipLogs() {
        val loading = LoadingDialog(this)
        flow<File> {
            val printer = Logger.getPrinter(DiskPrinter::class.java)
            val dir = File(cacheDir, "zip_logs")
            val fileName = "${System.currentTimeMillis()}"
            val output = printer.zipLatestLogs(dir, fileName, 5 * 1024 * 1024)
            emit(output)
        }.flowOn(Dispatchers.IO)
            .onStart { loading.show() }
            .onEach {
                mBinding.tvOutput.text = "Output: ${it.absolutePath}"
            }
            .catch {
                Toast.makeText(this@MainActivity, "Error:${it}", Toast.LENGTH_SHORT).show()
            }
            .onCompletion { loading.dismiss() }
            .launchIn(lifecycleScope)
    }
}