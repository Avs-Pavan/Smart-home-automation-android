package com.example.shas

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shas.databinding.ActivityMainBinding
import timber.log.Timber
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var recorder: MediaRecorder
    private lateinit var output: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        val cw = ContextWrapper(this)
        val fullPath = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString()
        val directory: File? = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

        output = fullPath + "/" + System.currentTimeMillis().toString() + "recording.mp3"
        recorder = MediaRecorder()

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder.setAudioSamplingRate(16000)
        recorder.setOutputFile(output)

        binding.record.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        val permissions = arrayOf(
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        ActivityCompat.requestPermissions(this, permissions, 0)
                    } else {
                        startRecording()
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    stopRecording()
                }
            }
            false
        })
    }

    private fun stopRecording() {
        try {
            recorder.stop()
            recorder.reset()
            recorder.release()
            Timber.e("Stop recording")
            Timber.e(output)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun startRecording() {
        try {
            recorder.prepare()
            recorder.start()
            Timber.e("Start recording")
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}