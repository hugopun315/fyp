package com.example.fyp

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp.adapter.MessageAdapter
import com.example.fyp.databinding.ActivityChatGptapiBinding

class chatGPTAPI : AppCompatActivity() {

    private lateinit var binding: ActivityChatGptapiBinding
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("chatGPTAPI", "onCreate called")
        binding = ActivityChatGptapiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MessageAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.messages.observe(this, Observer { messages ->
            Log.d("chatGPTAPI", "Messages updated: $messages")
            adapter.submitList(messages)
        })

        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            Log.d("chatGPTAPI", "Send button clicked with message: $message")
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.messageInput.text.clear()
            }
        }
    }
}