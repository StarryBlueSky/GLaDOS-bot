package jp.nephy.glados.component.audio

import com.google.cloud.speech.v1beta1.RecognitionAudio
import com.google.cloud.speech.v1beta1.RecognitionConfig
import com.google.cloud.speech.v1beta1.SpeechClient
import com.google.protobuf.ByteString
import java.nio.file.Files
import java.nio.file.Paths


class GoogleCloudSpeech {
    fun recognize() {
        val speech = SpeechClient.create()

        // The path to the audio file to transcribe
        val fileName = "./resources/audio.raw"

        // Reads the audio file into memory
        val path = Paths.get(fileName)
        val data = Files.readAllBytes(path)
        val audioBytes = ByteString.copyFrom(data)

        // Builds the sync recognize request
        val config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRate(16000)
                .build()
        val audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build()

        // Performs speech recognition on the audio file
        val response = speech.syncRecognize(config, audio)
        val results = response.resultsList

        results.forEach {
            it.alternativesList.forEach { alternative ->
                println("Transcription: ${alternative.transcript}\n")
            }
        }
        speech.close()
    }
}